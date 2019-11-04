/**
 * Displays the information for a single SimThread in the simulation's window.
 * 
 * @author Adam Fischbach - Widener University
 * @version Fall 2012
 */
import javax.swing.*;
import java.awt.*;
public class SimThreadInfoPanel extends JPanel {
	
		public static final long serialVersionUID = 1;
    
        private JLabel idLabel;			// SimThread ID
        private JLabel priorityLabel;	// SimThread priority
        private JLabel stateLabel;		// SimThread's current state
        private JLabel runLabel;		// Remaining run time
        private JLabel blockLabel;		// Remaining block time
        private JLabel queueTimeLabel;	// Overall queue (wait) time
        
        public SimThreadInfoPanel(int id, int priority, int run, int block, String state) {
            setLayout(new GridLayout(1, 6, 5, 5));
            setBorder(BorderFactory.createLoweredBevelBorder());
            
            idLabel = new JLabel(id+"", SwingConstants.CENTER);
            idLabel.setBackground(Color.WHITE);
            idLabel.setOpaque(true);
            
            priorityLabel = new JLabel(priority+"", SwingConstants.CENTER);
            
            stateLabel = new JLabel(state, SwingConstants.CENTER);
            stateLabel.setBackground(Color.WHITE);
            stateLabel.setOpaque(true);
            
            runLabel = new JLabel(run+"", SwingConstants.CENTER);
            
            blockLabel = new JLabel(block+"", SwingConstants.CENTER);
            blockLabel.setBackground(Color.WHITE);
            blockLabel.setOpaque(true);
            
            queueTimeLabel = new JLabel("0", SwingConstants.CENTER);
            
            add(idLabel);
            add(priorityLabel);
            add(stateLabel);
            add(runLabel);
            add(blockLabel);
            add(queueTimeLabel);
        }
        
        public void updateState(String state) {
            Color c;
            if(state.equals("Running")) {
                c = Color.RED;
            } else if (state.equals("Blocked")) {
                c = Color.BLUE;
            } else if (state.equals("Queued")){
                c = Color.BLACK;
            } else {
                c = Color.LIGHT_GRAY;
            }
            idLabel.setForeground(c);
            priorityLabel.setForeground(c);
            stateLabel.setForeground(c);
            stateLabel.setText(state);
            runLabel.setForeground(c);
            blockLabel.setForeground(c);
        }
        
        public void updateRunTime(int r) {
            runLabel.setText(r+"");
        }
        
        public void updateBlockTime(int b) {
            blockLabel.setText(b+"");
        }
        
        public void updatePriority(int p) {
            priorityLabel.setText(p+"");
        }
        
        public void incrementQueueTime() {
            queueTimeLabel.setText(""+(Integer.parseInt(queueTimeLabel.getText())+1));
        }     
        
}