package com.kmj.empire.client.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

import com.kmj.empire.common.Game;
import com.kmj.empire.common.ShipType;

/**
 * Dialog that is presented to the user when he or she joins
 * a game in which they do not have a ship.
 */
public class NewPlayerDialog extends JDialog implements ActionListener {
	
	private static final long serialVersionUID = 3018401234719456282L;
	
	//Setting bounds and commands that are to be used later.
	private static final int DIALOG_WIDTH = 300;
	private static final int DIALOG_HEIGHT = 200;
	private static final int PADDING = 20;
	
	private static final int COMBOBOX_WIDTH = DIALOG_WIDTH - (2 * PADDING);
	private static final int COMBOBOX_HEIGHT = 30;
	private static final int EMPIRE_SELECTION_X = PADDING;
	private static final int EMPIRE_SELECTION_Y = PADDING;
	private static final int SHIP_SELECTION_X = PADDING;
	private static final int SHIP_SELECTION_Y = EMPIRE_SELECTION_Y + COMBOBOX_HEIGHT + PADDING;
	
	private static final int OK_BUTTON_WIDTH = COMBOBOX_WIDTH / 2;
	private static final int OK_BUTTON_HEIGHT = COMBOBOX_HEIGHT;
	private static final int OK_BUTTON_X = (DIALOG_WIDTH / 2) - (OK_BUTTON_WIDTH / 2);
	private static final int OK_BUTTON_Y = SHIP_SELECTION_Y + COMBOBOX_HEIGHT + PADDING;
	
	private static final String ACTION_ALLIANCE = "alliance";
	private static final String ACTION_OK = "ok";
	
	private Game game;
	
	private JComboBox alliance;
	private JComboBox ship;
	
	private String[] arrAlliance;
	private String[] arrShips;
	
	private String allianceChoice;
	private String shipChoice;

	/**
	 * The default constructor; this should not be used when constructing
	 * this object.
	 */
	public NewPlayerDialog() {
		
	}

	/**
	 * The constructor that will be used to build the dialog
	 * that will be shown to the user.
	 * @param parent The parent window
	 * @param title The title of this dialog
	 * @param game The game the user is entering
	 */
	public NewPlayerDialog(JFrame parent, String title, Game game) {
		super(parent, title, true);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setSize(DIALOG_WIDTH, DIALOG_HEIGHT);
		setLayout(null);
		this.game = game;
		allianceChoice = "";
		shipChoice = "";
		
		// Build the initial combo boxes.
		arrAlliance = new String[game.getUniverse().getEmpireList().size()];
		for(int i = 0; i < game.getUniverse().getEmpireList().size(); i++) {
			arrAlliance[i] = game.getUniverse().getEmpireList().get(i).getName();
		}
		alliance = new JComboBox(arrAlliance);
		alliance.setBounds(EMPIRE_SELECTION_X, EMPIRE_SELECTION_Y, COMBOBOX_WIDTH, COMBOBOX_HEIGHT);
		alliance.addActionListener(this);
		alliance.setActionCommand(ACTION_ALLIANCE);
		add(alliance);

		String sel = arrAlliance[alliance.getSelectedIndex()];
		arrShips = new String[game.getUniverse().getEmpire(sel).getShipTypes().size()];
		for(int i = 0; i < game.getUniverse().getEmpire(sel).getShipTypes().size(); i++) {
			ShipType ship = game.getUniverse().getEmpire(sel).getShipTypes().get(i);
			arrShips[i] = "("+ship.getId()+") " + ship.getName();
		}
		ship = new JComboBox(arrShips);
		ship.setBounds(SHIP_SELECTION_X, SHIP_SELECTION_Y, COMBOBOX_WIDTH, COMBOBOX_HEIGHT);
		add(ship);
		
		// Add button.
		JButton button = new JButton("OK");
		button.setActionCommand(ACTION_OK);
		button.setBounds(OK_BUTTON_X, OK_BUTTON_Y, OK_BUTTON_WIDTH, button.getPreferredSize().height);
		button.addActionListener(this);
		add(button);
	}

	/**
	 * Handle the user's actions within the dialog. Get and save the user's input if they have
	 * selected OK and close the dialog box.
	 */
	public void actionPerformed(ActionEvent e) {
		String s = e.getActionCommand();

		if(s.equals(ACTION_ALLIANCE)) buildCombo();
		if(s.equals(ACTION_OK)) {
			allianceChoice = game.getUniverse().getEmpireList().get(alliance.getSelectedIndex()).getName();
			shipChoice = game.getUniverse().getEmpireList().get(alliance.getSelectedIndex()).getShipTypes().get(ship.getSelectedIndex()).getName();

			ShipType newship = game.getUniverse().getEmpireList().get(alliance.getSelectedIndex()).getShipTypes().get(ship.getSelectedIndex());
			shipChoice = "("+newship.getId()+") " + newship.getName();
			dispose();
		}
	}
	
	/**
	 * Set up the ComboBox depending on the user's selection.
	 */
	private void buildCombo() {
		String sel = arrAlliance[alliance.getSelectedIndex()];
		arrShips = new String[game.getUniverse().getEmpire(sel).getShipTypes().size()];
		for(int i = 0; i < game.getUniverse().getEmpire(sel).getShipTypes().size(); i++) {
			ShipType ship = game.getUniverse().getEmpire(sel).getShipTypes().get(i);
			arrShips[i] = "("+ship.getId()+") " + ship.getName();
		}
		ship.removeAllItems();
		for(int i = 0; i < arrShips.length; i++) ship.addItem(arrShips[i]);
	}
	
	/**
	 * Returns the empire the user has selected.
	 * @return a string representing the empire the user selected
	 */
	public String getSelectedEmpire() { return allianceChoice; }
	
	/**
	 * Returns the ship the user has selected.
	 * @return a string representing the ship the user selected
	 */
	public String getSelectedShip() { return shipChoice; }
	
}
