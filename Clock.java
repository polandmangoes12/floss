/**
 * The Clock runs as a separate thread from the rest of the simulation,
 * updating the status of each SimThread once per time unit.
 * 
 * @author Adam Fischbach - Widener University
 * @version Fall 2012
 */

import javax.swing.JOptionPane;

public class Clock extends Thread {

    private OS os;              // Reference to OS object.
    private GUI gui;			// Reference to GUI object
    private int speed;			// A single time unit of simulation (in milliseconds)
    private boolean stopped;	// Is simulation stopped?
    private boolean stepping;	// Is simulation being stepped through?
    
    /**
     * Constructor.
     * @param preempt Is preemption turned on?
     * @param quantum Length (in seconds) of time quantum.
     * @param os Reference to the simulation's OS object.
     */
    public Clock(OS os, GUI gui)
    {
        this.os = os; 
        this.gui = gui;
        speed = 1000;
        stopped = true;
        stepping = true;
    }
    
    /**
     * Sets simulation's time unit in milliseconds.
     * @param speed time unit in milliseconds
     */
    public void setSpeed(int speed) {
        this.speed = speed;
    }
    
    /**
     * Method executed upon Clock thread's creation.
     * Repeatedly sleeps for one time unit and then updates the status of the SimThreads.
     */
    public void run()
    {
        boolean reset;   // Should time quantum be restarted?
        while(true) {       
            reset = false;
            for(int i=0; i<os.getQuantum() && !reset; i++) {  
            	if (stopped) nap();
                try {
                    Thread.sleep(speed);     // Sleep for one time unit.
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "Something went horribly wrong in the clock!");
                    System.exit(0);
                }
                incrementThreads();         // Update status of Queued and Blocked SimThreads.
                reset = incrementRunning(); // Update status of Running thread.
                if (stepping) stopped = true;
            }
            if (os.getPreempt() && !reset) {        // "Interrupt" if time quantum exceeded (and preemption is on).
                os.interrupt();
            }
        }
    }
    
    // Puts Clock thread to sleep.
    private synchronized void nap() {
    	try {
    		wait();
    	} catch (InterruptedException e) {
    		JOptionPane.showMessageDialog(null, "Clock Interrupted");
    		System.exit(0);
    	}
    }
    
    // Wakes up Clock thread.
    private synchronized void wakeUp() {
    	notify();
    }
    
    /**
     * Stops (pauses) simulation.
     */
    public void pause() {
        stopped = true;
    }
    
    /**
     * Starts simulation.
     */
    public void go() {
        stepping = false;
        stopped = false;
        wakeUp();
    }
    
    /**
     * Advances simulation one time unit.
     */
    public void step() {
        stepping = true;
        stopped = false;
        wakeUp();
    }
    
    /**
     * Update status of Queued and Blocked SimThreads.
     */
    private void incrementThreads()
    {
        for(SimThread st : os.getThreadList()) {
            if (st.getState() == SimThread.State.QUEUED) {
                gui.incrementQueueTime(st);
            } else if (st.getState() == SimThread.State.BLOCKED) {
                if(!st.keepWaiting()) {        // Increment time spent in Waiting state
                    os.doneWaiting(st);        // Tell OS that thread is done waiting!
                }
            }
        }
    }
    
    /**
     * Update status of Running SimThread.
     * @return true if SimThread has reached the end of its CPU burst.
     */
    private boolean incrementRunning()
    {
        SimThread st = os.getRunning();
        if (st != null && !st.keepRunning()) {  // Increment time spent in Running state
            os.systemCall(st);                  // SimThread done running...issues "system call"
            return true;
        }
        return false;
    }
        
}