package com.kmj.empire.client;

import java.util.ArrayList;
import java.util.HashMap;

import com.kmj.empire.common.AuthenticationFailedException;
import com.kmj.empire.common.Base;
import com.kmj.empire.common.ConnectionFailedException;
import com.kmj.empire.common.EmpireType;
import com.kmj.empire.common.Game;
import com.kmj.empire.common.GameService;
import com.kmj.empire.common.Planet;
import com.kmj.empire.common.Player;
import com.kmj.empire.common.Ship;
import com.kmj.empire.common.ShipType;
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
		addSampleData();
		
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
				
				// Procedure to add a new player. In this case, the player
				// is using a Constitution-class vessel.
				sessions.put(sessionId, g);
				String username = users.get(sessionId);
				g.addPlayer(username, new Ship(new ShipType("Constitution"), new EmpireType("Federation")));
				
				// Log player joining.
				g.getLog().add(g.getStardate() + ": " + username + " joined.");
				break;
			}
		}
	}
	
	private void addSampleData() {
		Planet planet = new Planet();
		planet.setSector(3, 2);
		planet.setLocation(4, 8);
		gameList.get(0).addPlanet(planet);
		
		Base base = new Base(new EmpireType("Federation"));
		base.setSector(6, 1);
		base.setLocation(7, 4);
		gameList.get(0).addBase(base);
		
		base = new Base(new EmpireType("Klingon"));
		base.setSector(6, 2);
		base.setLocation(7, 4);
		gameList.get(0).addBase(base);
		
		Ship ship = new Ship(new ShipType("Bird of Prey"), new EmpireType("Klingon"));
		ship.setX(2);
		ship.setY(2);
		ship.setSector(4,7);
		gameList.get(0).addShip(ship);
	}
}
