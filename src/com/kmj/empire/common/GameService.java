package com.kmj.empire.common;

import java.util.ArrayList;

public interface GameService {
	
	
	/* Restore a saved game to the server */
	public int restoreGame(String gameData) throws ConnectionFailedException;
	
	/* Returns the details/state of the game being played by player identified by sessionId */
	public Game getGameState(int sessionId) throws ConnectionFailedException;
	
	/* Returns a list of ongoing games */
	public ArrayList<Game> getGamesList(int sessionId) throws AuthenticationFailedException, ConnectionFailedException;
	
	/* Authenticate username and password, returns playerID */
	public int authenticate(String user, String password) throws AuthenticationFailedException, ConnectionFailedException;
	
	/* Creates a game on this server */
	public int createGame() throws ConnectionFailedException;
	
	/* Join an existing game on this server */
	public void joinGame(int sessionId, String name) throws ConnectionFailedException;

}
