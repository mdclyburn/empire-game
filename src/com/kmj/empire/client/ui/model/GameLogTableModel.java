package com.kmj.empire.client.ui.model;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import com.kmj.empire.client.controller.Session;
import com.kmj.empire.client.controller.SessionObserver;
import com.kmj.empire.common.Game;

/**
 * Managing class for the in-game log a player sees. Provides
 * backend support for the game log in the GameWindow class.
 */
public class GameLogTableModel extends AbstractTableModel implements SessionObserver{

	private static final long serialVersionUID = 4610467740584913440L;

	protected static final int COLUMN_ENTRY = 0;

	protected String[] header;

	/**
	 * Default constructor.
	 */
	public GameLogTableModel() {
		header = new String[1];
		header[COLUMN_ENTRY] = "Universe Log";
	}

	/**
	 * Return the number of rows in the table.
	 */
	@Override
	public int getRowCount() {
		ArrayList<String> log = Session.getInstance().getGame().getLog();
		return (log != null ? log.size() : 0);
	}

	/**
	 * Returns the number of columns in the table.
	 */
	public int getColumnCount() {
		// COLUMN	DESCRIPTION
		// ====================
		// 0		The log entry.
		return header.length;
	}

	/**
	 * Returns the name of the table.
	 */
	@Override
	public String getColumnName(int c) { return header[c]; }

	@Override
	public Object getValueAt(int row, int column) {
		switch(column) {
		case COLUMN_ENTRY: return Session.getInstance().getGame().getLog().get(row);
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
