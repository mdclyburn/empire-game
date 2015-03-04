package com.kmj.empire.client.ui.model;

import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;

import com.kmj.empire.common.Game;

/**
 * Managing class for the list of games a player can join. Provides
 * backend support for the game list in the ServerListWindow class.
 */
public class GameListTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 8136003534138739190L;
	
	public static final int COLUMN_NAME = 0;
	public static final int COLUMN_PLAYERS = 1;
	public static final int COLUMN_ID = 2;

	ArrayList<Game> games;
	String[] header;

	/**
	 * Default constructor for GameListTableModel.
	 */
	public GameListTableModel() {
		header = new String[2];
		header[0] = "Name";
		header[1] = "Players";
	}

	/**
	 * Set the list of games from which the model will derive data from.
	 * @param games the list of games
	 */
	public void setTableSource(ArrayList<Game> games) { this.games = games; }

	/**
	 * Returns a list of rows in the table.
	 */
	@Override
	public int getRowCount() {
		return (games != null ? games.size() : 0);
	}

	/**
	 * Returns the number of columns in the table.
	 */
	@Override
	public int getColumnCount() {
		// COLUMN	DESCRIPTION
		// ====================
		// 0		The given name for an active game.
		// 1		The number of players in an active game.
		return 2;
	}

	/**
	 * Returns the name of the table.
	 */
	@Override
	public String getColumnName(int c) { return header[c]; }

	/**
	 * Returns the value at the specified row, column.
	 * @param row the row
	 * @param column the column
	 */
	@Override
	public Object getValueAt(int row, int column) {
		if(row == -1) {
			JOptionPane.showMessageDialog(null, "No game selected.", "Error", JOptionPane.ERROR_MESSAGE);
			return null;
		}
		// Returns the requested values.
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
