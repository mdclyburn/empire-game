package com.kmj.empire.client.controller;

import com.kmj.empire.common.Game;

/*
 * Any class implementing this interface can be
 * notified whenever the session ID or the state of the
 * Game changes.
 */
public interface SessionObserver {
	
	// The action to be taken when the session ID has been
	// set or changed.
	public void onIdChanged(int newId);
	
	// The action to be taken when the Game object has been
	// changed.
	public void onGameChanged(Game newGame);

}
