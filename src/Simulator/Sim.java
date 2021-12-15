package Simulator;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.BorderFactory;
import javax.swing.text.BadLocationException;
import javax.swing.text.Utilities;

import Simulator.InputBlock.InputType;

import java.sql.*;

public class Sim {

	final int[] frameSize = { 1280, 720 };
	private JFrame frame = new JFrame("Digital Logic Circuit Simulator");
	private CardLayout cLayout = new CardLayout();
	private JPanel cards = new JPanel(cLayout);
	static InteractivePanel circuitArea;
	static JLabel infoLabel = new JLabel("", JLabel.CENTER); //Displays contextual hints based on user input
	
	static Chip currentChip = new Chip(); //Associated with interactive area, all blocks/wires are added to this.
										  //This enables easy conversion to a placeable block when saved.
	private JButton saveChipButton;
	private JTextField saveField;
	private JButton insertChipButton;
	private JButton deleteChipButton;
	private JTextArea textArea;

	//Colors for UI.
	static final Color DARKGREY = new Color(36, 37, 38);
	static final Color MIDGREY = new Color(50, 51, 52);
	static final Color LIGHTGREY = new Color(80, 81, 82);
	static final Color GREEN = new Color(114, 211, 95);
	static final Color YELLOW = new Color(236, 167, 44);
	static final Color RED = new Color(193, 73, 83);

	public Sim() {

		try { // Set cross-platform Java L&F (also called "Metal") to be consistent with
				// different OS
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (UnsupportedLookAndFeelException e) {
			// handle exception
		} catch (ClassNotFoundException e) {
			// handle exception
		} catch (InstantiationException e) {
			// handle exception
		} catch (IllegalAccessException e) {
			// handle exception
		}

		// Create and add all cards, default to home on start
		cards.add(createCardHome(), "HOME");
		cLayout.show(cards, "HOME");
		cards.add(createCardCircuit(), "CIRCUIT");
		cards.add(createCardInstructions(), "INSTRUCTIONS");

		frame.add(cards);
		frame.setSize(frameSize[0], frameSize[1]);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	/**
	 * Creates button style used throughout program.
	 */
	public JButton createButton(String label, int fontSize) {

		JButton button = new JButton(label);

		button.setBackground(DARKGREY);
		button.setForeground(Color.WHITE);
		button.setFocusPainted(false);
		button.setBorderPainted(false);
		button.setFont(new Font("Dialog", Font.PLAIN, fontSize));

		button.addMouseListener(new MouseAdapter() { // Change color on rollover
			public void mouseEntered(MouseEvent e) {
				button.setBackground(YELLOW);
				button.setForeground(Color.BLACK);
			}

			public void mouseExited(MouseEvent e) {
				button.setBackground(DARKGREY);
				button.setForeground(Color.WHITE);
			}
		});

		return button;
	}
	
	/**
	 * Creates program home screen page for card layout.
	 */
	public JPanel createCardHome() {
		
		JPanel cardHome = new JPanel();
		cardHome.setBackground(MIDGREY);
		cardHome.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();

		JLabel title = new JLabel("DIGITAL LOGIC CIRCUIT SIMULATOR");
		title.setFont(new Font("Dialog", Font.BOLD, 50));
		title.setForeground(GREEN);
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridheight = 2;
		gbc.weighty = 0.7;
		gbc.weightx = 1; // Makes column fill full horizontal extents
		cardHome.add(title, gbc);

		JPanel spacer1 = new JPanel();
		spacer1.setOpaque(false);
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridheight = 1;
		gbc.weighty = 0.05;
		cardHome.add(spacer1, gbc);

		JButton menu1 = createButton("NEW CIRCUIT", 30);
		menu1.addActionListener(e -> {
			cLayout.show(cards, "CIRCUIT");
		});
		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.gridheight = 1;
		gbc.weighty = 0.05;
		cardHome.add(menu1, gbc);

		JButton menu3 = createButton("INSTRUCTIONS", 30);
		menu3.addActionListener(e -> {
			cLayout.show(cards, "INSTRUCTIONS");
		});
		gbc.gridx = 0;
		gbc.gridy = 4;
		gbc.gridheight = 1;
		gbc.weighty = 0.05;
		cardHome.add(menu3, gbc);

		JButton menu4 = createButton("CLOSE", 30);
		menu4.addActionListener(e -> System.exit(0));
		gbc.gridx = 0;
		gbc.gridy = 5;
		gbc.gridheight = 1;
		gbc.weighty = 0.05;
		cardHome.add(menu4, gbc);

		JPanel spacer2 = new JPanel();
		spacer2.setOpaque(false);
		gbc.gridx = 0;
		gbc.gridy = 6;
		gbc.gridheight = 1;
		gbc.weighty = 0.5;
		cardHome.add(spacer2, gbc);

		JLabel sig = new JLabel("Made by D'Arcy Anderson for CS-GY 9053 Fall 2021 Final Project");
		sig.setFont(new Font("Dialog", Font.BOLD, 12));
		sig.setForeground(RED);
		gbc.gridx = 0;
		gbc.gridy = 7;
		gbc.gridheight = 1;
		gbc.weighty = 0;
		gbc.anchor = GridBagConstraints.WEST;
		cardHome.add(sig, gbc);

		return cardHome;
	}

	/**
	 * Creates program instructions page for card layout.
	 */
	public JPanel createCardInstructions() {

		JPanel cardInstructions = new JPanel();
		cardInstructions.setBackground(new Color(50, 51, 52));
		cardInstructions.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();

		JLabel titleInstructions = new JLabel("INSTRUCTIONS");
		titleInstructions.setFont(new Font("DIALOG",Font.BOLD, 30));
		titleInstructions.setForeground(YELLOW);
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridheight = 1;
		gbc.weighty = 0.5;
		cardInstructions.add(titleInstructions, gbc);
		
		JTextArea instructions = new JTextArea(30,30);
		instructions.setEditable(false);
		instructions.setBackground(MIDGREY);
		instructions.setForeground(Color.WHITE);
		instructions.setFont(new Font("DIALOG", Font.PLAIN, 18));
		
		instructions.append("The \"CREATE CIRCUIT\" button will take you to the interactive circuit building board.\n\n");
		instructions.append("From this screen, use the \"INSERT\" menu to select input blocks, output blocks, or basic gates. Once selected, click on the board to place them.\n");
		instructions.append("You can left-click on a toggled input block to change it's value. The value of a clock input block will change periodically automatically.\n\n");
		instructions.append("You can left-click on a block and drag to change its position.\n");
		instructions.append("You can right-click on a block to delete it.\n\n");
		instructions.append("Click on a block's output point to begin drawing a wire. Click on a different block's input to make the connection.\n");
		instructions.append("Wires will progagate their input signals through the circuit.\n");
		instructions.append("Click on points on the board before clicking on an input to place bends in the wire.\n");
		instructions.append("(Clicking to place blocks and draw wires can be a little finicky, sorry! If a click doesn't register, just click again!)\n\n");
		instructions.append("Once you have drawn a circuit with at least one input and one output, the \"SAVE\" button will allow you to enter a name for the chip and save it.\n");
		instructions.append("Once saved, you can insert the saved circuit in block form using \"INSERT -> SAVED CHIP\". Select the saved chip from the list and insert.\n");
		instructions.append("To start you off, SR Latch and D Flip Flop chips are included.\n");
		instructions.append("You can also delete any saved chips from this window.\n\n");
		instructions.append("The chip retains the internal logic of the original circuit you created, and its outputs will respond to its inputs accordingly.\n\n");
		instructions.append("Have fun building up computer components with saved chips!");
		
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridheight = 1;
		gbc.weighty = 0.5;
		cardInstructions.add(instructions, gbc);
		
		JButton back = createButton("HOME", 30);
		back.addActionListener(e -> cLayout.show(cards, "HOME"));
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridheight = 1;
		gbc.weighty = 0.5;
		cardInstructions.add(back, gbc);

		return cardInstructions;
	}
	
	/**
	 * Creates program circuit builder page for card layout.
	 */
	public JPanel createCardCircuit() {

		JPanel cardCircuit = new JPanel();
		cardCircuit.setBackground(MIDGREY);
		cardCircuit.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();

		// Create menu row
		JPanel circuitMenu = new JPanel();
		circuitMenu.setBackground(MIDGREY);

		JButton insertIOButton = createButton("INSERT", 20);
		// The -1 prevents a yellow line appearing under the button on click for some
		// reason
		insertIOButton.addActionListener(
				e -> createInsertMenu(insertIOButton).show(insertIOButton, 0, insertIOButton.getHeight() - 1));
		circuitMenu.add(insertIOButton);

		// Opens save chip popup and sets infoLabel
		JButton saveButton = createButton("SAVE CHIP", 20);
		saveButton.addActionListener(e -> {
			JFrame savePopup = createSavePopup();
			savePopup.setVisible(true);
			saveChipButton.addActionListener(e1 -> {
				if (currentChip.inputs.size() == 0 || currentChip.outputs.size() == 0) {
					infoLabel.setForeground(RED);
					infoLabel.setText("Place at least one input block and one output block before saving chip.");
				} else if (saveField.getText().length() > 0) {
					String name = saveField.getText();
					savePopup.setVisible(false);
					saveChip(name, currentChip);
					infoLabel.setText("");
				} else if (saveField.getText().length() == 0) {
					infoLabel.setForeground(RED);
					infoLabel.setText("Cannot save chip with blank name.");
				}
			});
		});
		circuitMenu.add(saveButton);

		// Iterates through and removes all placed blocks and wire to show empty board
		JButton clearButton = createButton("CLEAR", 20);
		clearButton.addActionListener(e -> {
			Iterator<CircuitBlock> iter = currentChip.blocks.iterator();
			while (iter.hasNext()) {
				CircuitBlock block = iter.next();
				if (block instanceof InputBlock) {
					deleteInputBlock((InputBlock) block, currentChip);
				} else {
					deleteBlock(block, currentChip);
				}
				iter.remove();
			}
			currentChip.clearChip();
			circuitArea.removeAllListeners();
			infoLabel.setForeground(GREEN);
			infoLabel.setText("Cleared board.");
		});
		circuitMenu.add(clearButton);

		// Clears board same as clear button, and returns to home page
		JButton homeButton = createButton("HOME", 20);
		homeButton.addActionListener(e -> {
			Iterator<CircuitBlock> iter = currentChip.blocks.iterator();
			while (iter.hasNext()) {
				CircuitBlock block = iter.next();
				if (block instanceof InputBlock) {
					deleteInputBlock((InputBlock) block, currentChip);
				} else {
					deleteBlock(block, currentChip);
				}
				iter.remove();
			}
			currentChip.clearChip();
			circuitArea.removeAllListeners();
			infoLabel.setText("");
			cLayout.show(cards, "HOME");
		});
		circuitMenu.add(homeButton);

		infoLabel.setFont(new Font("Dialog", Font.PLAIN, 20));
		infoLabel.setPreferredSize(new Dimension(770, (int) homeButton.getPreferredSize().getHeight()));
		circuitMenu.add(infoLabel);

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridheight = 1;
		gbc.weighty = 0.5;
		gbc.weightx = 1;
		gbc.anchor = GridBagConstraints.WEST;
		cardCircuit.add(circuitMenu, gbc);

		// Create interactive area
		circuitArea = new InteractivePanel(currentChip);
		circuitArea.setBorder(BorderFactory.createLineBorder(DARKGREY, 10));
		circuitArea.setBackground(LIGHTGREY);
		circuitArea.setPreferredSize(new Dimension(1255, 620));
		new Thread(circuitArea).start();
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridheight = 1;
		gbc.weighty = 0.5;
		gbc.weightx = 1;
		gbc.anchor = GridBagConstraints.CENTER;
		cardCircuit.add(circuitArea, gbc);

		return cardCircuit;
	}

	/**
	 * Creates popup window that appears to save a chip.
	 */
	public JFrame createSavePopup() {

		JFrame savePopup = new JFrame("Save Chip");
		savePopup.setResizable(false);
		savePopup.setSize(new Dimension(400, 200));
		savePopup.setLocationRelativeTo(frame);

		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		panel.setBackground(MIDGREY);

		JLabel info = new JLabel("Enter name for saved chip:");
		info.setFont(new Font("DIALOG", Font.PLAIN, 20));
		info.setForeground(Color.WHITE);

		saveField = new JTextField();
		saveField.setColumns(15);
		saveField.setFont(new Font("DIALOG", Font.PLAIN, 20));
		saveField.setHorizontalAlignment(JTextField.CENTER);

		this.saveChipButton = createButton("SAVE CHIP", 20);

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weighty = 1;
		panel.add(info, gbc);

		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.weighty = 1;
		panel.add(saveField, gbc);

		gbc.gridx = 0;
		gbc.gridy = 4;
		gbc.weighty = 1;
		panel.add(saveChipButton, gbc);

		savePopup.add(panel);

		return savePopup;
	}

	/**
	 * Creates popup window that appears to insert a saved chip.
	 */
	public JFrame createInsertChipPopup() {

		JFrame popup = new JFrame("Insert Chip");
		popup.setResizable(false);
		popup.setSize(new Dimension(350, 400));
		popup.setLocationRelativeTo(frame);

		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		panel.setBackground(MIDGREY);

		JLabel info = new JLabel("Select saved chip to insert:");
		info.setFont(new Font("DIALOG", Font.PLAIN, 20));
		info.setForeground(Color.WHITE);

		//Creates text area for saved chips list, and enables clicking to select entry
		textArea = new JTextArea(10, 17);
		textArea.setEditable(false);
		textArea.setFont(new Font("DIALOG", Font.PLAIN, 20));
		JScrollPane scrollPane = new JScrollPane(textArea);
		textArea.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				int pos = textArea.getCaretPosition();
				try {
					int start = Utilities.getRowStart(textArea, pos);
					int end = Utilities.getRowEnd(textArea, pos);
					textArea.setSelectionStart(start);
					textArea.setSelectionEnd(end);
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
			}
		});

		insertChipButton = createButton("INSERT CHIP", 17);
		deleteChipButton = createButton("DELETE CHIP", 17);

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weighty = 0.15;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.gridwidth = 2;
		panel.add(info, gbc);

		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.weighty = 0.5;
		gbc.gridwidth = 2;
		gbc.anchor = GridBagConstraints.CENTER;
		panel.add(scrollPane, gbc);

		gbc.gridx = 0;
		gbc.gridy = 4;
		gbc.weighty = 0.35;
		gbc.gridwidth = 1;
		panel.add(insertChipButton, gbc);

		gbc.gridx = 1;
		gbc.gridy = 4;
		gbc.weighty = 1;
		gbc.gridwidth = 1;
		gbc.anchor = GridBagConstraints.EAST;
		panel.add(deleteChipButton, gbc);

		popup.add(panel);

		//Loads all saved chip names from database and populates text area with results
		ArrayList<String> nameList = new ArrayList<String>();
		try {
			Connection connection = DriverManager.getConnection("jdbc:sqlite:chips.db");
			String queryString = "SELECT name FROM Chips;";
			PreparedStatement preparedStatement = connection.prepareStatement(queryString);
			ResultSet rs = preparedStatement.executeQuery();
			while (rs.next()) {
				nameList.add(rs.getString(1));
				textArea.append(rs.getString(1) + "\n");
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}

		//Loads chip blob data based on text area selection and deserializes
		insertChipButton.addActionListener(e -> {
			if (nameList.contains(textArea.getSelectedText())) {
				popup.setVisible(false);
				try {
					Connection connection = DriverManager.getConnection("jdbc:sqlite:chips.db");
					String queryString = "SELECT chip FROM Chips WHERE name=?;";
					PreparedStatement preparedStatement = connection.prepareStatement(queryString);
					preparedStatement.setString(1, textArea.getSelectedText());
					ResultSet rs = preparedStatement.executeQuery();
					rs.next();
					byte[] buf = rs.getBytes(1);
					ObjectInputStream objectIn = null;
					if (buf != null) {
						objectIn = new ObjectInputStream(new ByteArrayInputStream(buf));
					}
					Object deSerializedObject = objectIn.readObject();
					Chip receivedChip = (Chip) deSerializedObject;
					infoLabel.setForeground(GREEN);
					infoLabel.setText("Click on board to place " + receivedChip.name + " chip.");

					//Click listener for placing loaded chip
					circuitArea.addMouseListener(new MouseAdapter() {
						public void mouseClicked(MouseEvent e) {
							receivedChip.positionX = e.getX();
							receivedChip.positionY = e.getY();
							initializeBlock(receivedChip);
							infoLabel.setText("");
							e.getComponent().removeMouseListener(this);
							
						}
					});
					connection.close();
				} catch (SQLException | IOException | ClassNotFoundException ex) {
					ex.printStackTrace();
				}
			}
		});

		//Deletes saved chip from database based on text area selection
		deleteChipButton.addActionListener(e -> {
			if (nameList.contains(textArea.getSelectedText())) {
				try {
					Connection connection = DriverManager.getConnection("jdbc:sqlite:chips.db");
					String queryString = "DELETE FROM Chips WHERE name=?;";
					PreparedStatement preparedStatement = connection.prepareStatement(queryString);
					preparedStatement.setString(1, textArea.getSelectedText());
					preparedStatement.execute();

					textArea.setText("");
					queryString = "SELECT name FROM Chips;";
					preparedStatement = connection.prepareStatement(queryString);
					ResultSet rs = preparedStatement.executeQuery();
					while (rs.next()) {
						nameList.add(rs.getString(1));
						textArea.append(rs.getString(1) + "\n");
					}
					connection.close();
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
		});

		return popup;
	}

	/**
	 * Creates dropdown menu to insert blocks.
	 */
	public JPopupMenu createInsertMenu(JButton insertIOButton) {

		JPopupMenu IOMenu = new JPopupMenu();
		IOMenu.setBorderPainted(false);
		UIManager.put("MenuItem.background", DARKGREY);
		UIManager.put("MenuItem.selectionBackground", YELLOW);
		UIManager.put("Menu.background", DARKGREY);
		UIManager.put("Menu.selectionBackground", YELLOW);

		JMenu insertIO = new JMenu("I/O");
		insertIO.setOpaque(true);
		insertIO.setForeground(Color.WHITE);
		JMenu insertInput = new JMenu("INPUT");
		insertInput.setOpaque(true);
		insertInput.setForeground(Color.WHITE);

		JMenuItem toggleInput = createMenuItem("TOGGLED INPUT");
		insertInput.add(toggleInput);
		toggleInput.addActionListener(new CreateIOMenuListener(0));

		JMenuItem clockInput = createMenuItem("CLOCK INPUT");
		insertInput.add(clockInput);
		clockInput.addActionListener(new CreateIOMenuListener(1));

		insertIO.add(insertInput);

		JMenuItem output = createMenuItem("OUTPUT");
		insertIO.add(output);
		output.addActionListener(new CreateIOMenuListener(2));

		JMenu basicGates = new JMenu("BASIC GATE");
		basicGates.setOpaque(true);
		basicGates.setForeground(Color.WHITE);

		JMenuItem not = createMenuItem("NOT");
		basicGates.add(not);
		not.addActionListener(new CreateChipMenuListener(0));

		JMenuItem and = createMenuItem("AND");
		basicGates.add(and);
		and.addActionListener(new CreateChipMenuListener(1));

		JMenuItem or = createMenuItem("OR");
		basicGates.add(or);
		or.addActionListener(new CreateChipMenuListener(2));

		JMenuItem nand = createMenuItem("NAND");
		basicGates.add(nand);
		nand.addActionListener(new CreateChipMenuListener(3));

		JMenuItem nor = createMenuItem("NOR");
		basicGates.add(nor);
		nor.addActionListener(new CreateChipMenuListener(4));

		JMenuItem xor = createMenuItem("XOR");
		basicGates.add(xor);
		xor.addActionListener(new CreateChipMenuListener(5));

		JMenuItem savedChip = createMenuItem("SAVED CHIP");
		savedChip.addActionListener(e -> {
			JFrame popup = createInsertChipPopup();
			popup.setVisible(true);
		});

		IOMenu.add(insertIO);
		IOMenu.add(basicGates);
		IOMenu.add(savedChip);
		IOMenu.setPopupSize(insertIOButton.getWidth(), (int) IOMenu.getPreferredSize().getHeight());
		return IOMenu;
	}

	/**
	 * Creates menu item in insert menu.
	 */
	public JMenuItem createMenuItem(String name) {

		JMenuItem menuitem = new JMenuItem(name);
		menuitem.setForeground(Color.WHITE);

		menuitem.addMouseListener(new MouseAdapter() { // Change color on rollover
			public void mouseEntered(MouseEvent e) {
				menuitem.setForeground(Color.BLACK);
			}

			public void mouseExited(MouseEvent e) {
				menuitem.setForeground(Color.WHITE);
			}
		});

		return menuitem;
	}

	/**
	 * Listener that creates I/O blocks from insert menu.
	 */
	class CreateIOMenuListener implements ActionListener {

		private final int selection;

		public CreateIOMenuListener(int selection) {
			this.selection = selection;
		}

		public void actionPerformed(ActionEvent e) {

			if (selection == 0) {
				infoLabel.setForeground(GREEN);
				infoLabel.setText("Click on board to place toggled input.");
			}

			else if (selection == 1) {
				if (InputBlock.clockLimit) {
					infoLabel.setForeground(RED);
					infoLabel.setText("Cannot place more than one clock input.");
					return;
				}
				infoLabel.setForeground(GREEN);
				infoLabel.setText("Click on board to place clock input.");
			}

			else if (selection == 2) {
				infoLabel.setForeground(GREEN);
				infoLabel.setText("Click on board to place output.");
			}

			circuitArea.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {

					if (selection == 0) {
						InputBlock toggleInput = new InputBlock(e.getX(), e.getY(), InputType.TOGGLE);
						currentChip.inputs.add(toggleInput.inputs.get(0));
						initializeBlock(toggleInput);

						circuitArea.addMouseListener(new MouseAdapter() {
							public void mouseClicked(MouseEvent e1) {
								if (toggleInput.circle.contains(e1.getPoint())) {
									toggleInput.value = !toggleInput.value;
								}
							}
						});
					} else if (selection == 1) {
						InputBlock clockInput = new InputBlock(e.getX(), e.getY(), InputType.CLOCK);
						currentChip.inputs.add(clockInput.inputs.get(0));
						initializeBlock(clockInput);
					} else if (selection == 2) {
						OutputBlock output = new OutputBlock(e.getX(), e.getY());
						currentChip.outputs.add(output.outputs.get(0));
						initializeBlock(output);
					}

					infoLabel.setText("");
					e.getComponent().removeMouseListener(this);
				}
			});
		}
	}

	/**
	 * Run for every new block.
	 * Adds listeners to an inserted block to enable click/dragging, right click deleting, and wire drawing.
	 * Starts a thread for each block, and if it is a saved block, starts threads for every internal block.
	 */
	public void initializeBlock(CircuitBlock block) {

		currentChip.blocks.add(block);
		addClickDragListener(block);
		addDeleteListener(block);
		addWireDrawListener(block);
		new Thread(block).start();

		if (block instanceof Chip && ((Chip) block).saved == true) {
			threadStarter((Chip) block);
		}
	}

	/**
	 * Recursively starts threads for internal blocks.
	 */
	public void threadStarter(Chip chip) {

		for (CircuitBlock interiorBlock : chip.blocks) {
			new Thread(interiorBlock).start();
			if (interiorBlock instanceof Chip && ((Chip) interiorBlock).saved == true)
				threadStarter((Chip) interiorBlock);
		}
		for (Wire wire : chip.wires)
			new Thread(wire).start();
	}

	/**
	 * Listener that creates basic gate blocks from insert menu.
	 */
	class CreateChipMenuListener implements ActionListener {

		private final int selection;

		public CreateChipMenuListener(int selection) {
			this.selection = selection;
		}

		public void actionPerformed(ActionEvent e) {

			if (selection == 0) {
				infoLabel.setForeground(GREEN);
				infoLabel.setText("Click on board to place NOT gate.");
			}

			if (selection == 1) {
				infoLabel.setForeground(GREEN);
				infoLabel.setText("Click on board to place AND gate.");
			}

			else if (selection == 2) {
				infoLabel.setForeground(GREEN);
				infoLabel.setText("Click on board to place OR gate.");
			}

			else if (selection == 3) {
				infoLabel.setForeground(GREEN);
				infoLabel.setText("Click on board to place NAND gate.");
			}

			else if (selection == 4) {
				infoLabel.setForeground(GREEN);
				infoLabel.setText("Click on board to place NOR gate.");
			}

			else if (selection == 5) {
				infoLabel.setForeground(GREEN);
				infoLabel.setText("Click on board to place XOR gate.");
			}

			circuitArea.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {

					if (selection == 0) {
						ArrayList<BoolExp> boolExps = new ArrayList<BoolExp>();
						BoolExp notExp = (BoolExp & Serializable) (a, b) -> !a;
						boolExps.add(notExp);
						Chip not = new Chip(e.getX(), e.getY(), 1, boolExps, "NOT");
						initializeBlock(not);
					} 
					
					else if (selection == 1) {
						ArrayList<BoolExp> boolExps = new ArrayList<BoolExp>();
						BoolExp andExp = (BoolExp & Serializable) (a, b) -> a && b;
						boolExps.add(andExp);
						Chip and = new Chip(e.getX(), e.getY(), 2, boolExps, "AND");
						initializeBlock(and);
					} 
					
					else if (selection == 2) {
						ArrayList<BoolExp> boolExps = new ArrayList<BoolExp>();
						BoolExp orExp = (BoolExp & Serializable) (a, b) -> a || b;
						boolExps.add(orExp);
						Chip or = new Chip(e.getX(), e.getY(), 2, boolExps, "OR");
						initializeBlock(or);
					}

					else if (selection == 3) {
						ArrayList<BoolExp> boolExps = new ArrayList<BoolExp>();
						BoolExp nandExp = (BoolExp & Serializable) (a, b) -> !(a && b);
						boolExps.add(nandExp);
						Chip nand = new Chip(e.getX(), e.getY(), 2, boolExps, "NAND");
						initializeBlock(nand);
					}

					else if (selection == 4) {
						ArrayList<BoolExp> boolExps = new ArrayList<BoolExp>();
						BoolExp norExp = (BoolExp & Serializable) (a, b) -> !(a || b);
						boolExps.add(norExp);
						Chip nor = new Chip(e.getX(), e.getY(), 2, boolExps, "NOR");
						initializeBlock(nor);
					}

					else if (selection == 5) {
						ArrayList<BoolExp> boolExps = new ArrayList<BoolExp>();
						BoolExp xorExp = (BoolExp & Serializable) (a, b) -> a ^ b;
						boolExps.add(xorExp);
						Chip xor = new Chip(e.getX(), e.getY(), 2, boolExps, "XOR");
						initializeBlock(xor);
					}

					infoLabel.setText("");
					e.getComponent().removeMouseListener(this);
				}
			});
		}
	}

	/**
	 * Enables blocks to be moved around the board through clicking and dragging.
	 */
	public void addClickDragListener(CircuitBlock block) {

		circuitArea.addMouseMotionListener(new MouseAdapter() {

			public void mouseDragged(MouseEvent e) {
				Point cursor = e.getPoint();
				if (block.dragging == true) {
					block.positionX = (int) cursor.getX();
					block.positionY = (int) cursor.getY();
				}
			}
		});

		circuitArea.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				Point cursor = e.getPoint();
				if (block.icon.contains(cursor)) {
					block.dragging = true;
				}
			}

			public void mouseReleased(MouseEvent e) {
				block.dragging = false;
			}
		});
	}

	/**
	 * Enables blocks to be deleted when right-clicked.
	 */
	public void addDeleteListener(CircuitBlock block) {

		circuitArea.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (block.icon.contains(e.getPoint()) && e.getButton() == MouseEvent.BUTTON3) {
					if (block instanceof InputBlock) {
						deleteInputBlock((InputBlock) block, currentChip);
					} else {
						deleteBlock(block, currentChip);
					}
					Sim.currentChip.blocks.remove(block);
				}
			}
		});
	}

	/**
	 * Creates listeners at every block output for wire drawing.
	 */
	public void addWireDrawListener(CircuitBlock block) {

		for (Output output : block.outputs) {
			circuitArea.addMouseListener(new WireConnectionListener(block, output));
		}
	}

	/**
	 * Serializes Chip object and saves to database.
	 */
	public void saveChip(String name, Chip chip) {

		chip.formatSave(name);

		PreparedStatement preparedStatement;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream out;

		try {
			Connection connection = DriverManager.getConnection("jdbc:sqlite:chips.db");
			out = new ObjectOutputStream(bos);
			out.writeObject(chip);
			out.flush();
			byte[] chipBytes = bos.toByteArray();
			String insertString = "INSERT INTO Chips VALUES (?, ?, ?)";
			preparedStatement = connection.prepareStatement(insertString);
			preparedStatement.setString(1, name);
			preparedStatement.setBytes(2, chipBytes);
			preparedStatement.setLong(3, Chip.serialVersionUID);
			preparedStatement.execute();
			connection.close();
		} catch (IOException | SQLException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Deletes a block, removing it from all appropriate lists and deleting and disconnecting any appropriate wires.
	 * Recursively deletes any internal blocks. Stops all relevant threads.
	 */
	public void deleteBlock(CircuitBlock block, Chip hostChip) {

		block.exists = false; // Stop thread
		//Remove linked wires to other blocks
		removeBlockLinks(block, hostChip);

		//If block is a saved block, also delete the internal blocks
		if (block instanceof Chip && ((Chip) block).saved) {
			for (CircuitBlock interiorBlock : ((Chip) block).blocks) {
				if (interiorBlock instanceof InputBlock)
					deleteInputBlock((InputBlock) interiorBlock, (Chip) block);
				else
					deleteBlock(interiorBlock, (Chip) block);
			}
		}

	}

	/**
	 * Variation of deleteBlock() to reset the InputBlock clock limit on deletion.
	 */
	public void deleteInputBlock(InputBlock block, Chip hostChip) {

		deleteBlock(block, hostChip);
		if (block.type == InputType.CLOCK) {
			InputBlock.clockLimit = false;
		}
	}

	/**
	 * Disconnects a block's connections to other blocks.
	 */
	public void removeBlockLinks(CircuitBlock block, Chip hostChip) {

		for (Input input : block.inputs) {
			if (input.linkedStatus) {
				Wire wire = input.linkedWire;
				deleteWireEnd(wire, hostChip);
				hostChip.wires.remove(wire);
			}
		}

		for (Output output : block.outputs) {
			Iterator<Wire> iter = output.linkedWires.iterator();
			while (iter.hasNext()) {
				Wire wire = iter.next();
				deleteWireStart(wire, hostChip);
				iter.remove();
				hostChip.wires.remove(wire);
			}

			for (Wire wire : output.linkedWires) {
				hostChip.wires.remove(wire);
			}
		}
	}

	/**
	 * Deletes a wire from its start point.
	 */
	public void deleteWireStart(Wire wire, Chip hostChip) {

		// Doesn't actually remove wire from hostChip.wires, have to do it externally
		wire.exists = false; // Stop thread
		wire.end.removeLink();
	}

	/**
	 * Deletes a wire from its end point.
	 */
	public void deleteWireEnd(Wire wire, Chip hostChip) {

		// Doesn't actually remove wire from hostChip.wires, have to do it externally
		wire.exists = false; // Stop thread
		wire.end.linkedStatus = false;
		wire.end.linkedWire = null;
		wire.start.removeLink(wire);
	}

	/**
	 * Connects two blocks and creates a wire between them.
	 */
	public static void wireLink(Input input, Output output, ArrayList<Point> bends) {

		Wire wire = new Wire(output, input, bends);
		input.link(wire);
		output.link(wire);
		Sim.currentChip.wires.add(wire);
		new Thread(wire).start();
	}

	public static void main(String[] args) {

		new Sim();
	}
}