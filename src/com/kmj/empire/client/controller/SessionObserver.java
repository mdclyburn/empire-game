package com.kmj.empire.client;

import com.kmj.empire.common.Game;

public interface SessionObserver {
	
	// The action to be taken when the session ID has been
	// set or changed.
	public void onIdChanged(int newId);
	
	// The action to be taken when the Game object has been
	// changed.
	public void onGameChanged(Game newGame);

}
