package nachos.threads; //Change this one too 

import nachos.machine.*;

/**
 * An implementation of condition variables that disables interrupt()s for
 * synchronization.
 *
 * <p>
 * You must implement this.
 *
 * @see	nachos.threads.Condition
 */
public class Condition2 {
    /**
     * Allocate a new condition variable.
     *
     * @param	conditionLock	the lock associated with this condition
     *				variable. The current thread must hold this
     *				lock whenever it uses <tt>sleep()</tt>,
     *				<tt>wake()</tt>, or <tt>wakeAll()</tt>.
     */
    public Condition2(Lock conditionLock) {
	    this.conditionLock = conditionLock;
    }

    //Create something simular using thread kernal,put current thread into Q. 
    private ThreadQueue waitQueue = 
    ThreadedKernel.scheduler.newThreadQueue(true);
    
    //ThreadQueue emptyQueue =ThreadedKernel.scheduler.newThreadQueue(transferPriority)
    
    
    //private ThreadQueue emptyQueue = ThreadedKernel.scheduler.newThreadQueue(true);
    /**
     * Atomically release the associated lock and go to sleep on this condition
     * variable until another thread wakes it using <tt>wake()</tt>. The
     * current thread must hold the associated lock. The thread will
     * automatically reacquire the lock before <tt>sleep()</tt> returns.
     */
    public void sleep() {
        Lib.assertTrue(conditionLock.isHeldByCurrentThread());
        boolean currentStat = Machine.interrupt().disable(); 
        conditionLock.release();
        waitQueue.waitForAccess(KThread.currentThread());
        KThread.sleep();
        conditionLock.acquire();
        Machine.interrupt().restore(currentStat);
    }

	/**
	 * Wake up at most one thread sleeping on this condition variable. The
	 * current thread must hold the associated lock.
	 */
	public void wake() {
		Lib.assertTrue(conditionLock.isHeldByCurrentThread());
		
		boolean currentStat = Machine.interrupt().disable(); 
		
        //while there is nothing in waitQueue
		if (!waitQueue.isEmpty())
		{   
			waitQueue.nextThread().ready(); 
        }
		Machine.interrupt().restore(currentStat);
	}

	/**
	 * Wake up all threads sleeping on this condition variable. The current
	 * thread must hold the associated lock.
	 */
	public void wakeAll() {
		Lib.assertTrue(conditionLock.isHeldByCurrentThread());
		
        //while there is nothing in waitQueue
		while(!waitQueue.isEmpty()){
            wake();
		}
	}


    private Lock conditionLock;
}
