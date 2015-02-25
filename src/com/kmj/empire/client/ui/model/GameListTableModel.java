package com.kmj.empire.client.ui.model;

import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;

import com.kmj.empire.client.controller.Session;
import com.kmj.empire.client.controller.SessionObserver;
import com.kmj.empire.common.Game;

public class GameListTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 8136003534138739190L;
	
	public static final int COLUMN_NAME = 0;
	public static final int COLUMN_PLAYERS = 1;
	public static final int COLUMN_ID = 2;

	ArrayList<Game> games;
	String[] header;

	public GameListTableModel() {
		header = new String[2];
		header[0] = "Name";
		header[1] = "Players";
	}

	public void setTableSource(ArrayList<Game> games) { this.games = games; }

	@Override
	public int getRowCount() {
		return (games != null ? games.size() : 0);
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
		if(row == -1) {
			JOptionPane.showMessageDialog(null, "No game selected.", "Error", JOptionPane.ERROR_MESSAGE);
			return null;
		}
		switch(column) {
		case COLUMN_NAME: System.out.println(games); return games.get(row).getName();
		case COLUMN_PLAYERS: return games.get(row).getActivePlayers().size();
		case COLUMN_ID: return games.get(row).getId();
		default:
			System.out.println("Invalid row-column query: " + row + "-" + column + ".");
			return null;
		}
	}
}
