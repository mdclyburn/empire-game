package com.kmj.empire.client;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import com.kmj.empire.common.Game;

public class GameListTableModel extends AbstractTableModel {
	
	public static final int COLUMN_NAME = 0;
	public static final int COLUMN_PLAYERS = 1;
	
	ArrayList<Game> games;
	String[] header;
	
	GameListTableModel() {
		header = new String[2];
		header[0] = "Name";
		header[1] = "Players";
	}
	
	void setTableSource(ArrayList<Game> games) { this.games = games; }

	@Override
	public int getRowCount() {
		return games.size();
	}

	@Override
	public int getColumnCount() {
		// COLUMN	DESCRIPTION
		// ====================
		// 0		The given name for an active game.
		// 1		The number of players in an active game.
		return 2;
	}
	
	@Override
	public String getColumnName(int c) { return header[c]; }

	@Override
	public Object getValueAt(int row, int column) {
		switch(column) {
		case COLUMN_NAME: return games.get(row).getName();
		case COLUMN_PLAYERS: return games.get(row).getActivePlayers();
		default:
			System.out.println("Invalid row-column query: " + row + "-" + column + ".");
			return null;
		}
	}

}
