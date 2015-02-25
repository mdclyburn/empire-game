package com.kmj.empire.common;

import java.util.ArrayList;

import com.kmj.empire.client.controller.ActionException;
import com.kmj.empire.common.exceptions.AuthenticationFailedException;
import com.kmj.empire.common.exceptions.BadDestinationException;
import com.kmj.empire.common.exceptions.ConnectionFailedException;
import com.kmj.empire.common.exceptions.InvalidGameFileException;


public interface GameService {
	
	
	/* Restore a saved game to the server */
	public int restoreGame(String gameData) throws InvalidGameFileException, ConnectionFailedException;
	
	/* Returns the details/state of the game being played by player identified by sessionId */
	public GameState getGameState(int sessionId) throws ConnectionFailedException;
	
	/* Returns a list of ongoing games */
	public ArrayList<Game> getGamesList(int sessionId) throws AuthenticationFailedException, ConnectionFailedException;
	
	/* Authenticate username and password, returns playerID */
	public int authenticate(String user, String password) throws AuthenticationFailedException, ConnectionFailedException;
	
	/* Creates a game on this server */
	public int createGame() throws ConnectionFailedException;
	
	/* Join an existing game on this server */
	public void joinGame(int sessionId, int id) throws ConnectionFailedException;
	
	/* Notifies the server of a disconnecting user. */
	public void disconnect(int sessionId) throws ConnectionFailedException;
	
	/* Move a ship via the impulse mechanism. */
	public void navigate(int sessionId, int x, int y) throws BadDestinationException, ConnectionFailedException;
	
	/* Set the alert level. */
	public void setAlertLevel(int sessionId, AlertLevel level) throws ConnectionFailedException;
	
	/* Move a ship via the warp mechanism. */
	public void warp(int sessionId, Sector sector) throws BadDestinationException, ConnectionFailedException;
	
	/* Fire a torpedo at target. */
	public void fireTorpedo(int sessionId, Sector sector, int x, int y) throws ActionException, ConnectionFailedException;

}
