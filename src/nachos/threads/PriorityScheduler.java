package nachos.threads;
 
import nachos.machine.*;
 
import java.util.TreeSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.PriorityQueue;
 
/**
* A scheduler that chooses threads based on their priorities.
*
* <p>
* A priority scheduler associates a priority with each thread. The next thread
* to be dequeued is always a thread with priority no less than any other
* waiting thread's priority. Like a round-robin scheduler, the thread that is
* dequeued is, among all the threads of the same (highest) priority, the
* thread that has been waiting longest.
*
* <p>
* Essentially, a priority scheduler gives access in a round-robin fassion to
* all the highest-priority threads, and ignores all other threads. This has
* the potential to
* starve a thread if there's always a thread waiting with higher priority.
*
* <p>
* A priority scheduler must partially solve the priority inversion problem; in
* particular, priority must be donated through locks, and through joins.
* Thread greader 5, and 6 for donation
*
* Track what is waiting on the thread state
* Priorty queue to foward resource
* Lock reprecent resources, inside of the lock there is a queue
* Priorty queue a thread that is called owner, how to keep threak 8 simple
* queue
* have a pri queue from java or heap have full control
* Can have many thread waiting on it, thread state can own different things the
* thing waiting on should only have one thing
* Move around the notes
* Return the cashe value when flag change, dont need to recompute priorty dont
* have to use nacho queue
*/
public class PriorityScheduler extends Scheduler {
   /**
    * Allocate a new priority scheduler.
    */
   public PriorityScheduler() {
   }
 
   /**
    * Allocate a new priority thread queue.
    *
    * @param transferPriority <tt>true</tt> if this queue should
    *                         transfer priority from waiting threads
    *                         to the owning thread.
    * @return a new priority thread queue.
    */
   public ThreadQueue newThreadQueue(boolean transferPriority) {
       return new PriorityQueue(transferPriority);
   }
 
   @Override
   public int getPriority(KThread thread) {
       Lib.assertTrue(Machine.interrupt().disabled());
 
       return getThreadState(thread).getPriority();
   }
 
   public int getEffectivePriority(KThread thread) {
       Lib.assertTrue(Machine.interrupt().disabled());
 
       return getThreadState(thread).getEffectivePriority();
   }
 
   public void setPriority(KThread thread, int priority) {
       Lib.assertTrue(Machine.interrupt().disabled());
 
       Lib.assertTrue(priority >= priorityMinimum &&
               priority <= priorityMaximum);
 
       getThreadState(thread).setPriority(priority);
   }
 
   public boolean increasePriority() {
       boolean intStatus = Machine.interrupt().disable();
 
       KThread thread = KThread.currentThread();
 
       int priority = getPriority(thread);
       if (priority == priorityMaximum)
           return false;
 
       setPriority(thread, priority + 1);
 
       Machine.interrupt().restore(intStatus);
       return true;
   }
 
   public boolean decreasePriority() {
       boolean intStatus = Machine.interrupt().disable();
 
       KThread thread = KThread.currentThread();
 
       int priority = getPriority(thread);
       if (priority == priorityMinimum)
           return false;
 
       setPriority(thread, priority - 1);
 
       Machine.interrupt().restore(intStatus);
       return true;
   }
 
   /**
    * The default priority for a new thread. Do not change this value.
    */
   public static final int priorityDefault = 1;
   /**
    * The minimum priority that a thread can have. Do not change this value.
    */
   public static final int priorityMinimum = 0;
   /**
    * The maximum priority that a thread can have. Do not change this value.
    */
   public static final int priorityMaximum = 7;
 
   /**
    * Return the scheduling state of the specified thread.
    *
    * @param thread the thread whose scheduling state to return.
    * @return the scheduling state of the specified thread.
    */
   protected ThreadState getThreadState(KThread thread) {
       if (thread.schedulingState == null)
           thread.schedulingState = new ThreadState(thread);
 
       return (ThreadState) thread.schedulingState;
   }
 
   /**
    * A <tt>ThreadQueue</tt> that sorts threads by priority.
    */
   protected class PriorityQueue extends ThreadQueue {
       PriorityQueue(boolean transferPriority) {
           this.transferPriority = transferPriority;
       }
 
       public void waitForAccess(KThread thread) {
           Lib.assertTrue(Machine.interrupt().disabled());
           getThreadState(thread).waitForAccess(this);
       }
 
       public void acquire(KThread thread) {
           Lib.assertTrue(Machine.interrupt().disabled());
 
           getThreadState(thread).acquire(this);
       }
 
       public KThread nextThread() {
            Lib.assertTrue(Machine.interrupt().disabled());
 
           ThreadState threadState = pickNextThread();
      
           priorityQueue.poll();    
           if (threadState == null) {
               return null;
           }
           return threadState.thread;
 
       }
 
       /**
        * Return the next thread that <tt>nextThread()</tt> would return,
        * without modifying the state of this queue.
        *
        * @return the next thread that <tt>nextThread()</tt> would
        *         return.
        */
       protected ThreadState pickNextThread() {
           return this.priorityQueue.peek();
       }
 
       public void print() {
           Lib.assertTrue(Machine.interrupt().disabled());
           // implement me (if you want)
       }
 
       /**
        * <tt>true</tt> if this queue should transfer priority from waiting
        * threads to the owning thread.
        */
       protected boolean transferPriority;
       protected java.util.PriorityQueue<ThreadState> priorityQueue = new java.util.PriorityQueue<ThreadState>();
       // queue for priority queue
       protected ThreadState threadHolder = null;
	@Override
	public boolean isEmpty() {
		// Lagacy finction from PJ1 plz ignore
		return false;
	}
   }
 
   /**
    * The scheduling state of a thread. This should include the thread's
    * priority, its effective priority, any objects it owns, and the queue
    * it's waiting for, if any.
    *
    * @see nachos.threads.KThread#schedulingState
    */
   protected class ThreadState implements Comparable<ThreadState> {
       /**
        * Allocate a new <tt>ThreadState</tt> object and associate it with the
        * specified thread.
        *
        * @param thread the thread this state belongs to.
        */
       public ThreadState(KThread thread) {
           this.thread = thread;
           queueList = new LinkedList<PriorityQueue>();
           time = Machine.timer().getTime();
           effectivePriority = priorityDefault;
           deqThread = null;
       }
 
       /**
        * Return the priority of the associated thread.
        *
        * @return the priority of the associated thread.
        */
       public int getPriority() {
           return priority;
       }
 
       /**
        * Return the effective priority of the associated thread.
        *
        * @return the effective priority of the associated thread.
        */
       public int getEffectivePriority() {
           // implement me
           return effectivePriority;
       }
 
       public void calEffectivePriority() {
           int orgPriority = this.getPriority();
           int maxEffectivePriority = -1;
 
           for (int i = 0; i < queueList.size(); i++) {
               PriorityQueue thread = queueList.get(i);
               ThreadState donor = thread.pickNextThread();
               if (donor != null &&
                       donor.getEffectivePriority() > maxEffectivePriority &&
                       thread.transferPriority) {
                   maxEffectivePriority = donor.getEffectivePriority();
               }
 
           }
           if (maxEffectivePriority < orgPriority) {
 
               maxEffectivePriority = orgPriority;
           }
 
           effectivePriority = maxEffectivePriority;
 
           if (deqThread != null && deqThread.threadHolder != null
                   && effectivePriority != deqThread.threadHolder.effectivePriority) {
               deqThread.threadHolder.calEffectivePriority();
           }
       }
 
       /**
        * Set the priority of the associated thread to the specified value.
        *
        * @param priority the new priority.
        */
       public void setPriority(int priority) {
           this.priority = priority;
           calEffectivePriority();
       }
 
       /**
        * Called when <tt>waitForAccess(thread)</tt> (where <tt>thread</tt> is
        * the associated thread) is invoked on the specified priority queue.
        * The associated thread is therefore waiting for access to the
        * resource guarded by <tt>waitQueue</tt>. This method is only called
        * if the associated thread cannot immediately obtain access.
        *
        * @param waitQueue the queue that the associated thread is
        *                  now waiting on.
        *
        * @see nachos.threads.ThreadQueue#waitForAccess
        */
       public void waitForAccess(PriorityQueue waitQueue) {
           boolean intStatus = Machine.interrupt().disable();
           time = Machine.timer().getTime();
           waitQueue.priorityQueue.add(this);
           deqThread = waitQueue;
            
           Machine.interrupt().setStatus(intStatus);
       }
 
       /**
        * Called when the associated thread has acquired access to whatever is
        * guarded by <tt>waitQueue</tt>. This can occur either as a result of
        * <tt>acquire(thread)</tt> being invoked on <tt>waitQueue</tt> (where
        * <tt>thread</tt> is the associated thread), or as a result of
        * <tt>nextThread()</tt> being invoked on <tt>waitQueue</tt>.
        *
        * @see nachos.threads.ThreadQueue#acquire
        * @see nachos.threads.ThreadQueue#nextThread
        */
       public void acquire(PriorityQueue waitQueue) {
           boolean intStatus = Machine.interrupt().disable();
           waitQueue.threadHolder = this;
           queueList.add(waitQueue);
           deqThread = null;
           calEffectivePriority();
           Machine.interrupt().setStatus(intStatus);
 
       }
 
       @Override
       public int compareTo(ThreadState threadState) {
           if (this.getEffectivePriority() < threadState.getEffectivePriority() || this.time >= threadState.time) {
               return 1;
           } 
        	return -1;
       }
 
       /** The thread with which this object is associated. */
       protected KThread thread;
       /** The priority of the associated thread. */
       protected int effectivePriority;
       protected int priority = priorityDefault;
 
       protected LinkedList<PriorityQueue> queueList = new LinkedList<PriorityQueue>();
       // protected LinkedList<ThreadQueue> waitingList = new
       // LinkedList<ThreadQueue>();
       protected PriorityQueue deqThread = null;
       public long time = Machine.timer().getTime();
 
   }
}
