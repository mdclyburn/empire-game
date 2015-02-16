package com.kmj.empire.client;

import java.util.ArrayList;

import com.kmj.empire.common.AuthenticationFailedException;
import com.kmj.empire.common.ConnectionFailedException;
import com.kmj.empire.common.Game;
import com.kmj.empire.common.GameService;
import com.kmj.empire.common.UniverseType;

// A dummy connection service to get the client-side prototype
// to proceed to show off functionality. All instances of its use
// should be later replaced with a real, more robust implementation
// with actual connection to the server soon.

public class DummyServerConnectionProxy implements GameService {
	
	ArrayList<Game> gameList;

	public DummyServerConnectionProxy() {
		gameList = new ArrayList<Game>();
		gameList.add(new Game("Trekkie's Delight", new UniverseType()));
		gameList.add(new Game("World War III", new UniverseType()));
		gameList.add(new Game("The Battle of Gettysburg", new UniverseType()));
	}

	// This function will always proceed to feed the client
	// a valid ID that will allow it to interact with a non-existent
	// server.
	@Override
	public int authenticate(String username, String password) throws AuthenticationFailedException, ConnectionFailedException {
		if(!password.equals("p")) throw new AuthenticationFailedException("The username and password combination you supplied is incorrect.");
		return 0;
	}

	@Override
	public int restoreGame(Game game) throws ConnectionFailedException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Game getGameState(int gameId) throws ConnectionFailedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override

	// Hand over the list of active Games. The list is prefabricated for
	// the purposes of this class and do not represent actual, live
	// Games.
	public ArrayList<Game> getGamesList(int sessionId) throws AuthenticationFailedException, ConnectionFailedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int createGame() throws ConnectionFailedException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void joinGame() throws ConnectionFailedException {
		// TODO Auto-generated method stub
		
	}

}
