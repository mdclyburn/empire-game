package com.kmj.empire.client;

import java.util.ArrayList;
import java.util.HashMap;

import com.kmj.empire.common.AuthenticationFailedException;
import com.kmj.empire.common.ConnectionFailedException;
import com.kmj.empire.common.Game;
import com.kmj.empire.common.GameService;
import com.kmj.empire.common.Player;
import com.kmj.empire.common.UniverseType;

// A dummy connection service to get the client-side prototype
// to proceed to show off functionality. All instances of its use
// should be later replaced with a real, more robust implementation
// with actual connection to the server soon.

public class DummyServerConnectionProxy implements GameService {
	
	ArrayList<Game> gameList;
	HashMap<Integer, String> users;
	HashMap<Integer, Game> sessions;

	public DummyServerConnectionProxy() {
		gameList = new ArrayList<Game>();
		gameList.add(new Game("Trekkie's Delight", new UniverseType()));
		gameList.add(new Game("World War III", new UniverseType()));
		gameList.add(new Game("The Battle of Gettysburg", new UniverseType()));
		
		users = new HashMap<Integer, String>();
		sessions = new HashMap<Integer, Game>();
	}

	// This function will always proceed to feed the client
	// a valid ID that will allow it to interact with a non-existent
	// server.
	@Override
	public int authenticate(String username, String password) throws AuthenticationFailedException, ConnectionFailedException {
		if(!password.equals("p")) throw new AuthenticationFailedException("The username and password combination you supplied is incorrect.");
		int id = users.size();
		users.put(id, username);
		System.out.println(username + " logging in. Assigning ID " + id);
		return id;
	}

	@Override
	public int restoreGame(Game game) throws ConnectionFailedException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Game getGameState(int sessionId) throws ConnectionFailedException {
		System.out.println("Getting game state for session " + sessionId);
		return sessions.get(sessionId);
	}

	// Hand over the list of active Games. The list is prefabricated for
	// the purposes of this class and do not represent actual, live
	// Games.
	@Override
	public ArrayList<Game> getGamesList(int sessionId) throws AuthenticationFailedException, ConnectionFailedException {
		return gameList;
	}

	@Override
	public int createGame() throws ConnectionFailedException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void joinGame(int sessionId, String name) throws ConnectionFailedException {
		for(Game g : gameList) {
			if(g.getName().equals(name)) {
				System.out.println("Session " + sessionId + " joining " + name + ".");
				sessions.put(sessionId, g);
				String username = users.get(sessionId);
				Player player = new Player(username);
				g.getActivePlayers().add(player);
				break;
			}
		}
	}
}
