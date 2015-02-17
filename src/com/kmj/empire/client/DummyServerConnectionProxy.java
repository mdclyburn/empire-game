package com.kmj.empire.client;

import java.util.ArrayList;
import java.util.HashMap;

import com.kmj.empire.common.AuthenticationFailedException;
import com.kmj.empire.common.Base;
import com.kmj.empire.common.ConnectionFailedException;
import com.kmj.empire.common.EmpireType;
import com.kmj.empire.common.EnergyWeaponType;
import com.kmj.empire.common.Game;
import com.kmj.empire.common.GameService;
import com.kmj.empire.common.MissleWeaponType;
import com.kmj.empire.common.Planet;
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
		
		UniverseType startrek = new UniverseType("Star Trek");
		EmpireType klingon = new EmpireType("KLI", "Klingon", "Aggression");
		startrek.getEmpireList().add(new EmpireType("FED", "Federation", "Exploration"));
		startrek.getEmpireList().add(klingon);
		startrek.getEmpire("Klingon").getWeaponTypes().add(new EnergyWeaponType("PCAN", "Pulse Cannon", 150));
		startrek.getEmpire("Klingon").getWeaponTypes().add(new MissleWeaponType("GTOR", "Gravimetric Torpedo", 800));
		startrek.getEmpire("Klingon").getShipTypes().add(
				new ShipType("BOP", "Bird of Prey", "D-12", startrek.getEmpire("Klingon"), 10, 10, 10, 10, 
				startrek.getEmpire("Klingon").getWeapon("PCAN"), startrek.getEmpire("Klingon").getWeapon("GTOR")));
		startrek.getEmpire("Federation").getWeaponTypes().add(new EnergyWeaponType("PHAS", "Phaser", 100));
		startrek.getEmpire("Federation").getWeaponTypes().add(new MissleWeaponType("PTOR", "Photon Torpedo", 300));
		startrek.getEmpire("Federation").getShipTypes().add(new ShipType("STC", "Starship", "Constitution", startrek.getEmpire("Federation"), 10, 10, 10, 10,
				startrek.getEmpire("Federation").getWeapon("PHAS"), startrek.getEmpire("Federation").getWeapon("PTOR")));
		
		gameList.add(new Game("Trekkie's Delight", startrek));
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
	public int restoreGame(String gameData) throws ConnectionFailedException {
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
				
				String username = users.get(sessionId);
				sessions.put(sessionId, g);
				
				if(g.hasPlayed(username)) {
					g.addPlayer(username, null);
				}
				else {
					// Get information from user
					NewPlayerDialog d = new NewPlayerDialog(null, "New Player", g);
					d.setVisible(true);
					if(d.getSelectedEmpire().length() == 0) throw new ConnectionFailedException("");
					g.addPlayer(username, new Ship(g.getUniverse().getEmpire(d.getSelectedEmpire()).getShip(d.getSelectedShip()), g, g.getSector(1, 1), 1, 1));
				}
				
				

				break;
			}
		}
	}
	
	@Override
	public void disconnect(int sessionId) throws ConnectionFailedException {
		Game game = sessions.get(sessionId);
		game.getActivePlayers().remove(users.get(sessionId));
	}
	
	private void addSampleData() {
		
		Planet planet = new Planet(gameList.get(0), gameList.get(0).getSector(4, 4), 3, 2);
		gameList.get(0).addPlanet(planet);
		
		Base base = new Base(gameList.get(0).getUniverse().getEmpire("Klingon"), gameList.get(0), gameList.get(0).getSector(6, 1), 7, 4);
		gameList.get(0).addBase(base);
		
		base = new Base(gameList.get(0).getUniverse().getEmpire("Federation"), gameList.get(0), gameList.get(0).getSector(6, 1), 7, 5);
		gameList.get(0).addBase(base);
		
		Ship ship = new Ship(gameList.get(0).getUniverse().getEmpire("Klingon").getShip("Bird of Prey"), gameList.get(0), gameList.get(0).getSector(6, 1), 7, 4);
		gameList.get(0).addShip(ship);
	}
}
