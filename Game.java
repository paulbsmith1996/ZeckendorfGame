/**
 * @author - Paul Baird-Smith 2017-2018
 * ppb366@cs.utexas.edu
 *
 * A Game object can be constructed using a number n, and when played using the
 * playGame() method, plays through the nth Zeckendorf Decomposition game, according
 * to (Epstein 2018).
 *
 * The Game object wraps the Tree of GameStates that represent all possible moves to
 * play in the nth ZD game.
 *
 * To determine the winner of the nth game, construct the Game using Game(n), call 
 * playGame() on the game, and then call determineVal(), passing the root of the
 * Game Tree as an argument. This recursively computes the winner of the game.
 *
 * As not all nodes need to have their winner value computed, determineVal() doesn't
 * assign a winner value to each node in the game Tree. Therefore, to compute the
 * winner value of an intermediate node, it may be necessary to call determineVal()
 * on all nodes in the game tree (this is not recommended if several games are being
 * played as this is a costly process).
 *
 */

import java.util.ArrayList;
import java.util.LinkedList;

public class Game {

    /**
     * The starting number of 1s in the fibonacci decomposition game
     */
    private int n;

    /**
     * The initial GameState of the Game object
     */
    private GameState initGameState;

    /**
     * Root tree of the Game, wrapping initGameState
     */
    private Tree<GameState> topRoot;

    /**
     * Holds a list of the GameStates in a winning strategy for Player 2
     */
    private LinkedList<Tree<GameState>> winningPath;

    /**
     * Constructor for a Game on n 1s
     *
     * @param n - The number of 1s in the starting decomposition of the fibonacci game
     */
    public Game(int n) {

	// Initialize all global values
	this.n = n;
	this.initGameState = new GameState(n);
	this.topRoot = new Tree<GameState>(initGameState);
	this.winningPath = new LinkedList<Tree<GameState>>();
    }


    //==================================
    // GETTERS AND SETTERS
    //==================================

    /**
     * Getter and setter for the initial number of 1s
     */
    public int getN() { return this.n; }
    public void setN(int n) { this.n = n; }


    /**
     * Getter and setter for the initial GameState of the Game
     */
    public GameState getInitState() { return this.initGameState; }
    public void setInitState(GameState state) { this.initGameState = state; }


    /**
     * Getter and setter for the root tree of the Game
     */
    public Tree<GameState> getRoot() { return this.topRoot; }
    public void setRoot(Tree<GameState> root) { this.topRoot = root; }

    /**
     * Getter for the list of winning paths. No setter.
     */
    public LinkedList<Tree<GameState>> getWinningPath() { return this.winningPath; }



    //==================================
    // GAME RUNNING METHODS
    //==================================

    /**
     * Generates the game tree for the Game object
     *
     * @return - 1 for a Player 1 victory, -1 for a Player 2 victory
     */
    public int playGame() {

	// Maintain the list of GameState Trees to explore
        LinkedList<Tree<GameState>> toExplore = new LinkedList<Tree<GameState>>();
        toExplore.add(this.topRoot);

	// Maintain the current layer of Trees. The GameStates of all Trees in 
	// curLayer have the same depth
	ArrayList<Tree<GameState>> curLayer = new ArrayList<Tree<GameState>>();

	// Keep track of important statistics
        int curDepth = 0;
        int numNodes = 1;
        int numEnds = 0;

	// Loop through all the GameState Trees to explore
        while(toExplore.size() > 0) {

            // Get the current state and tree root
            Tree<GameState> root = toExplore.poll();
	    GameState state = root.getState();

            // Update the max number of moves played if necessary and update the
	    // current layer of trees
            if(state.getDepth() > curDepth) {
                curDepth = state.getDepth();
		curLayer = new ArrayList<Tree<GameState>>();
            }

            // Check if state is terminal and update queue
            if(!state.isEndState()) {

		// Generate all possible subsequent states from the current state
                ArrayList<GameState> children = state.genChildren();

		// Loop through all subsequent states
                for(GameState child: children) {

		    // Determine if state has been added to the current layer of 
		    // Trees  before
                    boolean isNew = true;
                    for(Tree<GameState> t: curLayer) {
			GameState tState = t.getState();
                        if(!tState.isDifferent(child) ) {

			    // If the state is already in the current layer, simply
			    // update its parents list
                            isNew = false;
			    t.addParent(root);

                        }
                    }

		    // If it is not in the list of Trees in the current layer, add child
		    // to the  list of trees to explore
                    if(isNew) {

			// Wrap the GameState in a Tree and add child to current state
			Tree<GameState> newChild = new Tree<GameState>(child);

			// Increase the number of explored nodes
                        numNodes++;

			// Add newChild to all appropriate lists
                        toExplore.add(newChild);
			curLayer.add(newChild);

			// Add newChild as a child to the root
			root.addChild(newChild);
                    }

                }
            } else {
		// If state is terminal, increase the number of terminal states
                numEnds++;
            }

        }

        // Print interesting information
        
        System.out.println("All games played after " + curDepth + " moves.");
        System.out.println("Explored " + numNodes + " nodes.");
        System.out.println("Winner is: " + determineVal(this.topRoot));
        
	//System.out.println("Finding value");
        return determineVal(this.topRoot);
    }

    
    /**
     * Determines the winner of the Game recursively, starting with the initial
     * GameState.
     *
     * @return - 1 is player 1 wins, -1 if player 2 wins
     */
    public int determineVal(Tree<GameState> tree) {

	// Get the GameState of the tree 
	GameState state = tree.getState();

	if(state.getVal() != 0) {
	    return state.getVal();
	}

	// Get all the children of the GameState
        ArrayList<Tree<GameState>> children = tree.getChildren();

	// Initialize max and min to small and large value respectively
        int max = -1;
        int min = 1;
	
        if(children.size() == 0) {

	    // The current GameState is terminal
            if(state.getDepth() % 2 == 1){
		// Player 1 wins if an odd number of moves have been played
                state.setVal(1);
                return 1;

            } else {
		// Player 2 wins if an even number of moves have been played.
		// Update values and also the winning path 
		if(this.winningPath.size() == 0 
		   || this.winningPath.peek().getParents().contains(tree)) {
		    this.winningPath.addFirst(tree);
		}
                state.setVal(-1);
                return -1;

            }
        } else {

	    // Current GameState is not terminal
            if(state.getDepth() % 2 == 0) {

		// It is player 1's turn

		// Check assigned values before doigna recursive call, for speed
                for(Tree<GameState> child: children) {

		    if(child.getState().getVal() == 1) {
			state.setVal(1);
			return 1;
		    }
		}

		// Get the maximum over all possible subsequent GameStates 
                for(Tree<GameState> child: children) {
                    if(determineVal(child) == 1) {
			state.setVal(1);
			return 1;
                    }
                }

		// Update values and also the winning path 
		if(this.winningPath.size() == 0 
		   || this.winningPath.peek().getParents().contains(tree)) {
		    this.winningPath.addFirst(tree);
		}
                state.setVal(-1);
                return -1;

            } else {

		// It is player 2's turn

		// Check assigned values before doigna recursive call, for speed
                for(Tree<GameState> child: children) {

		    if(child.getState().getVal() == -1) {

			// Update values and also the winning path 
			if(this.winningPath.size() == 0 
			   || this.winningPath.peek().getParents().contains(tree)) {
				this.winningPath.addFirst(tree);
			}
			state.setVal(-1);
			return -1;
		    }
		}

		// Get the minimum over all possible subsequent GameStates 
                for(Tree<GameState> child: children) {
                    if(determineVal(child) == -1) {

			// Update values and also the winning path 
			if(this.winningPath.size() == 0 
			   || this.winningPath.peek().getParents().contains(tree)) {
				this.winningPath.addFirst(tree);
			}
			state.setVal(-1);
			return -1;
                    }
                }

                state.setVal(1);
                return 1;

            }
        }
    }


    //==================================
    // MAIN METHOD
    //==================================


    public static void main(String[] args) {
	System.out.println();




	//============================================
	// COLLECTION OF RUNNABLE POSSIBILITIES
	//============================================


	/*
	Game game = new Game(9);
	game.playGame();
	*/

	
	/*
	for(int gameNum = 1; gameNum <= 50; gameNum++) {
	    System.out.print("\n---------------------------------\n");
	    System.out.println(gameNum + "\n");
	    //System.out.println("Game on " + gameNum);
	    Game game = new Game(gameNum);
	    game.playGame();
	    //System.out.println("Winner is: " + game.playGame() + "\n");
	    

	}
	*/

		
	/*
	for(int gameNum = 1; gameNum <= 40; gameNum++) {
	    Game game = new Game(gameNum);
	    game.playGame();
	    
	    System.out.println("\n---------------------------------\n");
	    System.out.println("Game on: " + gameNum + "\n");

	    LinkedList<Tree<GameState>> winner = game.getWinningPath();
	    
	    System.out.println("Length of winning strategy: " + (winner.size() - 1));

	    int count = 0;
	    for(Tree<GameState> tree: winner) {
		if(count % 2 == 0) {
		    System.out.print("Player 1 is playing: ");
		} else {
		    System.out.print("Player 2 is playing: ");
		}
		System.out.print("Threre are ");
		System.out.print(tree.getState().getState().get(2));
		System.out.println(" 2s.");
		count++;
	    }
	    System.out.println("\n");
	}
	*/
	
	

	
	/*
	Game game = new Game(30);
	game.playGame();
	ArrayList<Tree<GameState>> flat = game.getRoot().flatten();
	for(Tree<GameState> t: flat) {
	    //game.determineVal(t);
	    System.out.println(t.getState().toString() + "\n");
	}
	*/

	/*
	Game game = new Game(75);
	System.out.println("Winner " + game.playGame());
	ArrayList<Tree<GameState>> layer1 = game.getRoot().getChildren();
	ArrayList<Tree<GameState>> layer2 = new ArrayList<Tree<GameState>>();
	for(Tree<GameState> t: layer1) {
	    ArrayList<Tree<GameState>> children = t.getChildren();
	    for(Tree<GameState> child: children) {
		layer2.add(child);
	    }
	}
	
	for(Tree<GameState> desired: layer2) {
	    game.determineVal(desired);
	    System.out.println(desired.getState().toString() + "\n");
	}
	*/

	/*
	for(int gameNum = 15; gameNum <= 15; gameNum++) {
	    System.out.println("\n-------------------------------\n");
	    System.out.println("Game on: " + gameNum);
	    Game game = new Game(gameNum);
	    game.playGame();
	    
	    ArrayList<Tree<GameState>> flat = game.getRoot().flatten();
	    for(Tree<GameState> t: flat) {
		if(t.getState().getVal() == 0) {
		    game.determineVal(t);
		}
		System.out.println(t.getState().toString() + "\n");
	    }
	    
	}
	*/
	
	    
	
    }


}