package com.kmj.empire.client.ui.model;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import com.kmj.empire.client.controller.Session;
import com.kmj.empire.client.controller.SessionObserver;
import com.kmj.empire.common.Game;

public class GameLogTableModel extends AbstractTableModel implements SessionObserver{

	private static final long serialVersionUID = 4610467740584913440L;

	protected static final int COLUMN_ENTRY = 0;

	protected String[] header;

	public GameLogTableModel() {
		header = new String[1];
		header[COLUMN_ENTRY] = "Universe Log";
	}

	@Override
	public int getRowCount() {
		ArrayList<String> log = Session.getInstance().getGame().getLog();
		return (log != null ? log.size() : 0);
	}

	public int getColumnCount() {
		// COLUMN	DESCRIPTION
		// ====================
		// 0		The log entry.
		return header.length;
	}

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
	
	@Override
	public void onIdChanged(int newId) {
		
	}
	
	@Override
	public void onGameChanged(Game newGame) {
		fireTableDataChanged();
	}

}
