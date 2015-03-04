package com.kmj.empire.client.ui.model;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import com.kmj.empire.client.controller.Session;
import com.kmj.empire.client.controller.SessionObserver;
import com.kmj.empire.common.Game;
import com.kmj.empire.common.Player;

/*
 * The model class for the table containing a list of
 * active players.
 */
public class PlayerListTableModel extends AbstractTableModel implements SessionObserver {
	
	private static final long serialVersionUID = -2458706180159928665L;

	public static final int COLUMN_NAME = 0;
	
	protected String[] header;
	
	/**
	 * Default constructor.
	 */
	public PlayerListTableModel() {
		header = new String[1];
		header[COLUMN_NAME] = "Player List";
	}
	
	/**
	 * Returns the number of rows in the table.
	 * @return number of rows
	 */
	@Override
	public int getRowCount() {
		ArrayList<Player> players = Session.getInstance().getGame().getActivePlayers();
		return (players != null ? players.size() : 0);
	}
	
	/**
	 * Returns the number of columns in the table.
	 * @return number of columns
	 */
	@Override
	public int getColumnCount() {
		// COLUMN	DESCRIPTION
		// ====================
		// 0		The name of the user.
		return header.length;
	}
	
	/**
	 * Returns the name of the table.
	 * @return name of the table
	 */
	@Override
	public String getColumnName(int c) { return header[c]; }
	
	@Override
	public Object getValueAt(int row, int column) {
		switch(column) {
		case COLUMN_NAME: return Session.getInstance().getGame().getActivePlayers().get(row).getUsername();
		default:
			System.out.println("Invalid row-column query: " + row + "-" + column + ".");
			return null;
		}
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
