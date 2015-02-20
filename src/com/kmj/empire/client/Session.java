package com.kmj.empire.client;

import java.util.ArrayList;

import com.kmj.empire.common.Game;
import com.kmj.empire.common.GameService;

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

}
