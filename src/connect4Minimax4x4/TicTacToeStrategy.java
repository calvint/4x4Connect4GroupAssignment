package connect4Minimax4x4;

//author: Gary Kalmanovich; rights reserved

public class TicTacToeStrategy implements InterfaceStrategy {
    @Override
    public void getBestMove(InterfacePosition position, InterfaceSearchInfo context) {
        // Note, return information is embedded in context

        int player = position.getPlayer();
        int opponent = 3-player; // There are two players, 1 and 2.
        for ( InterfaceIterator iPos = new TicTacToeIterator(); iPos.isInBounds(); iPos.increment() ) {
            InterfacePosition posNew = new TicTacToePosition(position);
            if (posNew.getColor(iPos) == 0) { // This is a free spot
                posNew.setColor(iPos, player);
                int isWin = posNew.isWinner();
                float score;
                if        ( isWin ==   player ) { score =  1f;  // Win
                } else if ( isWin ==        0 ) { score =  0f;  // Draw
                } else if ( isWin == opponent ) { score = -1f;  // Loss
                } else { // Game is not over, so check further down the game
                    posNew.setPlayer(opponent);
                    InterfaceSearchInfo opponentContext = new TicTacToeSearchInfo();
                    getBestMove(posNew, opponentContext); // Return information is in opponentContext
                    score = -opponentContext.getBestScoreSoFar();
                    // Note, for player, opponent's best move has negative worth
                    //   That is because, score = ((probability of win) - (probability of loss))
                }

                if (context.getBestScoreSoFar() <  score ) {
                    context.setBestMoveSoFar(iPos, score );
                }
            }
        }
    }

    @Override
    public void setContext(InterfaceSearchInfo strategyContext) {
        // Not used in this strategy
    }

    @Override
    public InterfaceSearchInfo getContext() {
        // Not used in this strategy
        return null;
    }
}
