package Simulator;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;
import java.io.Serializable;

/**
 * Basic output block that displays the value of the connected wire. Acts as block output when a circuit is saved
 * to a block.
 */
public class OutputBlock extends CircuitBlock implements Serializable {

	final Color DARKGREY = new Color(36,37,38);
	final Color MIDGREY = new Color(50,51,52);
	final Color GREEN = new Color(114,211,95);
	private static final long serialVersionUID = 7207597394455400737L;
	boolean value = false;
	
	/**
	 * Create new output block at set position.
	 */
	public OutputBlock(int positionX, int positionY) {

		this.positionX = positionX;
		this.positionY = positionY;
		this.icon = new Rectangle(positionX, positionY, 30, 30);
		this.inputs.add(new Input());
		
		//For saving
		this.outputs.add(new Output());
	}
	
	@Override
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
		g2.fillRect(icon.x+4,icon.y+4,22,22);

		drawInPoint(g2, positionX, positionY+15);
	}


	@Override
	public void run() {
		
		while(exists) {

			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			if (this.inputs.get(0).linkedStatus) {
				this.value = this.inputs.get(0).value;
			}
			else {
				this.value = false;
			}
			this.outputs.get(0).value = this.value;
			
			this.icon = new Rectangle(positionX, positionY, 30, 30);
			this.inputs.get(0).inPoint = new Ellipse2D.Float(positionX-15,positionY+10,10,10);
		}
	}
}