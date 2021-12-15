package Simulator;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.JPanel;

/**
 * Interactive panel that hosts the placed circuit blocks and drawn wires. Hosts all listeners to enable interaction
 * with drawn blocks. Contains a currentChip in which all placed elements are stored to enable the current displayed 
 * circuit to saved as a block to be inserted in a different circuit.
 */
@SuppressWarnings("serial")
public class InteractivePanel extends JPanel implements Runnable, Cloneable {

	Chip currentChip;
	
	public InteractivePanel(Chip currentChip) {
		this.currentChip = currentChip;
	}
	
	public void paintComponent(Graphics g) {	
		
		Graphics2D g2 = (Graphics2D)g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		super.paintComponent(g2);

		for (Wire wire : currentChip.wires) {
			wire.draw(g2);
		}
		
		for (CircuitBlock block : currentChip.blocks) {
			block.draw(g2);
		}
	}	
	
	/**
	 * Redraw the board at set frequency.
	 */
	public void run() {

		while (true) {
			repaint();
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Remove all listeners currently added to the board.
	 */
	public void removeAllListeners() {
		
		for(MouseListener l : this.getMouseListeners() ) {
			this.removeMouseListener(l);
		}
		for(MouseMotionListener l : this.getMouseMotionListeners()) {
			this.removeMouseMotionListener(l);
		}
	}
}