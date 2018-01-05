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
     * Constructor for a Game on n 1s
     *
     * @param n - The number of 1s in the starting decomposition of the fibonacci game
     */
    public Game(int n) {

	// Initialize all global values
	this.n = n;
	this.initGameState = new GameState(n);
	this.topRoot = new Tree<GameState>(initGameState);
    }


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
     * Generates the game tree for the Game object
     *
     * @return - 1 for a Player 1 victory, -1 for a Player 2 victory
     */
    public int playGame() {

	System.out.println("Playing game");
	
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
		System.out.println("Checked up to " + curDepth + " moves");
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
        
        //System.out.println("All games played after " + curDepth + " moves.");
        System.out.println("Explored " + numNodes + " nodes.");
        //System.out.println("Total of " + numEnds + " games played.");
        //System.out.println(start.determineVal());
        
	System.out.println("Finding value");
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
		// Player 2 wins if an even number of moves have been played
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

                state.setVal(-1);
                return -1;

            } else {

		// It is player 2's turn

		// Check assigned values before doigna recursive call, for speed
                for(Tree<GameState> child: children) {

		    if(child.getState().getVal() == -1) {
			state.setVal(-1);
			return -1;
		    }
		}

		// Get the minimum over all possible subsequent GameStates 
                for(Tree<GameState> child: children) {
                    if(determineVal(child) == -1) {
			state.setVal(-1);
			return -1;
                    }
                }

                state.setVal(1);
                return 1;

            }
        }
    }

    public static void main(String[] args) {
	System.out.println();
	
	/*
	for(int gameNum = 1; gameNum <= 50; gameNum++) {
	    System.out.println("Game on " + gameNum);
	    Game game = new Game(gameNum);
	    System.out.println("Winner is: " + game.playGame() + "\n");
	    

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
	    
	
    }


}