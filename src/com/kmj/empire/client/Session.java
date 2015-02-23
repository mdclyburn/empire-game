package com.kmj.empire.client;

import java.util.ArrayList;

import com.kmj.empire.common.AlertLevel;
import com.kmj.empire.common.AuthenticationFailedException;
import com.kmj.empire.common.BadDestinationException;
import com.kmj.empire.common.ConnectionFailedException;
import com.kmj.empire.common.Game;
import com.kmj.empire.common.GameService;
import com.kmj.empire.common.InvalidGameFileException;
import com.kmj.empire.common.Sector;

/*
 * Singleton object that will hold often-used objects
 * and update observers when they change.
 */

public class Session {

	// The ID for the current session.
	private int id;

	// The state of the game the user is currently playing.
	private Game game;

	// The server connection proxy. This is used to communicate
	// with the server.
	private GameService provider;

	// Observers of changes to the state of this object.
	private ArrayList<SessionObserver> observers;
	
	private static Session session;
	static { session = new Session(); }

	private Session() {
		observers = new ArrayList<SessionObserver>();
	}

	private Session(int id, Game game, GameService provider) {
		this.id = id;
		this.game = game;
		this.provider = provider;
	}

	// Getters
	public static Session getInstance() { return session; }
	
	public int getId() { return id; }
	public Game getGame() { return game; }
	public GameService getProvider() { return provider; }

	// Setters. Some are augmented for observers.
	public void setId(int id) {
		this.id = id;

		// Notify observers.
		for(SessionObserver o : observers)
			o.onIdChanged(id);
	}

	public void setGame(Game game) {
		this.game = game;

		// Notify observers.
		for(SessionObserver o : observers)
			o.onGameChanged(game);
	}

	public void setProvider(GameService provider) {
		this.provider = provider;
	}
	
	public void addObserver(SessionObserver o) {
		observers.add(o);
	}
	
	public void removeObserver(SessionObserver o) {
		observers.remove(o);
	}
	
	// Functions that call down to the GameService, providing
	// all the necessary boilerplate.
	
	public void disconnect() throws ConnectionFailedException {
		provider.disconnect(id);
	}
	
	public ArrayList<Game> getGamesList() throws AuthenticationFailedException, ConnectionFailedException {
		return provider.getGamesList(id);
	}
	
	public void refresh() throws ConnectionFailedException {
		setGame(provider.getGameState(id));
	}
	
	public int restoreGame(String data) throws InvalidGameFileException, ConnectionFailedException {
		return provider.restoreGame(data);
	}
	
	public void joinGame(int gameId) throws ConnectionFailedException {
		provider.joinGame(id, gameId);
	}
	
	public void setAlertLevel(AlertLevel level) throws ConnectionFailedException {
		provider.setAlertLevel(id, level);
		refresh();
	}
	
	public void warp(Sector sector) throws BadDestinationException, ConnectionFailedException {
		provider.warp(id, sector);
		refresh();
	}
	
	public void navigate(int x, int y) throws BadDestinationException, ConnectionFailedException {
		provider.navigate(id, x, y);
		refresh();
	}
	
	public void fireTorpedo(Sector sector, int x, int y) throws ActionException, ConnectionFailedException {
		provider.fireTorpedo(id, sector, x, y);
		refresh();
	}

}
