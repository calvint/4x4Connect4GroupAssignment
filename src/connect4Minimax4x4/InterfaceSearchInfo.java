package connect4Minimax4x4;

//author: Gary Kalmanovich; rights reserved

interface InterfaceSearchInfo{ 
    // Anything that is related to bounds on strategy calculations.
    // In particular this class is connected to a search of a particular position
    //  and contains the result of such search as well as the parameters of the search
    // For example,
    InterfaceIterator getBestMoveSoFar();
    float    getBestScoreSoFar();
    void     setBestMoveSoFar( InterfaceIterator newMove, float newScore );
    int      getMinDepthSearchForThisPos();
    void     setMinDepthSearchForThisPos( int minDepth );
    int      getMaxDepthSearchForThisPos();
    void     setMaxDepthSearchForThisPos( int minDepth );
    int      getMaxSearchTimeForThisPos();
    void     setMaxSearchTimeForThisPos( int maxTime );
    float    getOpponentBestScoreOnPreviousMoveSoFar();                    // For alpha-beta pruning
    void     setOpponentBestScoreOnPreviousMoveSoFar( float scoreToBeat ); // For alpha-beta pruning
    int      getClassStateCompacted();
    void     setClassStateFromCompacted(int compacted);
    // Note, not all of these need to be fully implemented. 
    // Ones that are not utilized can be simply empty shells. 
}