/**
 * Implements the scheduling algorithm used by the simulation.
 * Currently implements Round Robin (with preemption on) and FIFO (with preemption off).
 * 
 * @author Adam Fischbach - Widener University
 * @version Fall 2012
 */

 import java.util.*;


public class Scheduler 
{
    LinkedList<SimThread>[] readyQ;//= new LinkedList<SimThread>[5];// = new Queue<SimThread>[5];
    private OS os;                       // Reference to the "OS"
    
    /**
     * Constructor.
     * @param os A reference to the simulation's OS object.
     */
    public Scheduler(OS os)//hello
    {
        this.os = os;//hello
        readyQ = (LinkedList<SimThread>[])new LinkedList<?>[5];//new LinkedList<SimThread>();//array of qs
        for (int i=0; i<5; i++)
        {
            readyQ[i] = new LinkedList<SimThread>();//initialize qs
        }
        os.setPreempt(true);                      // Turn preemption on for Round Robin.
        os.setQuantum(3);                         // Set time quantum to 3 time units.
    }
    
    /**
     * The simulation calls this method when a SimThread enters the Queued state.
     * you can get or set a Sim Thread’s priority by using the getPriority() and setPriority(int) methods, respectively, of the SimThread class
     * @param st The SimThread to place into Ready queue(s).
     * @param code Identifies why the thread is entering the queue:
     *                  0 - a new thread
     *                  1 - thread returning from Blocked state
     *                  2 - thread has exceeded its time quantum
     *                  3 - thread has been preempted for a higher priority thread
     * When scheduling a thread, always pick the available thread with the highest priority.
    • Preemption Policy: If a thread entering the Queued state (i.e. being “enqueued”) has a
    higher priority than the Running thread, then the Running thread is preempted in favor of
    the higher-priority thread.
    • If a thread exceeds its time quantum (time limit), its priority is lowered one level.
    • Whenever a thread returns from the Blocked state, its priority increases one level.
    • The Round Robin time quantum for each thread depends upon its priority as follows:
            o Priority 0 => Time Quantum is 4
            o Priority 1 => Time Quantum is 8
            o Priority 2 => Time Quantum is 16
            o Priority 3 => Time Quantum is 25
            o Priority 4 => Time Quantum is 40
     */
    public void enqueue(SimThread st, int code)
    {
        //int q;
    	if(os.getRunning() == null) 
        {
    		os.setRunning(st);
    	} 
        else 
        {
            int p = st.getPriority();//new thread
            int p1 = os.getRunning().getPriority();//curr thread
            if (code==2)
            {
                p=p-1;
            }
            else if (code==1)
            {
                p=p+1;
            }
            if (p>p1)
            {
                if (p==0)
                {
                     os.setQuantum(4);
                }
                else if (p==1)
                {
                    os.setQuantum(8);
                }
                else if (p==2)
                {
                    os.setQuantum(16);
                }
                else if (p==3)
                {
                    os.setQuantum(25);
                }
                else if (p==4)
                {
                    os.setQuantum(40);
                }
                else
                {
                    System.out.println("error bc p!=0,1,2,3,or 4");
                }
                readyQ[p1].add(os.getRunning());
                os.setRunning(st);
            }
            else
            {
                readyQ[p].add(st);
            }
    	}
    }
    
    /**
     * The simulation calls this method when it's time to schedule a thread.
     * Use the os.setRunning() method to tell the simulation which thread to schedule. 
     */
    public void scheduleThread()
    {
    	if(readyQ.length==0) 
        {
    		os.setRunning(null);
    	} 
        else 
        {
            //int p = os.getRunning().getPriority();//curr thread priority
            int i = 0;
            while (i<5)
            {
                if (!readyQ[i].isEmpty())
                {
                    SimThread best = readyQ[i].peek();//get first element of array that has priority i (checks from 0 to 4)
                    os.setRunning(best);
                    readyQ[i].remove(best);
                    return;//break;//exit;
                }
                else
                {
                    i++;
                }
            }
    	}
    }
}