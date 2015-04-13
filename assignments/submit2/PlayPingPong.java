package vandy.mooc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

/**
 * @class PlayPingPong
 *
 * @brief This class uses elements of the Android HaMeR framework to
 *        create two Threads that alternately print "Ping" and "Pong",
 *        respectively, on the display.
 */
public class PlayPingPong implements Runnable {
    private final String TAG = getClass().getSimpleName();

    /**
     * Keep track of whether a Thread is printing "ping" or "pong".
     */
    private enum PingPong {
        PING, PONG
    };

    /**
     * Number of iterations to run the ping-pong algorithm.
     */
    private final int mMaxIterations;

    /**
     * The strategy for outputting strings to the display.
     */
    private final OutputStrategy mOutputStrategy;

    /**
     * Define a pair of Handlers used to send/handle Messages via the
     * HandlerThreads.
     */
    // @@ TODO - you fill in here.
    private  Handler h1;
    private  Handler h2;

    /**
     * Define a CyclicBarrier synchronizer that ensures the
     * HandlerThreads are fully initialized before the ping-pong
     * algorithm begins.
     */
    // @@ TODO - you fill in here.
    private CyclicBarrier mCycle;


    /**
     * Implements the concurrent ping/pong algorithm using a pair of
     * Android Handlers (which are defined as an array field in the
     * enclosing PlayPingPong class so they can be shared by the ping
     * and pong objects).  The class (1) extends the HandlerThread
     * superclass to enable it to run in the background and (2)
     * implements the Handler.Callback interface so its
     * handleMessage() method can be dispatched without requiring
     * additional subclassing.
     */
    class PingPongThread extends HandlerThread implements Handler.Callback {

        private final String TAG = getClass().getSimpleName();

        /**
         * Keeps track of whether this Thread handles "pings" or
         * "pongs".
         */
        private PingPong mMyType;

        /**
         * Number of iterations completed thus far.
         */
        private int mIterationsCompleted;

        /**
         * Constructor initializes the superclass and type field
         * (which is either PING or PONG).
         */
        public PingPongThread(PingPong myType) {
        	super(myType.toString());
            // @@ TODO - you fill in here.
            mMyType = myType;
        }

        /**
         * This hook method is dispatched after the HandlerThread has
         * been started.  It performs ping-pong initialization prior
         * to the HandlerThread running its event loop.
         */
        @Override    
        protected void onLooperPrepared() {

            Log.d(TAG,"onlooper prepared");
            // Create the Handler that will service this type of
            // Handler, i.e., either PING or PONG.
            // @@ TODO - you fill in here.
            if (mMyType == PingPong.PING) {
                h1 = new Handler(this);
            }
            else {
                h2 = new Handler(this);
            }
            try {
                // Wait for both Threads to initialize their Handlers.
                // @@ TODO - you fill in here.
                mIterationsCompleted = 1;
                mCycle.await();

            } catch (Exception e) {
                e.printStackTrace();
            }

            // Start the PING_THREAD first by (1) creating a Message
            // where the PING Handler is the "target" and the PONG
            // Handler is the "obj" to use for the reply and (2)
            // sending the Message to the PING_THREAD's Handler.
            // @@ TODO - you fill in here.

            if (mMyType == PingPong.PING) {
                Message msg = h1.obtainMessage();
                msg.obj = h2;
                msg.sendToTarget();
                mOutputStrategy.print("\n");

            }

        }

        /**
         * Hook method called back by HandlerThread to perform the
         * ping-pong protocol concurrently.
         */
        @Override
        public boolean handleMessage(Message reqMsg) {
            // Print the appropriate string if this thread isn't done
            // with all its iterations yet.
            // @@ TODO - you fill in here, replacing "true" with the
            // appropriate code.
            if (mMyType == PingPong.PING) {
                Log.d(TAG, "handleMessage PING");
            } else  {
                Log.d(TAG, "handleMessage PONG");
            }

            if (mIterationsCompleted<=mMaxIterations) {
                if (mMyType == PingPong.PING) {
                    mOutputStrategy.print("PING("+String.valueOf(mIterationsCompleted)+")\n");
                }
                else {
                    mOutputStrategy.print("PONG("+String.valueOf(mIterationsCompleted)+")\n");
                 }
                mIterationsCompleted +=1;

            } else {
                Log.d("TAG","Quit");
                // Shutdown the HandlerThread to the main PingPong
                // thread can join with it.
                // @@ TODO - you fill in here.
                quit();
                return  true;
            }


            // Create a Message that contains the Handler as the
            // reqMsg "target" and our Handler as the "obj" to use for
            // the reply.
            // @@ TODO - you fill in here.

            Handler t_h = (Handler) reqMsg.obj;

            // Uncomment the "if..." to be sure interrupts are properly handled
           // if (t_h.getLooper().getThread().isAlive()){
                Message msg = t_h.obtainMessage();
                msg.obj = reqMsg.getTarget();
                msg.sendToTarget();
           // }

            return true;
        }
    }

    /**
     * Constructor initializes the data members.
     */
    public PlayPingPong(int maxIterations,
                        OutputStrategy outputStrategy) {
        // Number of iterations to perform pings and pongs.
        mMaxIterations = maxIterations;

        // Strategy that controls how output is displayed to the user.
        mOutputStrategy = outputStrategy;
    }

    /**
     * Start running the ping/pong code, which can be called from a
     * main() method in a Java class, an Android Activity, etc.
     */
    public void run() {
        // Let the user know we're starting. 
        mOutputStrategy.print("Ready...Set...Go!");

         Log.d(TAG, "run");

        // Create the ping and pong threads.
        // @@ TODO - you fill in here.

        PingPongThread PING = new PingPongThread(PingPong.PING);
        PingPongThread PONG = new PingPongThread(PingPong.PONG);


        // Start ping and pong threads, which cause their Looper to
        // loop.
        // @@ TODO - you fill in here.
        PING.start();
        PONG.start();


        // Barrier synchronization to wait for all work to be done
        // before exiting play().
        // @@ TODO - you fill in here.
       mCycle = new CyclicBarrier(2);


        try {
            Log.d(TAG, "join");
            PING.join();
            Log.d(TAG, "JoinPING");
            // Why no PONG.join() ??

        }catch (InterruptedException e) {
            Log.d(TAG, "Problem in barrier synchro");
        }


        // Let the user know we're done.
        mOutputStrategy.print("Done!");
    }
}
