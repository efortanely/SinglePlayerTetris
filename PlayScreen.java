import java.applet.Applet;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class PlayScreen extends Applet implements KeyListener{
	int blockWidth = 25, timer, delay = 15, linesCleared;
	Color[][] grid;
	Tetromino tetromino, ghost;
	Color bg = Color.decode("#FFF67C");
	
	@Override
	public void init(){
		addKeyListener(this);
		newGame();
	}
	
	public void newGame() {
		grid = new Color[getHeight()/blockWidth][getWidth()/blockWidth];
		Tetromino.initializeGame(getWidth(), blockWidth);
		tetromino = Tetromino.getNextPiece();
		linesCleared = 0;
		delay = 15;
	}
	
	@Override
	public final void paint(Graphics go) {
		Graphics2D g = (Graphics2D) go;
		long z = System.currentTimeMillis();
		loop((Graphics2D)g);
		z = System.currentTimeMillis() - z;
		try {
			z = (40 - z > 0) ? 40 - z : 0;
			Thread.sleep(z);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		repaint();
	}
	
	public void loop(Graphics2D g){
		if(timer%delay==0) if(!tetromino.legalMoveDown(grid)) updateGrid();
		
		g.setColor(bg);
		g.fillRect(0, 0, getWidth(), getHeight());
		
		tetromino.drawShadow(g, bg);
		
		for(int r = 0; r < grid.length; r++)
		for(int c = 0; c < grid[0].length; c++)
			if(grid[r][c] != null){
				Tetromino.drawBlockShadow(g, c, r, bg);
				Tetromino.drawBlock(g, c, r, grid[r][c], false);
			}
		
		ghost = tetromino.ghost();
		while(ghost.legalMoveDown(grid)){ }
		
		ghost.draw(g, true);
		tetromino.draw(g, false);
		
		timer++;
	}

	private void updateGrid(){
		tetromino.embed(grid);
		tetromino = Tetromino.getNextPiece();
		if(tetromino.overlapping(grid)) newGame();
		checkForFullLines();
	}
	
	public void checkForFullLines(){
		for(int r = 0; r < grid.length; r++){
			boolean fullRow = true;
			for(int c = 0; c < grid[0].length; c++) if(grid[r][c] == null) fullRow = false;
			if(fullRow){
				linesCleared++;
				if(linesCleared%3==0 && delay>1) delay--;
				for(int back = r; back > 0; back--) grid[back] = grid[back-1].clone();
			}
		}
		repaint();
	}
	
	@Override
	public void keyPressed(KeyEvent e) { 
		switch(e.getKeyCode()){
		case KeyEvent.VK_W:
		case KeyEvent.VK_UP:
			tetromino.rotate("clockwise");
			if(tetromino.isOutOfBounds(grid, "any") || tetromino.overlapping(grid)) tetromino.rotate("counterClockwise");
			break;
		case KeyEvent.VK_A:
		case KeyEvent.VK_LEFT:
			tetromino.move("left");
			if(tetromino.isOutOfBounds(grid, "left") || tetromino.overlapping(grid)) tetromino.move("right");
			break;
		case KeyEvent.VK_D:
		case KeyEvent.VK_RIGHT: 
			tetromino.move("right");  
			if(tetromino.isOutOfBounds(grid, "right") || tetromino.overlapping(grid)) tetromino.move("left");
			break;
		case KeyEvent.VK_DOWN:
		case KeyEvent.VK_S:
			tetromino.legalMoveDown(grid);
			break;
		case KeyEvent.VK_SPACE:
			while(tetromino.legalMoveDown(grid)){ }
			updateGrid();	
			break;
		default:
	}
		repaint();
	}
	
	Image boi;
	Graphics bog;
	
	//http://www.jguru.com/article/client-side/double-buffering.html
	@Override
	public final void update(java.awt.Graphics g) {
		boi = createImage(getWidth(), getHeight());
		bog = boi.getGraphics();
		bog.setColor(getBackground());
		bog.fillRect(0, 0, getWidth(), getHeight());
		bog.setColor(getForeground());
		paint(bog);
		g.drawImage(boi, 0, 0, this);
	}
	
	public void keyReleased(KeyEvent e){} public void keyTyped(KeyEvent e){}
}
