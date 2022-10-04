package nachos.threads;

import nachos.machine.*;

/**
 * A <i>communicator</i> allows threads to synchronously exchange 32-bit
 * messages. Multiple threads can be waiting to <i>speak</i>,
 * and multiple threads can be waiting to <i>listen</i>. But there should never
 * be a time when both a speaker and a listener are waiting, because the two
 * threads can be paired off at this point.
 * 
 * 
 * Implement synchronous send and receive of one word messages (also known as Ada-style rendezvous), 
 * using condition variables (don’t use semaphores!). Implement the Communicator class with operations, 
 * void speak(int word) and int listen(). speak() atomically waits until listen() is called on the same Communicator object,
 * and then transfers the word over to listen(). Once the transfer is made, both can return. Similarly, listen() waits until speak() is called,
 * at which point the transfer is made, and both can return (listen() returns the word). This means that neither thread may return from listen() 
 * or speak() until the word transfer has been made. Your solution should work even if there are multiple speakers and listeners for the same Communicator 
 * (note: this is equivalent to a zero-length bounded buffer; since the buffer has no room, the producer and consumer must interact directly, requiring that
 * they wait for one another). Each communicator should only use exactly one lock. If you’re using more than one lock, you’re making things too complicated.

***
 */
public class Communicator {
    /**
     * Allocate a new communicator.
     */
    public Communicator() {
        lock =new Lock();
		sleepSpeakers=new Condition(lock);
		sleepListeners=new Condition(lock);
        Speaking = new Condition(lock);
		used=false;
    }

    /**
     * Wait for a thread to listen through this communicator, and then transfer
     * <i>word</i> to the listener.
     *
     * <p>
     * Does not return until this thread is paired up with a listening thread.
     * Exactly one listener should receive <i>word</i>.
     *
     * @param	word	the integer to transfer.
     */
    public void speak(int word) {

    }

    /**
     * Wait for a thread to speak through this communicator, and then return
     * the <i>word</i> that thread passed to <tt>speak()</tt>.
     *
     * @return	the integer transferred.
     */    
    public int listen() {
	return 0;
    }
    private int listener;
    private Lock lock;
    private Condition sleepSpeakers;
    private Condition sleepListeners;
    private Condition Speaking;
    private boolean used;
    


}


