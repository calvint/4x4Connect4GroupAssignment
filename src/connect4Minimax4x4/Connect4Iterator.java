package connect4Minimax4x4;


//author: Gary Kalmanovich; rights reserved

public class Connect4Iterator implements InterfaceIterator {
    // This implementation assumes nC x nR //7 columns by 6 rows

    private int column  = 0;
    private int nC;
    private int nR;
    private Connect4Position position;
    
    
    Connect4Iterator(int nC, int nR, InterfacePosition position) { this.nC = nC; this.nR = nR; 
                                        this.position = (Connect4Position)position;} 
    Connect4Iterator(Connect4Iterator iter) { this.nC = iter.nC(); this.nR = iter.nR();
                            this.position = iter.getPosition(); this.set(iter); } 
    
    @Override public int          iC() { return column; }//7
    @Override public int          iR() { return position.getColumnChipCount(column); }//7 iterator/nC;
    @Override public int          nC() { return          nC; }//7
    @Override public int          nR() { return          nR; }//6
              public Connect4Position getPosition() { return position; }
    @Override public void  increment() { column++; }
    @Override public void  resetBack() {       column = 0; }
    @Override public void set( InterfaceIterator iter ) { column = ((Connect4Iterator)iter).iC(); }
    @Override public void set(int iC, int iR) { column = iC; }//7
    @Override public boolean isInBounds() { return 0<=column && column<nC; }//7*6

}
