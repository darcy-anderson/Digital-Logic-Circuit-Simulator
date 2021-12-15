package Simulator;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Line2D;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Drawn connection wire between an input and an output. Carries signal and displays current value graphically.
 */
public class Wire implements Runnable, Serializable {
	
	static final Color DARKGREY = new Color(36,37,38);
	static final Color GREEN = new Color(114,211,95);
	private static final long serialVersionUID = 6694486147112923143L;
	boolean exists = true;
	boolean value;
	Output start;
	Input end;
	ArrayList<Point> bends; //If intermediate clicks are input during wire drawing, wire will have bends

	/**
	 * Create new wire between input and output.
	 */
	public Wire(Output start, Input end, ArrayList<Point> bends) {
		
		this.start = start;
		this.end = end;
		this.value = start.value;
		this.bends = new ArrayList<Point>(bends);
	}
	
	public void draw(Graphics g) {

		Graphics2D g2 = (Graphics2D)g;
		g2.setStroke(new BasicStroke(2));
		if(value) {
			g2.setColor(GREEN);
		}
		else {
			g2.setColor(DARKGREY);
		}
		
		int runningX = (int)start.outPoint.x+5;
		int runningY = (int)start.outPoint.y+5;
		for (Point bend : this.bends) {
			g2.draw(new Line2D.Float(runningX, runningY, bend.x, bend.y));
			runningX = bend.x;
			runningY = bend.y;
		}
		g2.draw(new Line2D.Float(runningX, runningY, end.inPoint.x+5, end.inPoint.y+5));
		
	}

	public void run() {
		
		while(exists) {
			
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.value = start.value;
			this.end.value = value;	
		}	
	}
}