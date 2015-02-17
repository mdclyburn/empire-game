package com.kmj.empire.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import com.kmj.empire.common.AuthenticationFailedException;
import com.kmj.empire.common.BadDestinationException;
import com.kmj.empire.common.Base;
import com.kmj.empire.common.ConnectionFailedException;
import com.kmj.empire.common.EmpireType;
import com.kmj.empire.common.EnergyWeaponType;
import com.kmj.empire.common.Game;
import com.kmj.empire.common.GameService;
import com.kmj.empire.common.MissleWeaponType;
import com.kmj.empire.common.Planet;
import com.kmj.empire.common.Player;
import com.kmj.empire.common.Sector;
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
		startrek.getWeaponTypes().add(new EnergyWeaponType("PCAN", "Pulse Cannon", 150));
		startrek.getWeaponTypes().add(new MissleWeaponType("GTOR", "Gravimetric Torpedo", 800));
		startrek.getEmpire("Klingon").getShipTypes().add(
				new ShipType("BOP", "Bird of Prey", "D-12", startrek.getEmpire("Klingon"), 3000, 10, 500, 10, 
				startrek.getWeapon("PCAN"), startrek.getWeapon("GTOR")));
		startrek.getWeaponTypes().add(new EnergyWeaponType("PHAS", "Phaser", 100));
		startrek.getWeaponTypes().add(new MissleWeaponType("PTOR", "Photon Torpedo", 300));
		startrek.getEmpire("Federation").getShipTypes().add(new ShipType("STC", "Starship", "Constitution", startrek.getEmpire("Federation"), 2500, 10, 500, 10,
				startrek.getWeapon("PHAS"), startrek.getWeapon("PTOR")));
		
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
					Ship ship = g.getPlayerShip(username);
					Player player = new Player(username, ship.getType().getEmpire(), ship);
					g.addPlayer(player);
				}
				else {
					// Get information from user
					NewPlayerDialog d = new NewPlayerDialog(null, "New Player", g);
					d.setVisible(true);
					if(d.getSelectedEmpire().length() == 0) throw new ConnectionFailedException("");
					Random r = new Random();
					Ship ship = new Ship(g.getUniverse().getShip(d.getSelectedShip()), g, g.getSector(1, 1), 1, 1);
					Player player = new Player(username, ship.getType().getEmpire(), ship);
					g.addPlayer(player);
				}

				break;
			}
		}
	}
	
	@Override
	public void disconnect(int sessionId) throws ConnectionFailedException {
		Game game = sessions.get(sessionId);
		game.removePlayer(users.get(sessionId));
	}
	
	@Override
	public void navigate(int sessionId, int x, int y) throws BadDestinationException, ConnectionFailedException {
		String username = users.get(sessionId);
		Game game = sessions.get(sessionId);
		Ship playerShip = game.getPlayerShip(username);
		
		// Make sure the distance is navigable.
		int distance = Math.abs(playerShip.getX() - x) + Math.abs(playerShip.getY() - y);
		
		// Make sure energy level is sufficient.
		if((10 * distance) > playerShip.getEnergy())
			throw new BadDestinationException("There is not enough energy to go there.");
		
		// Check if there is something at the destination.
		Sector currentSector = playerShip.getSector();
		for(Base b : currentSector.getBases())
			if(b.getX() == x && b.getY() == y) throw new BadDestinationException("A base is located here.");
		for(Ship s : currentSector.getShips())
			if(s.getX() == x && s.getY() == y) throw new BadDestinationException("A ship is located here.");
		for(Planet p : currentSector.getPlanets())
			if(p.getX() == x && p.getY() == y) throw new BadDestinationException("A planet is located here.");
		
		// Move player.
		playerShip.setLocation(x, y);
		playerShip.consumeImpulseEnergy(distance);
	}
	
	@Override
	public void warp(int sessionId, Sector sector) throws BadDestinationException, ConnectionFailedException {
		String username = users.get(sessionId);
		Game game = sessions.get(sessionId);
		Ship playerShip = game.getPlayerShip(username);
		
		// Make sure distance is navigable.
		int distance = Math.abs(sector.getX() - playerShip.getX()) + Math.abs(sector.getY() - playerShip.getY());
		int max = playerShip.getType().getMaxSpeed();
		if(distance > max)
			throw new BadDestinationException("The distance is " + (distance - max) + " sectors too far.");
		
		// Make sure energy level is sufficient.
		if((100 * distance) > playerShip.getEnergy())
			throw new BadDestinationException("There is not enough energy to warp that far.");
		
		// Find a place in that sector to warp to.
		System.out.println("Looking for position for " + username + "...");
		for(int y = 1; y <= 8; y++) {
			for(int x = 1; x <= 8; x++) {
				boolean containsEntity = false;
				for(Ship s : sector.getShips()) {
					if(s.getX() == x && s.getY() == y) {
						containsEntity = true;
						break;
					}
				}
				if(!containsEntity) {
					for(Base b : sector.getBases()) {
						if(b.getX() == x && b.getY() == y) {
							containsEntity = true;
							break;
						}
					}
				}
				if(!containsEntity) {
					for(Planet p : sector.getPlanets()) {
						if(p.getX() == x && p.getY() == y) {
							containsEntity = true;
							break;
						}
					}
				}
				
				// If no other entity is in this position, then the player can be
				// warped here.
				if(!containsEntity) {
					playerShip.getSector().getShips().remove(playerShip);
					playerShip.setSector(sector.getX(), sector.getY());
					sector.getShips().add(playerShip);
					playerShip.setX(x);
					playerShip.setY(y);
					playerShip.consumeWarpEnergy(distance);
					return;
				}
			}
		}
		
		// If this is reached, then the sector is full.
		throw new BadDestinationException("The sector is full.");
	}
	
	public void fireTorpedo(int sessionId, Ship target) throws ActionException, ConnectionFailedException {
	}
	
	private void addSampleData() {
		
		Planet planet = new Planet(gameList.get(0), gameList.get(0).getSector(1, 1), 3, 2);
		gameList.get(0).addPlanet(planet);
		
		Base base = new Base(gameList.get(0).getUniverse().getEmpire("Klingon"), gameList.get(0), gameList.get(0).getSector(1, 1), 3, 4);
		gameList.get(0).addBase(base);
		
		base = new Base(gameList.get(0).getUniverse().getEmpire("Federation"), gameList.get(0), gameList.get(0).getSector(1, 1), 2, 5);
		gameList.get(0).addBase(base);
		
		Ship ship = new Ship(gameList.get(0).getUniverse().getEmpire("Klingon").getShip("Bird of Prey"), gameList.get(0), gameList.get(0).getSector(1, 1), 7, 4);
		gameList.get(0).addShip(ship);
	}
}
