/**
 * Builds the graphical interface for the simulation.
 * 
 * @author Adam Fischbach - Widener University
 * @version Fall 2012
 */

import javax.swing.*;
import javax.swing.border.*;
import java.awt.event.*;
import java.awt.*;

public class GUI {

    private JFrame frame;			
    private JLabel[] readyQueue;				// Display of SimThreads in Queued state
    private SimThreadInfoPanel[] threadInfo;
    private int inQueue;						// Number of SimThreads in Queued state
    private JLabel preemption;
    private JLabel quantum;
    private JCheckBoxMenuItem logFile;
    private JCheckBoxMenuItem terminal;
    private JButton run;
    private JButton slow;
    private JButton slower;
    private JButton slowest;
    private OS os;
    
    public GUI(OS os) {
        inQueue = 0;
        this.os = os;
        makeFrame();
    }
    
    /**
     * Updates displayed state of specified SimThread and the list of Queued SimThreads.
     * @param t SimThread to update
     */
    public void updateStatus(SimThread t) {
        SimThread.State state = t.getState();
        int id = t.getID();
        threadInfo[id].updateState(state.toString());
        if (state == SimThread.State.QUEUED) {
            for(int i=inQueue; i>0; i--) {
                readyQueue[i].setText(readyQueue[i-1].getText());
            }
            readyQueue[0].setText(id+"");
            inQueue++;
        } else if (state == SimThread.State.RUNNING) {
            for(int i=0; i<inQueue; i++) {
                if(readyQueue[i].getText().equals(""+id)) {
                    for(int j=i; j<inQueue; j++) {
                        readyQueue[j].setText(readyQueue[j+1].getText());
                    }
                    i=inQueue;
                    inQueue--;
                }
            }
        }        
    }
    
    /**
     * Updates preempt display.
     * @param tf Is preemption on?
     */
    public void updatePreemption(boolean tf) {
        String onoff = tf ? "ON" : "OFF";
        preemption.setText("Preemption: " + onoff);
    }
    
    /**
     * Updates time quantum display.
     * @param q Time quantum.
     */
    public void updateQuantum(int q) {
        quantum.setText("Quantum: " + q);
    }
    
    /**
     * Updates priority display for specified SimThread.
     * @param t SimThread to update
     */
    public void updatePriority(SimThread t) {
        threadInfo[t.getID()].updatePriority(t.getPriority());
    }
    
    /**
     * Increments the displayed queue (wait) time for the specified SimThread.
     * @param t SimThread to update.
     */
    public void incrementQueueTime(SimThread t) {
        threadInfo[t.getID()].incrementQueueTime();
    }
    
    /**
     * Updates the displayed remaining run time for the specified SimThread.
     * @param t SimThread to update.
     * @param time Remaining run time. 
     */
    public void updateRunTime(SimThread t, int time) {
        threadInfo[t.getID()].updateRunTime(time);
    }
    
    /**
     * Updates the displayed remaining block time for the specified SimThread.
     * @param t SimThread to update.
     * @param time Remaining block time.
     */
    public void updateBlockTime(SimThread t, int time) {
        threadInfo[t.getID()].updateBlockTime(time);
    }
    
    /**
     * Returns true if terminal output check box is selected.
     * @return true if terminal output selected.
     */
    public boolean terminalOut() {
        return terminal.getState();
    }
    
    /**
     * Returns true if log file output check box is selected.
     * @return true if log file output selected.
     */
    public boolean fileOut() {
        return logFile.getState();
    }
    
    /**
     * De-selects the log file output check box.
     */
    public void cancelFile() {
        logFile.setState(false);
    }
    
    private void makeFrame() {
        frame = new JFrame("Thread Scheduling");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        frame.setJMenuBar(createMenuBar());
        
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.add(createQueuePanel(), BorderLayout.NORTH);
        panel.add(createSidePanel(), BorderLayout.EAST);
        panel.add(createMainPanel(), BorderLayout.CENTER);
        frame.add(panel);
        
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    
    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        JMenu output = new JMenu("Output");
        logFile = new JCheckBoxMenuItem("Log file");
        terminal = new JCheckBoxMenuItem("Terminal");
        output.add(logFile);
        output.add(terminal);
        menuBar.add(output);
        
        return menuBar;
    }
    
    private JPanel createQueuePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        panel.add(new JLabel("Ready Queue: "), BorderLayout.WEST);
        
        JPanel queue = new JPanel(new GridLayout(1, OS.MAX_THREADS, 5, 5));
        readyQueue = new JLabel[OS.MAX_THREADS+1];
        for(int i=0; i<OS.MAX_THREADS+1; i++) {
            readyQueue[i] = new JLabel("-", SwingConstants.CENTER);
            readyQueue[i].setBorder(BorderFactory.createLoweredBevelBorder());
            readyQueue[i].setBackground(Color.WHITE);
            readyQueue[i].setOpaque(true);
            queue.add(readyQueue[i]);
        }
        panel.add(queue, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createSidePanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        
        JPanel qp = new JPanel(new GridLayout(2, 1, 5, 5));
        quantum = new JLabel("Quantum: 1", SwingConstants.LEFT);
        preemption = new JLabel("Preemption: OFF", SwingConstants.LEFT);
        qp.add(quantum);
        qp.add(preemption);
        panel.add(qp, BorderLayout.SOUTH);
        
        JPanel centerPanel = new JPanel(new GridLayout(3, 1));
        
        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        buttonPanel.setBorder(new CompoundBorder(new EtchedBorder(), new EmptyBorder(5, 5, 5, 5)));
        run = new JButton("RUN");
        run.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(os.isStopped()) {
                    run.setText("PAUSE");
                } else {
                    run.setText("RUN");
                }
                os.startStop();
            }
        });
        buttonPanel.add(run);
        JButton step = new JButton("STEP");
        step.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                run.setText("RUN");
                os.step();
            }
        });
        buttonPanel.add(step);
        
        JPanel speedPanel = new JPanel(new GridLayout(1, 3, 5, 5));
        slow = new JButton("1");
        slow.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                slow.setEnabled(false);
                slower.setEnabled(true);
                slowest.setEnabled(true);
                os.speed(250);
            }
        });
        speedPanel.add(slow);
        slower = new JButton("2");
        slower.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                slower.setEnabled(false);
                slow.setEnabled(true);
                slowest.setEnabled(true);
                os.speed(500);
            }
        });
        speedPanel.add(slower);
        slowest = new JButton("3");
        slowest.setEnabled(false);
        slowest.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                slowest.setEnabled(false);
                slow.setEnabled(true);
                slower.setEnabled(true);
                os.speed(1000);
            }
        });
        speedPanel.add(slowest);
        buttonPanel.add(speedPanel);
        
        centerPanel.add(new JPanel());
        centerPanel.add(buttonPanel);
        centerPanel.add(new JPanel());
        
        panel.add(centerPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createMainPanel() {
        JPanel panel = new JPanel(new GridLayout(OS.MAX_THREADS+1, 1, 5, 5));
        
        JPanel headings = new JPanel(new GridLayout(1, 6, 5, 5));
        JLabel t = new  JLabel("Thread", SwingConstants.CENTER);
        headings.add(t);
        JLabel p = new  JLabel("Priority", SwingConstants.CENTER);
        headings.add(p);
        JLabel s = new  JLabel("State", SwingConstants.CENTER);
        headings.add(s);
        JLabel r = new JLabel("Run Time", SwingConstants.CENTER);
        headings.add(r);
        JLabel b = new JLabel("Blocked", SwingConstants.CENTER);
        headings.add(b);
        JLabel q = new  JLabel("Queued", SwingConstants.CENTER);
        headings.add(q);
        panel.add(headings);
        
        threadInfo = new SimThreadInfoPanel[OS.MAX_THREADS];
        for(int i=0; i<OS.MAX_THREADS; i++) {
            threadInfo[i] = new SimThreadInfoPanel(i, -1, -1, -1, "NONE");
            panel.add(threadInfo[i]);
        }
        
        return panel;
    }
    
}