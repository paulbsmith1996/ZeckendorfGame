Paul Baird-Smith 2017/2018

TreeDrawer is used to give a visual representation of the tree structure of the
Zeckendorf game described in (Epstein 2018). It plays through a specified game,
determining all moves that can be made, and draw all possible paths to the end of
this game.

Each horizontal layer is composed of GameStates that can be reached in the same
number of moves, namely the depth of the layer (e.g. any state in the 3rd layer
is reached in exactly 3 moves). States with red trim are states at which player 2
has a winning strategy over player 1, and states with blue trim are those at which
player 1 has a winning startegy. Lines between states signify that the lower state
can be reached after a single move from the upper state (parent/child relationship
in the tree structure).

States highlighted in yellow are terminal. There can be at most 1 of these in any
layer by design. Experiments to this point have shown that player 2 always has a
winning strategy (true up to 50), therefore we highlight states in green if they
belong to "the" winning path for player 2 (in reality, there are several winning
paths but we highlight just a single one).

The TreeDrawer can be executed, after compilaton, by running the command

    appletviewer TreeDrawer.java

Do not delete the comment in the preamble, as this is used at runtime by the
appletviewer.

email: ppb366@cs.utexas.edu