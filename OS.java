/**
 * Simulates the scheduling of threads based on the scheduling algorithm given
 * in the Scheduler class.  The basic unit of time is the second.  (Unrealistic,
 * but it lets us see what's going on.)
 * 
 * Threads must be created using the createSimThread method.  The simulation starts
 * when the first SimThread is created.
 * 
 * @author Adam Fischbach - Widener University
 * @version Fall 2014
 */

import java.util.LinkedList;
import javax.swing.*;
import java.io.*;

public class OS {

    public static final int MAX_THREADS = 10;  // Maximum number of threads allowed.
    public static final String FILE_NAME = "thschedlog.txt";	// Name of output file
    
    private LinkedList<SimThread> threadList;   // List of all SimThreads.
    private SimThread running;                  // The SimThread in the "Running" state.
    private int numThreads;                     // Number of SimThreads created.
    private Clock clock;                        // Reference to the Clock object.
    private int quantum;                        // Length of time quantum in seconds.
    private boolean preempt;                    // Is preemption turned on?
    private Scheduler scheduler;                // Reference to Scheduler object.
    private GUI gui;							// Reference to the GUI object
    private boolean stopped;					// Is simulation stopped?
    private FileWriter file;					// Output file 
        
    /**
     * Constructor.
     */
    public OS()
    {
        threadList = new LinkedList<SimThread>();
        running = null;
        numThreads = 0;
        clock = null;
        quantum = 1;
        gui = new GUI(this);
        scheduler = new Scheduler(this);
        stopped = true;
    }
    
    /**
     * Turns preemption on and off.
     * @param tf Is preemption on?
     */
    public void setPreempt(boolean tf)
    {
        preempt = tf;
        gui.updatePreemption(tf);
    }
    
    /**
     * Returns true if preemption is on.
     * @return true is preemption is on.
     */
    public boolean getPreempt()
    {
        return preempt;
    }
    
    /**
     * Changes the time quantum.
     * @param quantum Time quantum.
     */
    public void setQuantum(int quantum)
    {
        this.quantum = quantum;
        gui.updateQuantum(quantum);
    }
    
    /**
     * Returns the current time quantum.
     * @return time quantum.
     */
    public int getQuantum()
    {
        return quantum;
    }
    
    /**
     * Returns the list of all SimThreads.
     * @return List of all SimThreads.
     */
    public LinkedList<SimThread> getThreadList()
    {
        return threadList;
    }
       
    /**
     * Returns the currently running SimThread.
     * @return Current SimThread in Running state.
     */
    public SimThread getRunning()
    {
        return running;
    }
    
    /**
     * Preempts the currently running SimThread in favor of the specified SimThread.
     * The specified SimThread becomes the Running SimThread.
     * The preempted SimThread (formerly Running) is enqueued.
     * @param st The SimThread to run.
     */
    public void preemptRunningThread(SimThread st)
    {
        if(st.getState() == SimThread.State.QUEUED) {
            SimThread oldRunning = running;
            setRunning(st);
            oldRunning.setState(SimThread.State.QUEUED);
            scheduler.enqueue(oldRunning, 3);
            print("Thread " + oldRunning.getID() + " has been PREEMPTED!");
        } else {
            throw new IllegalThreadStateException("SimThread: " + st.getState().toString());
        }
    }
    
    /**
     * Clock calls this method when a SimThread in the Blocked state has
     * reached the end of its wait time.  The SimThread is returned to the Queued state.
     * @param t SimThread that has reached the end of its wait time.
     */
    public void doneWaiting(SimThread t)
    {
        if (t.getState() == SimThread.State.BLOCKED) {
            print("Thread " + t.getID() + ": DONE WAITING!");
            t.setState(SimThread.State.QUEUED);
            print("\tOS: Thread " + t.getID() + " back in queue!");
            scheduler.enqueue(t, 1);
        } else {
            throw new IllegalThreadStateException("SimThread: " + t.getState().toString());
        }
    }
    
    /**
     * Clock calls this method when the Running SimThread has reached the end
     * of its CPU burst.  If the SimThread has completed all of its
     * cycles, the SimThread is done and removed from the thread list.
     * Otherwise, the SimThread's state is changed to "Blocked" and
     * the scheduler is called to schedule a new SimThread.
     * @param t The SimThread that has reached the end of its CPU burst.
     */
    public void systemCall(SimThread t)
    {
        if (t.getState() == SimThread.State.RUNNING) {
            if(t.getCycles() == 0) {
                t.setState(SimThread.State.DONE);
                print("Thread " + t.getID() + " is ALL DONE!!!");
                threadList.remove(t);
                numThreads--;
            } else {
                print("Thread " + t.getID() + ": WAITING!");
                t.setState(SimThread.State.BLOCKED);
            }
            scheduler.scheduleThread();
        } else {
            throw new IllegalThreadStateException("SimThread: " + t.getState().toString());
        }
    }
 
    /**
     * When preemption is on, the Clock calls this method when the current
     * Running SimThread exceeds the time quantum.  The SimThread is
     * returned to the Queued state and a new SimThread is scheduled.
     */
    public void interrupt()
    {
        print("<<<=== INTERRUPT ===>>>");
        if (running != null) {
            running.setState(SimThread.State.QUEUED);
            scheduler.enqueue(running, 2);
            print("Thread " + running.getID() + " has been PREEMPTED!");
        }
        scheduler.scheduleThread();
    }
    
    /**
     * Create a new SimThread with random CPU burst and wait times.
     * @param priority The new SimThread's priority.
     */
    public void createSimThread(int priority)
    {
        startSimThread(new SimThread(numThreads, gui, priority));
    }
    
    /**
     * Create a new SimThread with the given properties.
     * @param burst Length, in seconds, of CPU burst.
     * @param wait Length, in seocnds, of wait time.
     * @param priority SimThread's priority.
     * @param cycles The number of times SimThread must cycle through all three states.
     */
    public void createSimThread(int burst, int wait, int priority, int cycles)
    {
        startSimThread(new SimThread(numThreads, gui, burst, wait, priority, cycles));
    }
    
    /**
     * Get the newly created SimThread started!
     * The SimThread is placed in the Queued state and enqueued.
     * If this is the first SimThread, the method also creates the Clock as
     * a separate (real) thread.
     * @param st The newly created SimThread.
     */
    private void startSimThread(SimThread st)
    {
    	print("\n\t\t\t!!! NEW THREAD " + st.getID());
        st.setState(SimThread.State.QUEUED);
        threadList.add(st);
        numThreads++;
        scheduler.enqueue(st, 0);
        if (clock == null) {
            clock = new Clock(this, gui);
            clock.start();
        }
    }
    
    /**
     * Places the given SimThread in the Running state.
     * @param st The SimThread to be placed in Running state.
     */
    public void setRunning(SimThread st)
    {
    	if(st == null) {
    		running = null;
    		print("\tOS: CPU is idle!!!");
    	} else if(st.getState() == SimThread.State.QUEUED) {
            print("\tOS: Scheduling Thread " + st.getID() + "!");
            st.setState(SimThread.State.RUNNING);
            running = st;
            print("Thread " + running.getID() + ": RUNNING!");
        } else {
        	throw new IllegalThreadStateException("SimThread: " + st.getState().toString());
        }
    }
            
    /**
     * Writes text output to standard output and/or output file.
     * Output printed only if text output is turned on.
     * @param msg String to display to output.
     */
    public void print(String msg)
    {
        if (gui.terminalOut()) {
            System.out.println(msg);
        }
        if (gui.fileOut()) {
            if (file == null) {
                try {
                    file = new FileWriter(FILE_NAME);
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(null, "Error creating log file.");
                    gui.cancelFile();
                    return;
                }
            }
            try {
                file.write(msg+"\n");
                file.flush();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Error writing log file.");
                gui.cancelFile();
            }
        }    
    }
    
    /**
     * Returns true is simulation is currently stopped.
     * @return true if simulation is stopped.
     */
    public boolean isStopped() {
        return stopped;
    }
    
    /**
     * Sets the speed of the simulation.
     * @param n Speed in milliseconds.
     */
    public void speed(int n) {
    	if (clock != null)
    		clock.setSpeed(n);
    }
    
    /**
     * Stops (pauses) the simulation if currently running, or runs the simulation if currently stopped.
     */
    public void startStop() {
    	if (clock != null) {
    		if(stopped) {
    			stopped = false;
    			clock.go();
    		} else {
    			stopped = true;
    			clock.pause();
    		}
    	}
    }
    
    /**
     * Advances simulation one time unit.
     */
    public void step() {
    	if (clock != null) {
    		stopped = true;
    		clock.step();
    	}
    }
        
}
        
    
    
    