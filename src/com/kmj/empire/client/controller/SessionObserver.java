package com.kmj.empire.client.controller;

import com.kmj.empire.common.Game;

/**
 * Any class implementing this interface can be
 * notified whenever the session ID or the state of the
 * Game changes.
 */
public interface SessionObserver {
	
	/**
	 * The method that is called when the session ID has changed.
	 * @param newId The new ID
	 */
	public void onIdChanged(int newId);
	
	/**
	 * The method that is called when the Game has been updated.
	 * @param newGame The new Game
	 */
	public void onGameChanged(Game newGame);

}
