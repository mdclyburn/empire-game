package com.kmj.empire.client.controller;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import com.kmj.empire.common.AlertLevel;
import com.kmj.empire.common.Game;
import com.kmj.empire.common.GameService;
import com.kmj.empire.common.Sector;
import com.kmj.empire.common.exceptions.ActionException;
import com.kmj.empire.common.exceptions.AuthenticationFailedException;
import com.kmj.empire.common.exceptions.BadDestinationException;
import com.kmj.empire.common.exceptions.ConnectionFailedException;
import com.kmj.empire.common.exceptions.InvalidGameFileException;

/*
 * Singleton object that will hold often-used objects
 * and update observers when they change. The main purpose
 * of Session is to provide easy access to communication
 * with a GameService. This can be flexibly set to different
 * providers to change the behavior of the client.
 */

public class Session {

	// The ID for the current session.
	private int id;

	// The state of the game the user is currently playing.
	private Game game;
	private ArrayList<Game> gamesList;

	// The server connection proxy. This is used to communicate
	// with the server.
	private GameService provider;

	// Observers of changes to the state of this object.
	private ArrayList<SessionObserver> observers;
	
	private static Session session;
	static { session = new Session(); }

	private Session() {
		observers = new ArrayList<SessionObserver>();
		gamesList = new ArrayList<Game>();
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
	
	public ArrayList<Game> getLocalGamesList() {
		return gamesList;
	}

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
	
	// Provide users an easy way to check for 'Game Over'.
	public boolean isGameOver() {
		return (game.getPlayerShip(Configuration.getInstance().getUsername()) == null);
	}
	
	// Functions that call down to the GameService, providing
	// all the necessary boilerplate.
	
	public void disconnect() throws ConnectionFailedException {
		provider.disconnect(id);
	}
	
	public ArrayList<Game> getGamesList() throws AuthenticationFailedException, ConnectionFailedException {
		gamesList = provider.getGamesList(id);
		return gamesList;
	}
	
	public void refresh() throws ConnectionFailedException {
		setGame(provider.getGameState(id).toGame(null));
	}
	
	public int restoreGame(String data) throws InvalidGameFileException, ConnectionFailedException {
		return provider.restoreGame(data);
	}
	
	public void joinGame(int gameId) throws ConnectionFailedException {
		provider.joinGame(id, gameId);
	}
	
	public void setAlertLevel(AlertLevel level) throws ConnectionFailedException, ActionException {
		refresh();
		if (!isGameOver())
		provider.setAlertLevel(id, level);
		refresh();
	}
	
	public void warp(Sector sector) throws BadDestinationException, ConnectionFailedException, ActionException {
		refresh();
		if (!isGameOver())
		provider.warp(id, sector);
		refresh();
	}
	
	public void navigate(int x, int y) throws BadDestinationException, ConnectionFailedException, ActionException {
		refresh();
		if (!isGameOver())
		provider.navigate(id, x, y);
		refresh();
	}
	
	public void fireTorpedo(Sector sector, int x, int y) throws ActionException, ConnectionFailedException {
		refresh();
		if (!isGameOver())
		provider.fireTorpedo(id, sector, x, y);
		refresh();
	}

	public void connect() throws ConnectionFailedException {
		try {
			Socket socket = new Socket(Configuration.getInstance().getServerAddress(), Configuration.getInstance().getServerPort());
			Session.getInstance().setProvider(new GameServiceProxy(socket));
		} catch (UnknownHostException e) {
			throw new ConnectionFailedException("Unknown host.");
		} catch (IOException e) {
			throw new ConnectionFailedException("Failed to connect to server.");
		}
		
	}

}
