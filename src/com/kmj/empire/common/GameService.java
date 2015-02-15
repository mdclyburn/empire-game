package com.kmj.empire.server;

import java.util.List;

public interface GameService {
	
	
	/* Restore a saved game to the server */
	public int restoreGame(Game game) throws ConnectionFailedException;
	
	/* Returns the details/state of the game identified by gameId */
	public Game getGameState(int gameId) throws ConnectionFailedException;
	
	/* Returns a list of ongoing games */
	public ArrayList<Game> getGamesList(int sessionId) throws AuthenticationFailedException, ConnectionFailedException;
	
	/* Authenticate username and password, returns playerID */
	public int authenticate(String user, String password) throws ConnectionFailedException;
	
	/* Creates a game on this server */
	public int createGame() throws ConnectionFailedException;
	
	/* Join an existing game on this server */
	public void joinGame() throws ConnectionFailedException;

	
	
}
