package Simulator;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

/**
 * Detects clicks on output points and begins wire drawing process. Cannot be Sim inner class as it requires static
 * field to turn off other instances once an output has been clicked.
 */
public class WireConnectionListener implements MouseListener {

	static boolean active = true;
	CircuitBlock block;
	Output output;
	ArrayList<Wire2ndPointConnectionListener> list;

	public WireConnectionListener(CircuitBlock block, Output output) {
		this.block = block;
		this.output = output;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (output.outPoint.contains(e.getPoint()) && active) {
			//On click add listener to every possible connection input
			list = new ArrayList<Wire2ndPointConnectionListener>();
			int i = 0;
			for (CircuitBlock block: Sim.circuitArea.currentChip.blocks) {
				for (Input input : block.inputs) {
					if (block != this.block && !input.linkedStatus) { //Don't allow connection to same block
						list.add(new Wire2ndPointConnectionListener(input));
						Sim.circuitArea.addMouseListener(list.get(i));
						i++;
					}
				}				
			}

			//If no listeners were made, there are no inputs on the board.
			if (i == 0) {
				Sim.infoLabel.setForeground(Sim.RED);
				Sim.infoLabel.setText("There are no inputs to connect to.");
				return;
			}

			active = false;
			output.clicked = Sim.YELLOW;
			Sim.infoLabel.setForeground(Sim.GREEN);
			Sim.infoLabel.setText("Select a block input to connect to.");

		}				
	}
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}

	/**
	 * Detects clicks on input points (as well as intermediate clicks to create bends) and completes wire drawing 
	 * process.
	 */
	public class Wire2ndPointConnectionListener implements MouseListener {

		Input input;
		ArrayList<Point> bends = new ArrayList<Point>();

		public Wire2ndPointConnectionListener(Input input) {
			this.input = input;
		}

		public void mouseClicked(MouseEvent e) {
			if (input.inPoint.contains(e.getPoint())) {
				if (!input.linkedStatus) {

					//Create wire and link
					Sim.wireLink(input, output, bends);
					this.bends.clear();

					//Remove all the connection listeners once a selection is made
					for (Wire2ndPointConnectionListener l : list) {
						Sim.circuitArea.removeMouseListener(l);
					}
					active = true;
					output.clicked = Sim.DARKGREY;
					Sim.infoLabel.setText("");
				}
			}
			else {
				this.bends.add(e.getPoint());
			}
		}
		public void mousePressed(MouseEvent e) {}
		public void mouseReleased(MouseEvent e) {}
		public void mouseEntered(MouseEvent e) {}
		public void mouseExited(MouseEvent e) {}


	}

}