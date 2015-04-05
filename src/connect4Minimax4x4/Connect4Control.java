package connect4Minimax4x4;

import connect4Minimax4x4.Connect4SearchInfo;
import connect4Minimax4x4.Connect4Strategy;

//author: Gary Kalmanovich; rights reserved

public class Connect4Control implements InterfaceControl, ThreadCompleteListener{
    // This is the controller class. It is also a model class
    // Its responsibilities are:
    //   - to make sure that the rules of the game are followed
    //   - to check if there is a winner in a particular position
    //   - to notify Strategy if Strategy is playing
    //   - this class can keep current state of the game
    //   

    private InterfaceView     view;
    private Connect4Position position; 
    private int currentPlayer = 1;
    private InterfaceStrategy player1Strategy = null;// new Connect4Strategy();// 
    private InterfaceStrategy player2Strategy = null;// new Connect4Strategy();// 
    private boolean isMoveBlockedByCalculation = false;

    @Override
    public void onMove() { // Control is notified of a player (real or automated) move
        if ( currentPlayer == 1 ) {
            if (player1Strategy != null && !isBlockManualMove()) { // isStrategy and game not over
                isMoveBlockedByCalculation = true;
                position.setPlayer( 1 );
                Connect4SearchInfo context = new Connect4SearchInfo();
                NotifyingThread thread = new ThreadStrategy(player1Strategy, position, context);
                thread.addListener(this); // add ourselves as a listener
                thread.start();           // Start the Thread
            }
        }
        if ( currentPlayer == 2 ) {
            if (player2Strategy != null && !isBlockManualMove()) { // isStrategy and game not over
                isMoveBlockedByCalculation = true;
                position.setPlayer( 2 );
                Connect4SearchInfo context = new Connect4SearchInfo();
                NotifyingThread thread = new ThreadStrategy(player2Strategy, position, context);
                thread.addListener(this); // add ourselves as a listener
                thread.start();           // Start the Thread
            }
        }
    }

    @Override
    public void onMove(int unused1, int unused2, int iColumn, int iRow, int iColor) {
        InterfaceIterator iter = new Connect4Iterator( view.nC(), view.nR()); iter.set(iColumn, iRow);
        //System.out.print("Slot: ( "+iRow+", "+iColumn+" ) is set to color: "+iColor+". Was set to: "+position.getColor(iter));
        position.setColor(iter, iColor);
        //System.out.println(". Now: "+position.getColor(iter));
        currentPlayer = 3 - iColor;
        onMove();
    }

    @Override
    public void resetGame() {
        position.reset();
        currentPlayer = 1;
    }

    @Override
    public void setView(InterfaceView connect4View) {
        view = connect4View;
        position = new Connect4Position( view.nC(), view.nR() );
    }

    @Override
    public InterfaceView getView() {
        return view;
    }

    @Override 
    public void setStrategy( int player, int strategy ) {
        InterfaceStrategy playerStrategy = strategy==0 ? null : new Connect4Strategy();
        if (player==1) player1Strategy = playerStrategy;
        else           player2Strategy = playerStrategy;
        onMove(); // Check if anything needs to be done via a strategy
    }
    
    @Override 
    public int getStrategy( int player ) {
        if (player==1) return player1Strategy==null ? 0 : 1;
        else           return player2Strategy==null ? 0 : 1;
    }
    
    @Override 
    public boolean isBlockManualMove() {
        return position.isWinner() >= 0  ||  isMoveBlockedByCalculation;
    }

    @Override
    public void notifyOfThreadComplete(Thread thread) {
        InterfaceSearchInfo context   = ((ThreadStrategy)thread).getContext();
        //System.out.println("Best move(Applct): c "+bestMove.iC()+", r "+bestMove.iR());
        int iR = context.getBestMoveSoFar().iR();
        int iC = context.getBestMoveSoFar().iC();
        
        thread.interrupt();
        
        view.performMove(0, 0, iC, iR, 0); // 0 references is not used in this view
        
        isMoveBlockedByCalculation = false;
    }
}
