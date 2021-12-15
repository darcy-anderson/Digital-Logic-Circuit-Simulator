package Simulator;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Base abstract class that chip and I/O blocks extend. Defines fields required of both block types. A thread is
 * started per CircuitBlock to monitor input values and update output values accordingly.
 */
public abstract class CircuitBlock implements Runnable, Serializable {

	static final Color DARKGREY = new Color(36,37,38);
	private static final long serialVersionUID = -3664184576466944073L;
	boolean exists = true; //For running threads
	boolean dragging = false;
	int positionX, positionY;
	Rectangle icon; //Used to draw shape of the component and detect clicks to move
	ArrayList<Input> inputs = new ArrayList<Input>();
	ArrayList<Output> outputs = new ArrayList<Output>();
	
	public abstract void draw(Graphics g);
	
	public abstract void run();
	
	/**
	 * Draws input connection toggle at specified positon.
	 */
	public void drawInPoint(Graphics2D g, int x, int y) {
		
		g.setColor(DARKGREY);
		g.setStroke(new BasicStroke(2));
		g.draw(new Line2D.Float(x, y, x-5, y));
		g.fillOval(x-15, y-5, 10, 10);
	}
	
	/**
	 * Draws output connection toggle at specified position.
	 */
	public void drawOutPoint(Graphics2D g, int x, int y, Color clicked) {
		
		g.setColor(DARKGREY);
		g.setStroke(new BasicStroke(2));
		g.drawLine(x, y, x+5, y);
		g.setColor(clicked);
		g.fillOval(x+5, y-5, 10, 10);
	}
}