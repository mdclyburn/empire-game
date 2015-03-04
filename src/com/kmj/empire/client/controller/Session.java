package com.kmj.empire.client.controller;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import com.kmj.empire.client.ui.ServerListWindow;
import com.kmj.empire.common.AlertLevel;
import com.kmj.empire.common.Game;
import com.kmj.empire.common.GameService;
import com.kmj.empire.common.Sector;
import com.kmj.empire.common.exceptions.ActionException;
import com.kmj.empire.common.exceptions.AuthenticationFailedException;
import com.kmj.empire.common.exceptions.BadDestinationException;
import com.kmj.empire.common.exceptions.ConnectionFailedException;
import com.kmj.empire.common.exceptions.InvalidGameFileException;

/**
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
	
	// Server List Window
	private ServerListWindow serverListWindow;
	
	private static Session session;
	static { session = new Session(); }

	/**
	 * Default constructor for the Session object.
	 */
	private Session() {
		observers = new ArrayList<SessionObserver>();
		gamesList = new ArrayList<Game>();
	}

	/**
	 * Constructor for the Session object.
	 * @param id The ID of the user's session
	 * @param game The Game the user is currently playing in
	 * @param provider The GameService to use
	 */
	private Session(int id, Game game, GameService provider) {
		this.id = id;
		this.game = game;
		this.provider = provider;
	}

	/**
	 * Returns the Session object.
	 * @return the Session object.
	 */
	public static Session getInstance() { return session; }
	
	/**
	 * Returns the ID of the user's session.
	 * @return the ID of the user's session
	 */
	public int getId() { return id; }
	
	/**
	 * Returns the Game the user is currently in
	 * @return the Game the user is currently in
	 */
	public Game getGame() { return game; }
	
	/**
	 * Returns the GameService in use
	 * @return the GameService in use
	 */
	public GameService getProvider() { return provider; }
	
	/**
	 * Returns the ServerListWindow
	 * @return the ServerListWindow
	 */
	public ServerListWindow getServerListWindow() { return serverListWindow; }
	
	/**
	 * Returns a cached list of active games. This list may or may not be current.
	 * @return a list of active games
	 */
	public ArrayList<Game> getLocalGamesList() {
		return gamesList;
	}

	/**
	 * Sets the ID of the user's session. All objects that have been subscribed to changes
	 * in the Session are subsequently notified of the change in ID.
	 * @param id The session ID
	 */
	public void setId(int id) {
		this.id = id;

		// Notify observers.
		for(SessionObserver o : observers)
			o.onIdChanged(id);
	}

	/**
	 * Sets the Game of the user's session. All objects that have been subscribed to changes
	 * in the Session are subsequently notified of the change in ID.
	 * @param game The game
	 */
	public void setGame(Game game) {
		this.game = game;

		// Notify observers.
		for(SessionObserver o : observers)
			o.onGameChanged(game);
	}

	/**
	 * Sets the GameService to use when communicating actions to the server.
	 * @param provider The GameService to use
	 */
	public void setProvider(GameService provider) {
		this.provider = provider;
	}
	
	/**
	 * Sets the ServerListWindow
	 * @param w the ServerListWindow
	 */
	public void setServerListWindow(ServerListWindow w) {
		serverListWindow = w;
	}
	
	/**
	 * Subscribe an observer to changes in the Game and the session ID.
	 * @param o the observer to be added
	 */
	public void addObserver(SessionObserver o) {
		observers.add(o);
	}
	
	/**
	 * Unsubscribe an observer.
	 * @param o the observer to be removed
	 */
	public void removeObserver(SessionObserver o) {
		observers.remove(o);
	}
	
	/**
	 * A convenience function to check if game over conditions have been reached.
	 * @return true if the user has lost; false otherwise
	 */
	public boolean isGameOver() {
		return (game.getPlayerShip(Configuration.getInstance().getUsername()) == null);
	}
	
	// Functions that call down to the GameService, providing
	// all the necessary boilerplate.
	
	/**
	 * Tells the provider to disconnect from the server.
	 * @throws ConnectionFailedException
	 */
	public void disconnect() throws ConnectionFailedException {
		provider.disconnect(id);
	}
	
	/**
	 * Tells the provider to retrieve a list of active games on the server.
	 * @return a list of active games
	 * @throws AuthenticationFailedException
	 * @throws ConnectionFailedException
	 */
	public ArrayList<Game> getGamesList() throws AuthenticationFailedException, ConnectionFailedException {
		gamesList = provider.getGamesList(id);
		return gamesList;
	}
	
	/**
	 * Tells the provider to retrieve a new game state.
	 * @throws ConnectionFailedException
	 */
	public void refresh() throws ConnectionFailedException {
		setGame(provider.getGameState(id).toGame(null));
	}
	
	/**
	 * Tells the provider to request that the server restore a game from the provided String.
	 * @param data String representation of the game to be restored
	 * @return the ID of the restored game
	 * @throws InvalidGameFileException
	 * @throws ConnectionFailedException
	 */
	public int restoreGame(String data) throws InvalidGameFileException, ConnectionFailedException {
		return provider.restoreGame(data);
	}
	
	/**
	 * Tells the provider to connect the client to a specified game.
	 * @param gameId the ID of the game to be joined
	 * @throws ConnectionFailedException
	 */
	public void joinGame(int gameId) throws ConnectionFailedException {
		provider.joinGame(id, gameId);
	}
	
	/**
	 * Tells the provider to request that the server set the alert level of the user's ship
	 * to the specified option.
	 * @param level the alert level to be set
	 * @throws ConnectionFailedException
	 * @throws ActionException
	 */
	public void setAlertLevel(AlertLevel level) throws ConnectionFailedException, ActionException {
		refresh();
		if (!isGameOver()) {
			provider.setAlertLevel(id, level);
			refresh();
		}
	}
	
	/**
	 * Tells the provider to request that the player's ship be warped to another sector.
	 * @param sector The sector to be warped to
	 * @throws BadDestinationException
	 * @throws ConnectionFailedException
	 * @throws ActionException
	 */
	public void warp(Sector sector) throws BadDestinationException, ConnectionFailedException, ActionException {
		refresh();
		if (!isGameOver()) {
			provider.warp(id, sector);
			refresh();
		}
	}
	
	/**
	 * Tells the provider to request that the server to navigate the user's ship to another location within its
	 * current sector.
	 * @param x The X-coordinate of the position to navigate to
	 * @param y The Y-coordinate of the position to navigate to
	 * @throws BadDestinationException
	 * @throws ConnectionFailedException
	 * @throws ActionException
	 */
	public void navigate(int x, int y) throws BadDestinationException, ConnectionFailedException, ActionException {
		refresh();
		if (!isGameOver()) {
			provider.navigate(id, x, y);
			refresh();
		}
	}
	
	/**
	 * Tells the provider to request that the server to fire a torpedo
	 * @param sector The sector the player is currently in
	 * @param x The X-coordinate to fire a torpedo at
	 * @param y The Y-coordinate to fire a torpedo at
	 * @throws ActionException
	 * @throws ConnectionFailedException
	 */
	public void fireTorpedo(Sector sector, int x, int y) throws ActionException, ConnectionFailedException {
		refresh();
		if (!isGameOver()) {
			provider.fireTorpedo(id, sector, x, y);
			refresh();
		}
	}
	
	/**
	 * Initialize a socket and connect to the server specified in the Configuration object.
	 * @throws ConnectionFailedException
	 */
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
