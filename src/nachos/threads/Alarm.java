package nachos.threads;

import java.util.PriorityQueue;

import nachos.machine.Machine;
//import nachos.threads.PriorityScheduler.PriorityQueue; //Not Implimented yet...


/**
 * Uses the hardware timer to provide preemption, and to allow threads to sleep
 * until a certain time.
 * 
 * - Can use multiple condition veriable (and should)
 * - Dont use condition veriable to queue up thread, can have multiple for it
 *      - use it to sleep and wake it up
 * - Condition veriable to put it awake and sleep
 * -
 * 
 * Complete the implementation of the Alarm class, by implementing the waitUntil(long x) method using condition variables. 
 * A thread calls waitUntil to suspend its own execution until time has advanced to at least now + x. This is useful for threads 
 * that operate in real-time, for example, for blinking the cursor once per second. There is no requirement that threads start running 
 * immediately after waking up; just put them on the ready queue in the timer interrupt handler after they have waited for at least the 
 * right amount of time. Do not fork any additional threads to implement waitUntil(); you need only modify waitUntil() and the timer interrupt handler. 
 * waitUntil is not limited to one thread; any number of threads may call it and be suspended at any one time.
 */
public class Alarm {
    /**
     * Allocate a new Alarm. Set the machine's timer interrupt handler to this
     * alarm's callback.
     *
     * <p><b>Note</b>: Nachos will not function correctly with more than one
     * alarm.
     */
    public Alarm() {
	Machine.timer().setInterruptHandler(new Runnable() {

		public void run() { timerInterrupt(); }
	    });
    }

    /**
     * The timer interrupt handler. This is called by the machine's timer
     * periodically (approximately every 500 clock ticks). Causes the current
     * thread to yield, forcing a context switch if there is another thread
     * that should be run 
     */
    public void timerInterrupt() {
        boolean Status = Machine.interrupt().disable();
        while (!waitQueue.isEmpty()&&waitQueue.peek().compareTo(Machine.timer().getTime())<=0) {
            //waitQueue.remove().getThread().ready();
            KThreadTimer FirstQueue = waitQueue.poll();
            lock.acquire();
            FirstQueue.threadWeaker();;
            lock.release();
     
        }
        KThread.yield();
        Machine.interrupt().restore(Status);
        //KThread.currentThread().yield();
    }

    /**
     * Put the current thread to sleep for at least <i>x</i> ticks,
     * waking it up in the timer interrupt handler. The thread must be
     * woken up (placed in the scheduler ready set) during the first timer
     * interrupt where
     *
     * <p><blockquote>
     * (current time) >= (WaitUntil called time)+(x)
     * </blockquote>
     *
     * @param	x	the minimum number of clock ticks to wait.
     *
     * @see	nachos.machine.Timer#getTime()
     */
    public void waitUntil(long x) {
		// disable and store interrupts
		
        long wakeTime = Machine.timer().getTime() + x;
        boolean Status = Machine.interrupt().disable();

        KThreadTimer threadTimer = new KThreadTimer(KThread.currentThread(), wakeTime);
        //waitQueue.add(new KThreadTimer(KThread.currentThread(), wakeTime));
        waitQueue.add(threadTimer);
        //KThread.sleep(); //Use condition ver to put it to sleep
        KThreadTimer FirstQueue = waitQueue.peek();
        lock.acquire();
        FirstQueue.threadSleeper();
        lock.release();
 
		Machine.interrupt().restore(Status);
    }

    class KThreadTimer implements Comparable<Long>{
        private KThread currentThread;
        private long time = 0;
        public KThreadTimer(KThread thread, long waitTime){
            this.currentThread=thread;
            this.time=waitTime;
        }
        public KThread getThread(){
            return currentThread;
        }
        public long getTime(){
            return time;
        }
        @Override
        public int compareTo(Long o) {
            if (o < this.time) {
                return 1;		
            } else if (o == this.time) {
                return 0;
            } else {
                return -1;
            }
        }
        
        public void threadSleeper() {
            speakers.sleep();
        }
        public void threadWeaker() {
            speakers.wake();
        }
    }
    
    //Need to use PriortyQueue
    PriorityQueue<KThreadTimer> waitQueue = new PriorityQueue<KThreadTimer>();
    
    private Lock lock =new Lock();
    
    private Condition2 speakers = new Condition2(lock);
    

    
}
