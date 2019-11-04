/**
 * Implements the scheduling algorithm used by the simulation.
 * Currently implements Round Robin (with preemption on) and FIFO (with preemption off).
 * 
 * @author Adam Fischbach - Widener University
 * @version Fall 2012
 */

import java.util.Queue;
import java.util.LinkedList;

public class Scheduler {

    private Queue<SimThread> readyQ;   // Queue of runnable threads
    private OS os;                       // Reference to the "OS"
    
    /**
     * Constructor.
     * @param os A reference to the simulation's OS object.
     */
    public Scheduler(OS os)
    {
        this.os = os;
        readyQ = new LinkedList<SimThread>();
        os.setPreempt(true);                      // Turn preemption on for Round Robin.
        os.setQuantum(3);                         // Set time quantum to 3 time units.
    }
    
    /**
     * The simulation calls this method when a SimThread enters the Queued state.
     * @param st The SimThread to place into Ready queue(s).
     * @param code Identifies why the thread is entering the queue:
     *                  0 - a new thread
     *                  1 - thread returning from Blocked state
     *                  2 - thread has exceeded its time quantum
     *                  3 - thread has been preempted for a higher priority thread
     */
    public void enqueue(SimThread st, int code)
    {
    	if(os.getRunning() == null) {
    		os.setRunning(st);
    	} else {
    		readyQ.add(st);
    	}
    }
    
    /**
     * The simulation calls this method when it's time to schedule a thread.
     * Use the os.setRunning() method to tell the simulation which thread to schedule. 
     */
    public void scheduleThread()
    {
    	if(readyQ.isEmpty()) {
    		os.setRunning(null);
    	} else {
    		os.setRunning(readyQ.remove());
    	}
    }

}