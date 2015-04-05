package connect4Minimax4x4;

//author: Gary Kalmanovich; rights reserved

public class Connect4Strategy implements InterfaceStrategy {
    @Override
    public void getBestMove(InterfacePosition position, InterfaceSearchInfo context) {
        int player   = position.getPlayer();
        int opponent = 3-player; // There are two players, 1 and 2.
        for ( InterfaceIterator iPos = new Connect4Iterator(4, 4); iPos.isInBounds(); iPos.increment() ) {
            InterfacePosition posNew = new Connect4Position(position);
            if (posNew.getColor(iPos) == 0 & ((iPos.nR()-iPos.iR()-1) == ((Connect4Position) posNew).getColumnChipCount(iPos))) { // This is a free spot
                posNew.setColor(iPos, player);
                int isWin = posNew.isWinner();
                float score;
                if        ( isWin ==   player ) { score =  1f;  // Win
                } else if ( isWin ==        0 ) { score =  0f;  // Draw
                } else if ( isWin == opponent ) { score = -1f;  // Loss
                } else { // Game is not over, so check further down the game
                    posNew.setPlayer(opponent);
                    InterfaceSearchInfo opponentContext = new Connect4SearchInfo();
                    getBestMove(posNew, opponentContext);
                    score = -opponentContext.getBestScoreSoFar();
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

class Connect4SearchInfo implements InterfaceSearchInfo {

    InterfaceIterator bestMoveSoFar  = null;
    float             bestScoreSoFar = Float.NEGATIVE_INFINITY;

    @Override
    public InterfaceIterator getBestMoveSoFar() {
        return bestMoveSoFar;
    }

    @Override
    public float getBestScoreSoFar() {
        return bestScoreSoFar;
    }

    @Override
    public void setBestMoveSoFar(InterfaceIterator newMove, float newScore) {
        bestMoveSoFar  = new Connect4Iterator(newMove);
        bestScoreSoFar = newScore;
    }

    @Override
    public int getMinDepthSearchForThisPos() {
        // Not used in this strategy
        return 0;
    }

    @Override
    public void setMinDepthSearchForThisPos(int minDepth) {
        // Not used in this strategy
    }

    @Override
    public int getMaxDepthSearchForThisPos() {
        // Not used in this strategy
        return 0;
    }

    @Override
    public void setMaxDepthSearchForThisPos(int minDepth) {
        // Not used in this strategy
    }

    @Override
    public int getMaxSearchTimeForThisPos() {
        // Not used in this strategy
        return 0;
    }

    @Override
    public void setMaxSearchTimeForThisPos(int maxTime) {
        // Not used in this strategy
    }

    @Override
    public float getOpponentBestScoreOnPreviousMoveSoFar() {
        // Not used in this strategy
        return 0;
    }

    @Override
    public void setOpponentBestScoreOnPreviousMoveSoFar(float scoreToBeat) {
        // Not used in this strategy
    }

    @Override
    public int getClassStateCompacted() {
        // Not used in this strategy
        return 0/0;
    }

    @Override
    public void setClassStateFromCompacted(int compacted) {
        // Not used in this strategy
    }

}
