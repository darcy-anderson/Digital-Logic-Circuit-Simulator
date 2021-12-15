package Simulator;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;

/**
 * Chip block with variable number of inputs and outputs, with output values depending on internal logic. Can contain
 * internal blocks and wires for operation of saved chips.
 */
public class Chip extends CircuitBlock {
	
	static final Color DARKGREY = new Color(36,37,38);
	static final long serialVersionUID = 1473004494465243128L;
	
	String name;
	boolean saved = false;
	ArrayList<CircuitBlock> blocks = new ArrayList<CircuitBlock>();
	ArrayList<Wire> wires = new ArrayList<Wire>();
	
	//Fields for icon drawing
	int iconHeight;
	int iconWidth;
	int inSectionHeight;
	int outSectionHeight;
	
	/**
	 * Create blank chip to be loaded into the board's currentChip.
	 */
	public Chip() {}
	
	/**
	 * For creating basic gates at set location.
	 */
	public Chip(int positionX, int positionY, int inputs, ArrayList<BoolExp> boolExps, String name) {
		
		this.name = name;
		this.positionX = positionX;
		this.positionY = positionY;
		
		for (int i = 0; i < inputs; i++) {
			this.inputs.add(new Input());
		}
		
		for (int i = 0; i < boolExps.size(); i++) {
			this.outputs.add(new Output());
			this.outputs.get(i).exp = boolExps.get(i);
		}
		
		iconHeight = inputs >= boolExps.size() ? inputs * 30 : boolExps.size() * 30;
		this.icon = new Rectangle(positionX, positionY, iconWidth, iconHeight);
		inSectionHeight = iconHeight / (this.inputs.size()+1);
		outSectionHeight = iconHeight / (this.outputs.size()+1);
		
	}

	public void draw(Graphics g) {

		Graphics2D g2 = (Graphics2D)g;
		g2.setColor(DARKGREY);
		iconWidth = g2.getFontMetrics().stringWidth(name) + 10;
		g2.fillRect(positionX, positionY, iconWidth, iconHeight);
		
		g2.setColor(Color.WHITE);
		g2.drawString(name, positionX+5, positionY+4+(iconHeight/2));
		
		//Equally space inputs and outputs along chip
		int drawLocation = this.inSectionHeight;
		for (int i = 0; i < this.inputs.size(); i++) {
			drawInPoint(g2, positionX, positionY+drawLocation);
			drawLocation += inSectionHeight;
		}

		drawLocation = outSectionHeight;
		for (int i = 0; i < this.outputs.size(); i++) {
			drawOutPoint(g2, positionX+iconWidth, positionY+drawLocation, this.outputs.get(i).clicked);
			drawLocation += outSectionHeight;
		}
	}
	
	/**
	 * Clears all internal blocks and wires.
	 */
	public void clearChip() {
		
		this.inputs.clear();
		this.outputs.clear();
		this.blocks.clear();
		this.wires.clear();
	}
	
	/**
	 * Sets output value for basic gates.
	 */
	public boolean evaluator(BoolExp exp) {
		
		if (this.inputs.size() == 1) {
			return exp.exp(this.inputs.get(0).value, this.inputs.get(0).value);
		}
		return exp.exp(this.inputs.get(0).value, this.inputs.get(1).value);
	}
	
	/**
	 * Prepares fields for the board's currentChip to enable placing and proper function after saving and inserting into
	 * a new board.
	 */
	public void formatSave(String name) {
		
		this.name = name;
		int inputsNo = 0;
		int outputsNo = 0;
		
		for (int i = 0; i < this.blocks.size(); i++) {
			
			CircuitBlock block = this.blocks.get(i);
			
			if (block instanceof InputBlock) {
				((InputBlock)block).interact = false;
				inputsNo++;
			}
			if (block instanceof OutputBlock) {
				outputsNo++;
			}
		}
		
		saved = true;
		iconHeight = inputsNo >= outputsNo ? inputsNo * 30 : outputsNo * 30;
		inSectionHeight = iconHeight / (this.inputs.size()+1);
		outSectionHeight = iconHeight / (this.outputs.size()+1);
	}

	public void run() {
	
		while(this.exists) {

			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			this.icon = new Rectangle(positionX, positionY, iconWidth, iconHeight);
			
			for (int i = 0; i < this.inputs.size(); i++) {
				int drawLocation = this.inSectionHeight + (i * this.inSectionHeight);
				this.inputs.get(i).inPoint = new Ellipse2D.Float(positionX-15,positionY+drawLocation-5,10,10);
			}
			for (int i = 0; i < this.outputs.size(); i++) {
				int drawLocation = this.outSectionHeight + (i * this.outSectionHeight);
				this.outputs.get(i).outPoint = new Ellipse2D.Float(positionX+5+iconWidth,positionY+drawLocation-5,10,10);
			}
			
			if (!this.saved) {
				for (Output output : this.outputs) {
					output.value = evaluator(output.exp);
				}	
			}
		}
	}
	
	/**
	 * Print info for debugging
	 */
	public String toString(String print) {
		
		print += "[name=" + this.name + ", ";
		print += "saved=" + this.saved + ", ";
		print += "inputsNO=" + this.inputs.size() +", ";
		print += "outputsNO=" + this.outputs.size() + "]\n";
		
		for (CircuitBlock chip : this.blocks) {
			if (chip instanceof Chip && ((Chip)chip).saved) print += ((Chip)chip).toString(print);
		}
		
		return print;
	}
}