/*
<html>
<applet code="TreeDrawer.class" width="1300" height="1300"></applet>
</html>
*/

import java.util.ArrayList;
import java.util.LinkedList;

import javax.swing.JApplet;
import java.awt.Graphics;
import java.awt.Color;

public class TreeDrawer extends JApplet implements Runnable {

    private final int WINDOW_WIDTH = 1300, WINDOW_HEIGHT = 775;

    private boolean running = false;

    private Thread ticker;
    
    private Game game;
    private LinkedList<GameState> winningPath;

    private final int BOX_BUFFER = 10, LAYER_BUFFER = 30;


    public void init() {
	game = new Game(9);
	game.playGame();

	winningPath = new LinkedList<GameState>();
	for(Tree<GameState> tree: game.getWinningPath()) {
	    winningPath.add(tree.getState());
	}
    }

    public void start() {
	if(ticker == null || !ticker.isAlive()) {
	    running = true;
	    ticker = new Thread(this);
	    ticker.setPriority(Thread.MIN_PRIORITY);
	    ticker.start();
	}

	resize(WINDOW_WIDTH, WINDOW_HEIGHT);
	requestFocusInWindow();
    }

    public void run() {
	repaint();
    }

    public void paint(Graphics g) {

	ArrayList<Tree<GameState>> flat = this.game.getRoot().flatten();
	ArrayList<GameStateDrawable> drawables = new ArrayList<GameStateDrawable>();

	int maxDepth = flat.get(flat.size() - 1).getState().getDepth();
	int boxHeight = (WINDOW_HEIGHT - ((maxDepth + 2) * LAYER_BUFFER)) / (maxDepth + 1);
	//while((maxDepth + 1) * boxHeight + ) {

	//}

	int curDepth = 0;

	int curY = LAYER_BUFFER;

	ArrayList<GameState> curLayer = new ArrayList<GameState>();
	for(int index = 0; index < flat.size(); index++) {

	    Tree<GameState> curTree = flat.get(index);
	    GameState curState = curTree.getState();
	    if(curState.getVal() == 0) {
		this.game.determineVal(curTree);
	    }

	    
	    if(curState.getDepth() == curDepth) {
		curLayer.add(curState);
	    } else {

		int layerSize = curLayer.size();
		int boxWidth = (WINDOW_WIDTH - ((layerSize + 1) * BOX_BUFFER)) / layerSize;

		int curX = BOX_BUFFER;

		for(GameState state: curLayer) {
		    GameStateDrawable gsd = new GameStateDrawable(state, 
								  curX, curY, 
								  boxWidth, boxHeight);
		    if(this.winningPath.contains(state)) {
			gsd.setFillColor(Color.GREEN);
		    }
		    gsd.draw(g);
		    drawables.add(gsd);
		    curX += boxWidth + BOX_BUFFER;
		}

		curY += boxHeight + LAYER_BUFFER;
		curDepth = curState.getDepth();
		curLayer = new ArrayList<GameState>();
		curLayer.add(curState);
	    }
	}

	if(curLayer.size() > 0) {
	    int layerSize = curLayer.size();
	    int boxWidth = (WINDOW_WIDTH - ((layerSize + 1) * BOX_BUFFER)) / layerSize;
	    int curX = BOX_BUFFER;
	    
	    for(GameState state: curLayer) {
		GameStateDrawable gsd = new GameStateDrawable(state, 
							      curX, curY, 
							      boxWidth, boxHeight);
		if(this.winningPath.contains(state)) {
		    gsd.setFillColor(Color.GREEN);
		}
		gsd.draw(g);
		drawables.add(gsd);
		curX += boxWidth + BOX_BUFFER;
	    }
	}

	g.setColor(Color.BLACK);

	for(int i = 0; i < drawables.size(); i++) {
	    for(int j = 0; j < drawables.size(); j++) {
		GameStateDrawable d1 = drawables.get(i);
		GameStateDrawable d2 = drawables.get(j);

		GameState s1 = d1.getState();
		GameState s2 = d2.getState();

		if(s1.hasChild(s2) && s2.getDepth() == s1.getDepth() + 1) {
		    g.drawLine(d1.getX() + d1.getWidth() / 2, 
			       d1.getY() + d1.getHeight(),
			       d2.getX() + d2.getWidth() / 2,
			       d2.getY());
		}
	    }
	}

	
    }

    public void stop() {
	running = false;
    }

}