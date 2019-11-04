/**
 * Objects of this class represent threads in the simulation.
 * Each SimThread just maintains information about itself.  
 * SimThreads are not created as actual threads.
 * 
 * @author Adam Fischbach - Widener University
 * @version Fall 2014
 */

import java.util.Random;

public class SimThread {

    public static enum State {	// SimThread states
    	
    	QUEUED, RUNNING, BLOCKED, DONE;
    
    	public String toString() {
    		switch(this) {
    		case QUEUED: return "Queued";
    		case RUNNING: return "Running";
    		case BLOCKED: return "Blocked";
    		case DONE: return "Done";
    		default: return "Unknown State";
    		}
    	}
    };
	
    private static Random rand = new Random();
    
    private int CPUburst;      // Length in seconds of CPU burst.
    private int waitTime;      // Length in seconds of wait time.
    private int priority;      // Thread's priority...not used by FCFS or Round Robin.
    private State state;       // Thread's state
    private int id;            // Thread's ID number so simulation can keep track of it.
    private int totalCycles;   // Number of times thread must complete cycle of all three states.
    private int runCounter;    // How many time units thread has spent in Running state.
    private int blockCounter;  // How many time units thread has spent in Blocked state.
    private GUI gui;		   // Reference to GUI object.
    
    /**
     * Constructor.  Creates a SimThread with random CPU burst (3-10) and wait (15-60) times.
     * @param id SimThread's ID number.
     * @param priority SimThread's priority.
     */
    public SimThread(int id, GUI gui, int priority)
    {
        this(id, gui, rand.nextInt(8) + 3, rand.nextInt(46) + 15, priority, 10);
    }
    
    /**
     * Constructor.  Creates a SimThread with given properties.
     * @param id SimThread's ID number.
     * @param burst Length (in seconds) of thread's CPU burst.
     * @param wait Length (in seconds) of thread's wait time.
     * @param priority SimThread's priority.  (Duh.)
     * @param cycles Number of times SimThread completes cycle of all three states.
     */
    public SimThread(int id, GUI gui, int burst, int wait, int priority, int cycles)
    {
        CPUburst = burst;
        waitTime = wait;
        this.priority = priority;
        state = State.QUEUED;
        this.id = id;
        totalCycles = cycles;
        this.gui = gui;
        runCounter = burst;
        blockCounter = wait;
        gui.updatePriority(this);
        gui.updateRunTime(this, burst);
        gui.updateBlockTime(this, wait); 
    }
    
    /**
     * @return Current state.
     */
    public State getState() { return state; }
    
    /**
     * @return SimThread's ID number.
     */
    public int getID() { return id; }

    /**
     * @return SimThread's CPU burst time.
     */
    public int getCPUBurst() { return CPUburst; }
    
    /**
     * @return Number of cycles remaining in SimThread's lifetime.
     */
    public int getCycles() { return totalCycles; }
    
    /**
     * @return SimThread's current priority.
     */
    public int getPriority() { return priority; }
    
    /**
     * Change SimThread's priority.
     * @param priority SimThread's new priority.
     */
    public void setPriority(int priority)
    {
        this.priority = priority;
        gui.updatePriority(this);
    }
    
    /**
     * Change SimThread's state.
     * @param newState SimThread's new state.
     */
    public void setState(State newState)
    {
        state = newState;
        gui.updateStatus(this);
    }
    
    /**
     * Advances time spent in current Blocked state by one time unit.
     * @return true if SimThread remains in Blocked state, false if SimThread is done waiting.
     */
    public boolean keepWaiting()
    {
    	boolean b = true;
        blockCounter--;
        if (blockCounter == 0) {
            blockCounter = waitTime;
            b = false;
        }
        gui.updateBlockTime(this, blockCounter);
        return b;
    }
    
    /**
     * Advances time spend in current Running state by one time unit.
     * @return true if SimThread stays in Running state, false if at end of CPU burst.
     */
    public boolean keepRunning()
    {
    	boolean b = true;
        runCounter--;
        if (runCounter == 0) {
            runCounter = CPUburst;
            totalCycles--;
            b = false;
        }
        gui.updateRunTime(this, runCounter);
        return b;
    }
   
}