package com.kmj.empire.common;

import java.util.ArrayList;

public interface GameService {

	// Verify authentication with the game server.
	// Returns session ID on success; throws exception on failure.
	public int authenticate(String username, String password) throws AuthenticationFailedException, ConnectionFailedException;

	// Get the list of active games from the server.
	// Returns an ArrayList on success; throws exception on failure.
	public ArrayList<Game> getGameList() throws ConnectionFailedException;
}
