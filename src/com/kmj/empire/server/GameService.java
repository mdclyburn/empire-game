package com.kmj.empire.server;

import java.util.List;

public interface GameService {
	
	
	/* Restore a saved game to the server */
	public int restoreGame(Game game);
	
	/* Returns the details/state of the game identified by gameId */
	public Game getGameState(int gameId);
	
	/* Returns a list of ongoing games */
	public List<Game> getGamesList();
	
	/* Authenticate username and password, returns playerID */
	public int authenticate(String user, String password);
	
	/* Creates a game on this server */
	public int createGame();
	
	/* Join an existing game on this server */
	public void joinGame();

	
	
}
