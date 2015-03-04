package com.kmj.empire.client.ui.model;

import javax.swing.table.AbstractTableModel;

import com.kmj.empire.client.controller.Configuration;
import com.kmj.empire.client.controller.Session;
import com.kmj.empire.client.controller.SessionObserver;
import com.kmj.empire.common.Game;
import com.kmj.empire.common.Ship;

/**
 * Model class showing the attributes for a selected ship.
 */
public class ShipAttributeTableModel extends AbstractTableModel implements SessionObserver {
	
	private static final long serialVersionUID = 5037957514446943337L;
	
	protected int shipId;
	protected String[] header;

	/**
	 * Default constructor.
	 */
	public ShipAttributeTableModel() {
		header = new String[1];
		header[0] = "Ship Attributes";
	}
	
	/**
	 * Sets the ship to pull information from.
	 * @param shipId ID of the ship
	 */
	public void setTableSource(int shipId) {
		this.shipId = shipId;
		fireTableDataChanged();
	}
	
	/**
	 * Returns the number of rows in the table
	 * @return number of rows
	 */
	@Override
	public int getRowCount() {
		// The number of attributes to display:
		// Ship Class,
		// Energy Level,
		// Alert Level,
		// Shield Level
		return 4;
	}

	/**
	 * Returns the number of columns in the table.
	 * @return number of columns
	 */
	@Override
	public int getColumnCount() {
		return 1;
	}
	
	/**
	 * Returns the name of the table.
	 * @return name of the table
	 */
	@Override
	public String getColumnName(int c) {
		return header[c];
	}

	@Override
	public Object getValueAt(int row, int column) {
		Ship ship = Session.getInstance().getGame().getIdShip(shipId);
		if(ship == null) return "";
		if(row == 0) return "Class: " + ship.getType().getId();
		else if(row == 1) return "Energy: " + ship.getEnergy();
		else if(row == 2) return "Alert: " + ship.getAlertLevel();
		else return "Shield: " + (ship.getShieldLevel() > 0 ? ship.getShieldLevel() : "-1");
	}
	
	/**
	 * Please see the SessionObserver interface.
	 */
	@Override
	public void onIdChanged(int newId) {
		
	}
	
	/**
	 * Please see the SessionObserver interface.
	 */
	@Override
	public void onGameChanged(Game newGame) {
		fireTableDataChanged();
	}

}
