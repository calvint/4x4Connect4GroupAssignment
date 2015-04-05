package connect4Minimax4x4;

//author: Gary Kalmanovich; rights reserved

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

class ThreadStrategy extends NotifyingThread {

    private InterfaceStrategy strategy;
    private InterfacePosition position;
    private InterfaceSearchInfo context;

    ThreadStrategy( InterfaceStrategy strategy, InterfacePosition position, InterfaceSearchInfo context ) {
        this.strategy = strategy;
        this.position = position;
        this.context  = context ;
    }

    InterfaceSearchInfo getContext() { return context; } // 
    @Override public void doRun() {
        long startTime = System.nanoTime(); // Start the total timing
        strategy.getBestMove(position, context);
        //System.out.println("Best move(Thread): c "+bestMove.iC()+", r "+bestMove.iR()+", for player "+player);
        long endTime  = System.nanoTime(); // Finish the total timing
        System.out.println("This move computation took:   " + ((double)(endTime - startTime)/1000000.0) + " milliseconds");
    }
}

// Using thread code from 
//   http://stackoverflow.com/questions/702415/how-to-know-if-other-threads-have-finished

interface ThreadCompleteListener {
    void notifyOfThreadComplete(final Thread thread);
}

abstract class NotifyingThread extends Thread {
    private final Set<ThreadCompleteListener> listeners
                     = new CopyOnWriteArraySet<ThreadCompleteListener>();
    public final void addListener(final ThreadCompleteListener listener) {
      listeners.add(listener);
    }
    public final void removeListener(final ThreadCompleteListener listener) {
      listeners.remove(listener);
    }
    private final void notifyListeners() {
      for (ThreadCompleteListener listener : listeners) {
        listener.notifyOfThreadComplete(this);
      }
    }
    @Override
    public final void run() {
      try {
        doRun();
      } finally {
        notifyListeners();
      }
    }
    public abstract void doRun();
  }
