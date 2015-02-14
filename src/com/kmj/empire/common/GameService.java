package com.kmj.empire.common;

public interface GameService {

	// Verify authentication with the game server.
	// Returns session ID on success; throws exception on failure.
	public int authenticate(String username, String password) throws AuthenticationFailedException, ConnectionFailedException;

}
