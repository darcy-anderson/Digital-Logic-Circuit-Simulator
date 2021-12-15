package Simulator;

import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Defines input and output objects
 */
public abstract class IO implements Serializable {

	private static final long serialVersionUID = 9075397406112705806L;
}

/**
 * Connection points for blocks. Receives signal from wires and passes to block. Only accepts a single connection.
 */
class Input implements Serializable {

	private static final long serialVersionUID = -4717611511142890871L;
	boolean value = false;
	Ellipse2D.Float inPoint = new Ellipse2D.Float(); //For use with click listeners
	boolean linkedStatus = false;
	Wire linkedWire;
	
	/**
	 * Links a wire with this input.
	 */
	public void link(Wire wire) {
		
		this.linkedStatus = true;
		this.linkedWire = wire;
	}
	
	/**
	 * Disconnects this input's linked wire.
	 */
	public void removeLink() {
		
		this.value = false;
		this.linkedStatus = false;
		this.linkedWire = null;
	}	
}

/**
 * Connection points for blocks. Passes signal to wires from block. Can accept multiple connections.
 */
class Output implements Serializable {
	
	private static final long serialVersionUID = -7598207453668289337L;
	boolean value = false;
	Ellipse2D.Float outPoint = new Ellipse2D.Float(); //For use with click listeners
	Color clicked = new Color(36,37,38); //Sets draw color when clicked
	ArrayList<Wire> linkedWires = new ArrayList<Wire>();
	BoolExp exp; //To define output values for basic gates
	
	/**
	 * Links a wire with this output.
	 */
	public void link(Wire wire) {
		
		this.linkedWires.add(wire);
	}
	
	/**
	 * Disconnects one wire from this output's linked wires.
	 */
	public void removeLink(Wire wire) {
		
		this.linkedWires.remove(wire);
	}
}