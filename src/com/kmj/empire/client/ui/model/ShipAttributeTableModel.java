package com.kmj.empire.client.ui.model;

import javax.swing.table.AbstractTableModel;

import com.kmj.empire.client.controller.SessionObserver;
import com.kmj.empire.common.AlertLevel;
import com.kmj.empire.common.Game;
import com.kmj.empire.common.Ship;

public class ShipAttributeTableModel extends AbstractTableModel implements SessionObserver {
	
	protected Ship ship;
	protected String[] header;

	public ShipAttributeTableModel() {
		header = new String[1];
		header[0] = "Ship Attributes";
	}
	
	public void setTableSource(Ship ship) {
		this.ship = ship;
		fireTableDataChanged();
	}
	
	@Override
	public int getRowCount() {
		// The number of attributes to display:
		// Ship Class,
		// Energy Level,
		// Alert Level,
		// Shield Level
		return 4;
	}

	@Override
	public int getColumnCount() {
		return 1;
	}
	
	@Override
	public String getColumnName(int c) {
		return header[c];
	}

	@Override
	public Object getValueAt(int row, int column) {
		if(ship == null) return "";
		if(row == 0) return "Class: " + ship.getType().getName();
		else if(row == 1) return "Energy: " + ship.getEnergy();
		else if(row == 2) return "Alert: " + ship.getAlertLevel();
		else return "Shield: " + (ship.getShieldLevel() > 0 ? ship.getShieldLevel() : "-1");
	}
	
	@Override
	public void onIdChanged(int newId) {
		
	}
	
	@Override
	public void onGameChanged(Game newGame) {
		fireTableDataChanged();
	}

}
