package connect4Minimax4x4;

//author: Gary Kalmanovich; rights reserved

public class Connect4Position implements InterfacePosition {
    // This implementation is designed for at most 7 columns by 6 rows
    // It packs the entire position into a single long
    // Though, there is some sparseness to the packing
    
    // Rightmost 21=3*7 bits are for storing column sizes. (3 bits accommodates 0..7)
    // Next, going to the left 42=6*7*1 bits are binary for colors. (Either red or yellow) 
    // Finally, the left most bit is for the player
    

    private long position = 0;
    private int nC = 0;
    private int nR = 0;

    Connect4Position( int nC, int nR) {
        position = 0;
        this.nC = nC;
        this.nR = nR;
    }

    Connect4Position( InterfacePosition pos ) {
        position = pos.getRawPosition();
        nC       = pos.nC();
        nR       = pos.nR();
    }

    private int getColumnChipCount( InterfaceIterator iPos ) { // Number of chips in column iC
        return getColumnChipCount( iPos.iC() );
    }
    
    public int getColumnChipCount( int iC ) { // Number of chips in column iC. 
        // return should be from 0-6.

        // Rightmost 21=3*7 bits are for storing column sizes. (3 bits accommodates 0..7)
        // Next, going to the left 42=6*7*1 bits are binary for colors. (Either red or yellow) 
        // Finally, the left most bit is for the player
        return (int) ((position >>> (18 - (iC * 3))) & 7L);
    }
    
    @Override public int nC() { return nC; } 
    @Override public int nR() { return nR; }

    @Override
    public long getRawPosition() { 
        return position;
    }

    @Override
    public int getColor( InterfaceIterator iPos ) { // 0 if transparent, 1 if red, 2 if yellow
        int  iR_ = iPos.nR()-iPos.iR()-1; // This numbers the rows from the bottom up
        return getColor( iPos.iC(), iR_, getColumnChipCount(iPos.iC()) );
    }

    private int getColor( int iC, int iR_, int nColumnChipCount ) { // 0 if transparent, 1 if red, 2 if yellow
        //TODO fill this in based on:
        // Rightmost 21=3*7 bits are for storing column sizes. (3 bits accommodates 0..7)
        // Next, going to the left 42=6*7*1 bits are binary for colors. (Either red or yellow) 
        // Finally, the left most bit is for the player
        
        // iR_ should be an index from 0 to 5.
        // If there are spots filled in the column
        // equal to the index, then the index spot
        // is actually empty.
//        System.out.println("getting color");
//        System.out.println(iC);
//        System.out.println(iR_);
//        System.out.println(nColumnChipCount);
        if (iR_ >= nColumnChipCount) {
            return 0;
        }
        else {
            return ((int)((position >>> (62 - ((iC * 6) + iR_))) & 1L)) + 1;
        }
    }

    public boolean spotReady(InterfaceIterator iPos) {
        int  iR  = iPos.iR();
        int  iR_ = iPos.nR()-iR-1; // This numbers the rows from the bottom up
        if ( iR_ > getColumnChipCount(iPos) || iR_ < getColumnChipCount(iPos)) { 
            return false;
        }
        return true;
    }
    
    @Override
    public void setColor( InterfaceIterator iPos, int color ) { // color is 1 if red, 2 if yellow
        int  iC  = iPos.iC();
        int  iR  = iPos.iR();
        int  iR_ = iPos.nR()-iR-1; // This numbers the rows from the bottom up
        if (        iR_ > getColumnChipCount(iPos)) {
            System.err.println("Error: This position ("+iC+","+iR_+") cannot yet be filled.");
        } else if ( iR_ < getColumnChipCount(iPos)) {
            System.err.println("Error: This position ("+iC+","+iR_+") is already filled.");
        } else {
            // Increment columnSize
            int shiftAmount = 18 - (iC * 3);
            
            int currColSize = (int) ((position >>> shiftAmount) & 7L);
            long newColSize = (currColSize + 1) & 0x0000000000000007L;
            
            long rightHalf = (position << (64-shiftAmount)) >>> (64-shiftAmount);
            
            position = ((position >>> shiftAmount)
                    & 0xFFFFFFFFFFFFFFF8L) | newColSize;
            
            position = (position << shiftAmount) | rightHalf;
            
            // Set the color (default is color==1) So default bit is 0?
            int PLAYER_BIT = 1;
            int posInColorBits = PLAYER_BIT + ((iC * 6) + iR_);
            position = position | (((long)(color - 1)) << (63-posInColorBits));
        }
    }

    @Override
    public int isWinner() {
        //      if winner, determine that and return winner, 
        //      else if draw, return 0
        //      else if neither winner nor draw, return -1

        //TODO make this faster, store getColor() calls. Rework diagonal checks.
        
        // Hard coded assumption of 4 column, 4 row board.
        
        int[] columnChipCounts = new int[nC];
        for (int iC=0; iC < nC; iC++) {
            columnChipCounts[iC] = getColumnChipCount(iC);
        }
        
        int[][] spotColors = new int[nC][nR];
        for (int iC=0; iC < nC; iC++) {
            for (int iR=0; iR < nR; iR++) {
                spotColors[iC][iR] = getColor(iC, iR, columnChipCounts[iC]);
            }
        }
        
        // go through vertical wins
        for (int iC=0; iC < nC; iC++) {
            if (columnChipCounts[iC] < 4) continue;
            for (int iR = columnChipCounts[iC]-1; iR >= 3; iR-- ) {
                if (spotColors[iC][iR] == 0) continue;
                if (spotColors[iC][iR] == spotColors[iC][iR-1] &&
                    spotColors[iC][iR-1] == spotColors[iC][iR-2] &&
                    spotColors[iC][iR-2] == spotColors[iC][iR-3]) {
                    return spotColors[iC][iR];
                }
            }
        }
        
        // go through horizontal wins
        for (int iR=0; iR < nR; iR++) {
            for (int iC = 0; iC <= nC - 4; iC++ ) {
                if (spotColors[iC][iR] == 0) continue;
                if (spotColors[iC][iR] == spotColors[iC+1][iR] &&
                    spotColors[iC+1][iR] == spotColors[iC+2][iR] &&
                    spotColors[iC+2][iR] == spotColors[iC+3][iR]) {
                    return spotColors[iC][iR];
                }
            }
        }
        
        // check diagonals
        // hard coded for 4x4 case
        if (spotColors[0][0] != 0) {
            if (spotColors[0][0] == spotColors[1][1]
                && spotColors[1][1] == spotColors[2][2]
                && spotColors[2][2] == spotColors[3][3]) {
                return spotColors[0][0];
            }
        }
        if (spotColors[0][3] != 0) {
            if (spotColors[0][3] == spotColors[1][2]
                && spotColors[1][2] == spotColors[2][1]
                && spotColors[2][1] == spotColors[3][0]) {
                return spotColors[0][3];
            }
        }
        
        // If we got this far, nobody has won. Therefore if the whole board is filled,
        // it is a draw.
        boolean allFilled = true;
        for (int iC=0; iC < nC; iC++) {
            if (columnChipCounts[iC] != 4) {
                allFilled = false;
                break;
            }
        }
        
        if (allFilled) {
            return 0;
        }
        else {
            return -1;
        }
    }

    @Override
    public void reset() {
        position = 0;
    }

    @Override
    public void setPlayer(int iPlayer) { // Only 1 or 2 are valid
        if ( !(0<iPlayer && iPlayer<3) ) {
            System.err.println("Error(Connect4Position::setPlayer): iPlayer ("+iPlayer+") out of bounds!!!");
        } else {
            int  currentPlayer = getPlayer();
            if ( currentPlayer != iPlayer ) {
                position ^= 1L << 63;
            }
        }
    }

    @Override
    public int getPlayer() {
        return ((int)(position>>>63))+1;
    }

    @Override
    public int getChipCount() {
        int chipCount = 0;
        for ( int iC = 0; iC < nC(); iC++ ) chipCount += getColumnChipCount(iC);
        return chipCount;
    }

    @Override
    public int isWinner(InterfaceIterator iPos) {
        // Not yet used (You may want to implement/use this for the group assignment)
        System.out.println("uh oh");
        return 0/0;
    }

    @Override
    public float valuePosition() {
        // Not yet used
        return 0/0;
    }

    @Override
    public int getChipCount(InterfaceIterator iPos) {
        // Not used yet
        return 0/0;
    }

}
