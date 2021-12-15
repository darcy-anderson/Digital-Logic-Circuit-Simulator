package Simulator;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;

/**
 * Basic input block that sets the value of the connected wire. Can be toggled on click, or set as a clock signal with
 * set frequency. Acts as block input when a circuit is saved to a block.
 */
public class InputBlock extends CircuitBlock {

	enum InputType {
	    TOGGLE,
	    CLOCK
	}
	
	static final Color DARKGREY = new Color(36,37,38);
	static final Color MIDGREY = new Color(50,51,52);
	static final Color GREEN = new Color(114,211,95);
	private static final long serialVersionUID = -6875768262928158623L;
	static boolean clockLimit = false; //Limits to one placed clock input
	boolean interact = true; //Takes input values from external wires if this input is in a saved block
	boolean value = false;
	InputType type;
	Ellipse2D.Float circle; // Used to draw the value display and detect clicks
	
	/**
	 * Create new input block of specified type at set position.
	 */
	public InputBlock(int positionX, int positionY, InputType type) {

		this.positionX = positionX;
		this.positionY = positionY;
		this.type = type;
		this.icon = new Rectangle(positionX, positionY, 30, 30);
		this.circle = new Ellipse2D.Float(positionX+5,positionY+5,20,20);
		if (type == InputType.CLOCK) {
			clockLimit = !clockLimit;
		}
		this.outputs.add(new Output());
		
		//For saving
		this.inputs.add(new Input());
	}
		
	public void draw(Graphics g) {

		Graphics2D g2 = (Graphics2D)g;
		g2.setColor(DARKGREY);
		g2.fillRect(icon.x, icon.y, icon.width, icon.height);
		
		if (this.value) {
			g2.setColor(GREEN);
		}
		else {
			g2.setColor(MIDGREY);
		}
		g2.fillOval((int)circle.x, (int)circle.y, (int)circle.width, (int)circle.height);
		
		drawOutPoint(g2, positionX+30, positionY+15, outputs.get(0).clicked);
		
	}
	
	public void run() {
		
		int i = 0;
		
		while(exists) {
			
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			this.outputs.get(0).value = value;
			
			if (!interact) {
				this.value = this.inputs.get(0).value;
			}
			
			this.icon = new Rectangle(positionX, positionY, 30, 30);
			this.circle = new Ellipse2D.Float(positionX+5,positionY+5,20,20);
			this.outputs.get(0).outPoint = new Ellipse2D.Float(positionX+35,positionY+10,10,10);

			//Flip value at set frequency for clock input only
			if (i == 200 && type == InputType.CLOCK) {
				this.value = !this.value;
				i = 0;
			}
			if (type == InputType.CLOCK) {
				i++;
			}
		}
	}
}