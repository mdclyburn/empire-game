package com.kmj.empire.client;

import com.kmj.empire.common.AuthenticationFailedException;
import com.kmj.empire.common.ConnectionFailedException;
import com.kmj.empire.common.GameService;

// A dummy connection service to get the client-side prototype
// to proceed to show off functionality. All instances of its use
// should be later replaced with a real, more robust implementation
// with actual connection to the server soon.

public class DummyServerConnectionProxy implements GameService {

	// This function will always proceed to feed the client
	// a valid ID that will allow it to interact with a non-existent
	// server.
	@Override
	public int authenticate(String username, String password) throws AuthenticationFailedException, ConnectionFailedException {
		return 0;
	}

}
