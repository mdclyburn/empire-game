package com.kmj.empire.client;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

public class GameLogTableModel extends AbstractTableModel {

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

}
