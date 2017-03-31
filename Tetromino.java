import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.util.Random;

public class Tetromino{
	private boolean[][] tetromino;
	private static int screenWidth, blockWidth, r, c = 1;
	private Color color;
	private int[] gridLocation = new int[2];
	static Area contour, highlight, innerSquare;

	public Tetromino(String pieceType){		
		switch(pieceType){
			case "i": 
				tetromino = new boolean[][]{
					{false, false, true, false},
					{false, false, true, false},
					{false, false, true, false},
					{false, false, true, false} };
				color = Color.decode("#A73208");
				break;
			case "j": 
				tetromino = new boolean[][]{
					{true, false, false},
					{true, true, true},
					{false, false, false} };
				color = Color.decode("#100B33");
				break;
			case "l": 
				tetromino = new boolean[][]{
					{false, false, true},
					{true, true, true},
					{false, false, false} };
				color = Color.decode("#7E1710");
				break;
			case "o": 
				tetromino = new boolean[][]{
					{true, true},
					{true, true} };
				color = Color.decode("#4C2506");
				break;
			case "s": 
				tetromino = new boolean[][]{
					{false, true, true},
					{true, true, false},
					{false, false, false} };
				color = Color.decode("#B57132");
				break;
			case "t": 
				tetromino = new boolean[][]{
					{false, true, false},
					{true, true, true},
					{false, false, false} };
				color = Color.decode("#B16511");
				break;
			case "z": 
				tetromino = new boolean[][]{
					{true, true, false},
					{false, true, true},
					{false, false, false} };
				color = Color.decode("#DAB519");
				break;
		}		
		gridLocation[c] = (screenWidth/blockWidth)/2 - tetromino[0].length/2;
	}
	
	public Tetromino(boolean[][] t, int[] g, Color c){
		tetromino = t.clone(); gridLocation = g.clone(); color = c;
	}
	
	public static void initializeGame(int w, int bw){ 
		screenWidth = w; blockWidth = bw; 
		innerSquare = new Area(new Rectangle2D.Double(1/6.0*blockWidth,1/6.0*blockWidth,4/6.0*blockWidth,4/6.0*blockWidth));
		contour = new Area(new Polygon(new int[]{0,blockWidth,blockWidth}, new int[]{blockWidth,0,blockWidth}, 3));
		highlight = new Area(new Polygon(new int[]{0,blockWidth,0}, new int[]{0,0,blockWidth}, 3));
		contour.subtract(innerSquare);
		highlight.subtract(innerSquare);
	}
	
	public static Tetromino getNextPiece(){
		String[] pieceBank = {"l", "s", "t", "z", "j", "i", "o"};
		return new Tetromino(pieceBank[new Random().nextInt(pieceBank.length)]);
	}
	
	public static void drawBlockShadow(Graphics g, int x, int y, Color bg){		
		g.setColor(bg.darker().darker());
		g.fillRect(x*blockWidth+8, y*blockWidth+8, blockWidth, blockWidth);
	}
	
	public static void drawBlock(Graphics2D g, int x, int y, Color cc, boolean isGhost){
		AffineTransform af = new AffineTransform();
		af.translate(x*blockWidth, y*blockWidth);
		
		g.setColor(cc);
		if (isGhost) innerSquare = new Area(new Rectangle2D.Double(1/6.0*blockWidth,1/6.0*blockWidth,4/6.0*blockWidth,4/6.0*blockWidth));
		if (!isGhost) innerSquare = new Area(new Rectangle2D.Double(0,0,blockWidth,blockWidth));
		g.fill(innerSquare.createTransformedArea(af));
		
		g.setColor(cc.darker());
		g.fill(contour.createTransformedArea(af));		
		
		g.setColor(cc.brighter());
		g.fill(highlight.createTransformedArea(af));
	}
	
	public void draw(Graphics go, boolean isGhost) {
		Graphics2D g = (Graphics2D)go.create();
		g.translate(gridLocation[c]*blockWidth,gridLocation[r]*blockWidth);
		
		for(int row = 0; row < tetromino.length; row++)
		for(int column = 0; column < tetromino[0].length; column++)
			if(tetromino[row][column]) drawBlock(g,column,row, color, isGhost);
		
		g.dispose();
	}
	
	public void drawShadow(Graphics go, Color backgroundColor) {
		Graphics2D g = (Graphics2D)go.create();
		g.translate(gridLocation[c]*blockWidth,gridLocation[r]*blockWidth);
		
		for(int row = 0; row < tetromino.length; row++)
		for(int column = 0; column < tetromino[0].length; column++)
			if(tetromino[row][column]) drawBlockShadow(g,column,row, backgroundColor);
		
		g.dispose();
	}
	
	public void embed(Color[][] grid) {
		for(int pieceRow = 0; pieceRow < tetromino.length; pieceRow++)
		for(int pieceCol = 0; pieceCol < tetromino[0].length; pieceCol++)
			if(tetromino[pieceRow][pieceCol]) grid[pieceRow + gridLocation[r]][pieceCol + gridLocation[c]] = color;
	}
	
	public boolean overlapping(Color[][] grid) {
		for(int row = 0; row < grid.length; row++){
		for(int column = 0; column < grid[0].length; column++){
			int localRow = row-gridLocation[r], localColumn = column-gridLocation[c];
			if(grid[row][column]!=null  && localRow<tetromino.length && localColumn<tetromino[r].length 
			&& localRow>=0 && localColumn>=0 && tetromino[localRow][localColumn]) 
				return true;
		}
		}
		
		return false;
	}
	
	public boolean isOutOfBounds(Color[][] grid, String side){
		switch(side){
			case "bottom":
				int localRow = grid.length-gridLocation[r];
				if(localRow > tetromino.length-1) return false;
				for(boolean c : tetromino[localRow]) if(c) return true;
				break;
			case "left":
				int localColumn = -1-gridLocation[c];
				if(localColumn < 0) return false;
				for(boolean[] r : tetromino) if(r[localColumn]) return true;
				break;
			case "right":
				localColumn = grid[0].length-gridLocation[c];
				if(localColumn > tetromino[0].length-1) return false;
				for(boolean[] r : tetromino) if(r[localColumn]) return true;
				break;
			case "any":
				if(isOutOfBounds(grid, "left") || isOutOfBounds(grid, "right") || isOutOfBounds(grid, "bottom")) return true;
				break;
		}
		return false;
	}
	
	public void rotate(String direction){
		boolean[][] rotated = new boolean[tetromino[r].length][tetromino.length];
		
		for(int r=0; r<rotated.length; r++)
		for(int c=0; c< rotated[0].length; c++)
			rotated[r][c] = (direction.equals("clockwise"))? tetromino[rotated[0].length-1-c][r]: tetromino[c][rotated.length-1-r];
		
		tetromino = rotated;
	}
	
	public void move(String direction){
		switch(direction){
		case "up": gridLocation[r] -= 1;    break;
		case "down": gridLocation[r] += 1;  break;
		case "left": gridLocation[c] -= 1;  break;
		case "right": gridLocation[c] += 1; break;
		}
	}
	
	public boolean legalMoveDown(Color[][] grid){
		move("down");
		if(isOutOfBounds(grid, "bottom") || overlapping(grid)){
			move("up");
			return false;
		}
		return true;
	}
	
	//ahh it's a spoopy ghoost
	public Tetromino ghost(){
		return new Tetromino(tetromino, gridLocation, new Color(color.getRed(),color.getGreen(),color.getBlue(),80));
	}
}
