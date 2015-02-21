package com.kmj.empire.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import com.kmj.empire.client.ui.NewPlayerDialog;
import com.kmj.empire.common.AlertLevel;
import com.kmj.empire.common.AuthenticationFailedException;
import com.kmj.empire.common.BadDestinationException;
import com.kmj.empire.common.Base;
import com.kmj.empire.common.ConnectionFailedException;
import com.kmj.empire.common.EmpireType;
import com.kmj.empire.common.EnergyWeaponType;
import com.kmj.empire.common.Game;
import com.kmj.empire.common.GameService;
import com.kmj.empire.common.Planet;
import com.kmj.empire.common.Player;
import com.kmj.empire.common.Sector;
import com.kmj.empire.common.Ship;
import com.kmj.empire.common.ShipType;
import com.kmj.empire.common.UniverseType;
import com.kmj.empire.common.WeaponType;

// A dummy connection service to get the client-side prototype
// to proceed to show off functionality. All instances of its use
// should be later replaced with a real, more robust implementation
// with actual connection to the server soon.

public class DummyServerConnectionProxy implements GameService {

	int idCounter;
	ArrayList<Game> gameList;
	HashMap<Integer, String> users;
	HashMap<Integer, Game> sessions;

	public DummyServerConnectionProxy() {
		idCounter = 0;
		gameList = new ArrayList<Game>();

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
	public int restoreGame(String gameData) {
		BufferedReader br = new BufferedReader(new StringReader(gameData));
		try {
			br.readLine();
			String line = br.readLine();
			String title = line.substring(0, line.indexOf('\t'));
			int stardate = Integer.valueOf(line.substring(line.indexOf('\t')+1, line.length()));
			br.readLine();
			br.readLine();
			UniverseType universe = new UniverseType(title);

			//read empires
			line = br.readLine();
			while (!line.equals("")) {
				String id = line.substring(0, line.indexOf('\t'));
				line = line.substring(line.indexOf('\t')+1);
				String name = line.substring(0, line.indexOf('\t'));
				line = line.substring(line.indexOf('\t')+1);
				String mission = line.substring(0, line.length());
				EmpireType empire = new EmpireType(id, name, mission);
				universe.getEmpireList().add(empire);
				line = br.readLine();
			}

			//read weapons
			br.readLine();
			line = br.readLine();
			while (!line.equals("")) {
				String id = line.substring(0, line.indexOf('\t'));
				line = line.substring(line.indexOf('\t')+1);
				String name = line.substring(0, line.indexOf('\t'));
				line = line.substring(line.indexOf('\t')+1);
				String type = line.substring(0, line.indexOf('\t'));
				line = line.substring(line.indexOf('\t')+1);
				int maxYield = Integer.valueOf(line.substring(0, line.length()));
				WeaponType weapon = new WeaponType(id, name, type, maxYield);
				universe.getWeaponTypes().add(weapon);
				line = br.readLine();
			}

			//read shiptypes
			br.readLine();
			line = br.readLine();
			while (!line.equals("")) {
				String id = line.substring(0, line.indexOf('\t'));
				line = line.substring(line.indexOf('\t')+1);
				String name = line.substring(0, line.indexOf('\t'));
				line = line.substring(line.indexOf('\t')+1);
				String shipClass = line.substring(0, line.indexOf('\t'));
				line = line.substring(line.indexOf('\t')+1);
				String empire = line.substring(0, line.indexOf('\t'));
				line = line.substring(line.indexOf('\t')+1);
				int maxEnergy = Integer.valueOf(line.substring(0, line.indexOf('\t')));
				line = line.substring(line.indexOf('\t')+1);
				int maxSpeed = Integer.valueOf(line.substring(0, line.indexOf('\t')));
				line = line.substring(line.indexOf('\t')+1);
				int maxShield = Integer.valueOf(line.substring(0, line.indexOf('\t')));
				line = line.substring(line.indexOf('\t')+1);
				WeaponType energyWeapon = universe.getWeapon(line.substring(0, line.indexOf('\t')));
				line = line.substring(line.indexOf('\t')+1);
				WeaponType missileWeapon = universe.getWeapon(line.substring(0, line.indexOf('\t')));
				line = line.substring(line.indexOf('\t')+1);
				int maxMissile = Integer.valueOf(line.substring(0, line.length()));
				ShipType ship = new ShipType(id, name, shipClass, universe.getEmpire(empire), maxEnergy, maxSpeed,
						maxShield, maxMissile, energyWeapon, missileWeapon);
				universe.getEmpire(empire).getShipTypes().add(ship);
				line = br.readLine();
			}

			//universe complete; create game
			Game restoredGame = new Game(title, universe);

			//read bases
			br.readLine();
			line = br.readLine();
			while (!line.equals("")) {
				int id = Integer.valueOf(line.substring(0, line.indexOf('\t')));
				line = line.substring(line.indexOf('\t')+1);
				EmpireType empire = universe.getEmpire(line.substring(0, line.indexOf('\t')));
				line = line.substring(line.indexOf('\t')+1);
				int sx = Integer.valueOf(line.substring(0, line.indexOf('\t')));
				line = line.substring(line.indexOf('\t')+1);
				int sy = Integer.valueOf(line.substring(0, line.indexOf('\t')));
				line = line.substring(line.indexOf('\t')+1);
				int px = Integer.valueOf(line.substring(0, line.indexOf('\t')));
				line = line.substring(line.indexOf('\t')+1);
				int py = Integer.valueOf(line.substring(0, line.length()));
				Base base = new Base(empire, restoredGame, restoredGame.getSector(sx, sy), px, py);
				base.setId(id);
				restoredGame.addBase(base);
				line = br.readLine();
			}

			//read ships
			br.readLine();
			line = br.readLine();
			while (!line.equals("")) {
				int id = Integer.valueOf(line.substring(0, line.indexOf('\t')));
				line = line.substring(line.indexOf('\t')+1);
				ShipType shipType = universe.getShip(line.substring(0, line.indexOf('\t')));
				line = line.substring(line.indexOf('\t')+1);
				int sx = Integer.valueOf(line.substring(0, line.indexOf('\t')));
				line = line.substring(line.indexOf('\t')+1);
				int sy = Integer.valueOf(line.substring(0, line.indexOf('\t')));
				line = line.substring(line.indexOf('\t')+1);
				int px = Integer.valueOf(line.substring(0, line.indexOf('\t')));
				line = line.substring(line.indexOf('\t')+1);
				int py = Integer.valueOf(line.substring(0, line.indexOf('\t')));
				line = line.substring(line.indexOf('\t')+1);
				int energy = Integer.valueOf(line.substring(0, line.indexOf('\t')));
				line = line.substring(line.indexOf('\t')+1);
				int missiles = Integer.valueOf(line.substring(0, line.indexOf('\t')));
				line = line.substring(line.indexOf('\t')+1);
				AlertLevel alert = AlertLevel.GREEN;
				String alertString = line.substring(0, line.indexOf('\t'));
				if (alertString.equals("YELLOW")) alert = AlertLevel.YELLOW;
				if (alertString.equals("RED")) alert = AlertLevel.RED;
				line = line.substring(line.indexOf('\t')+1);
				int shield = Integer.valueOf(line.substring(0, line.length()));
				Ship ship = new Ship(shipType, restoredGame, restoredGame.getSector(sx, sy), px, py);
				ship.setId(id);
				ship.setEnergy(energy);
				ship.setMissles(missiles);
				ship.setAlert(alert);
				ship.setShield(shield);
				restoredGame.addShip(ship);
				line = br.readLine();
			}

			//read players
			br.readLine();
			line = br.readLine();
			while (!line.equals("") && line != null) {
				String playerid = line.substring(0, line.indexOf('\t'));
				line = line.substring(line.indexOf('\t')+1);
				EmpireType empire = universe.getEmpire(line.substring(0, line.indexOf('\t')));
				line = line.substring(line.indexOf('\t')+1);
				int shipId = Integer.valueOf(line.substring(0, line.length()));
				Ship playerShip = restoredGame.getIdShip(shipId);
				Player player = new Player(playerid, empire, restoredGame.getIdShip(shipId));
				restoredGame.map(player.getUserame(), playerShip);
				restoredGame.addPlayer(player);
				line = br.readLine();
			}
			br.close();

			restoredGame.setStardate(stardate);

			gameList.add(restoredGame);
			int gameId = idCounter++;
			restoredGame.setId(gameId);
			System.out.println("Restored game with ID " + gameId);
			return gameId;
		} catch (IOException ioe) {
			System.err.println("Failed to read .dat file, inproper format.");
			return -1;
		}
	}

	@Override
	public Game getGameState(int sessionId) throws ConnectionFailedException {
		return sessions.get(sessionId);
	}

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
	public void joinGame(int sessionId, int id) throws ConnectionFailedException {
		for(Game g : gameList) {
			if(g.getId() == id) {
				System.out.println("Session " + sessionId + " joining " + g.getName() + "(" + g.getId() + ").");

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

					// Find a random spot.
					Random r = new Random();
					Sector sector = null;
					int sx, sy, x, y;
					sx = sy = x = y = 1;
					while(true) {
						sx = r.nextInt(8) + 1;
						sy = r.nextInt(8) + 1;
						x = r.nextInt(8) + 1;
						y = r.nextInt(8) + 1;
						sector = g.getSector(sx, sy);
						for(Ship s : sector.getShips()) {
							if(s.getX() == x && s.getY() == y) continue;
						}
						for(Base b : sector.getBases()) {
							if(b.getX() == x && b.getY() == y) continue;
						}
						for(Planet p : sector.getPlanets()) {
							if(p.getX() == x && p.getY() == y) continue;
						}

						break;
					}
					System.out.println("Placing ship in " + sx + "-" + sy + ", " + x + "-" + y);
					Ship ship = new Ship(g.getUniverse().getShip(d.getSelectedShip()), g, sector, x, y);
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
	public void setAlertLevel(int sessionId, AlertLevel level) throws ConnectionFailedException {
		sessions.get(sessionId).getPlayerShip(users.get(sessionId)).setAlert(level);

		// Log entry.
		sessions.get(sessionId).getLog().add(0, sessions.get(sessionId).getStardate() + ": " + users.get(sessionId) +
				" is on " + level.toString().toLowerCase() + " alert.");
		advance(sessions.get(sessionId));
		sessions.get(sessionId).nextStardate();
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
		advance(game);
		game.nextStardate();
	}

	@Override
	public void warp(int sessionId, Sector sector) throws BadDestinationException, ConnectionFailedException {
		String username = users.get(sessionId);
		Game game = sessions.get(sessionId);
		Ship playerShip = game.getPlayerShip(username);

		// Make sure distance is navigable.
		int distance = Math.abs(sector.getX() - playerShip.getSector().getX()) + Math.abs(sector.getY() - playerShip.getSector().getY());
		int max = playerShip.getType().getMaxSpeed();
		if(distance > max)
			throw new BadDestinationException("The distance is " + (distance - max) + " sectors too far.");

		// Make sure energy level is sufficient.
		if((100 * distance) > playerShip.getEnergy())
			throw new BadDestinationException("There is not enough energy to warp that far.");

		// Find a place in that sector to warp to.
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
					advance(game);
					game.nextStardate();
					return;
				}
			}
		}

		// If this is reached, then the sector is full.
		throw new BadDestinationException("The sector is full.");
	}

	public void fireTorpedo(int sessionId, Sector sector, int x, int y) throws ActionException, ConnectionFailedException {
		String username = users.get(sessionId);
		Game game = sessions.get(sessionId);
		Ship playerShip = game.getPlayerShip(username);

		// Make sure that the player has torpedoes left.
		if(playerShip.getMissles() == 0)
			throw new ActionException("There are no missiles left.");

		// Make sure that a ship is at that location.
		Ship target = null;
		for(Ship s : sector.getShips()) {
			if(s.getX() == x && s.getY() == y) {
				target = s;
				break;
			}
		}
		if(target == null)
			throw new ActionException("There is no ship at the specified location.");

		// Disallow self-destruction.
		if(playerShip == target)
			throw new ActionException("Don't blow yourself up.");

		// Disallow betrayal.
		if(playerShip.getType().getEmpire().getName().equals(target.getType().getEmpire().getName()))
			throw new ActionException("You are on the same side.");

		// Get source and destination names.
		String source = "";
		String dest = "";
		if(game.getOwner(playerShip) == null)
			source = playerShip.getType().getName();
		else
			source = game.getOwner(playerShip);

		if(game.getOwner(target) == null)
			dest = target.getType().getName();
		else
			dest = game.getOwner(target);

		// At this time, a torpedo never misses.
		if(target.getAlert() == AlertLevel.GREEN) {
			// The ship is immediately destroyed.
			target.setShield(-1);
			game.destroy(target);
		}
		else if(target.getAlert() == AlertLevel.YELLOW) {
			// Damaged by 50% of the missile's yield.
			target.setShield(target.getShield() - (playerShip.getType().getMissleWeapon().getMaxYield() / 2));
			if(target.getShield() < 0) game.destroy(target);
		}
		else {
			// Damaged by 100% of the missile's yield.
			target.setShield(target.getShield() - playerShip.getType().getMissleWeapon().getMaxYield());
			if(target.getShield() < 0) game.destroy(target);
		}

		// Remove a missile.
		playerShip.setMissles(playerShip.getMissles() - 1);

		// Log the event.
		String entry = game.getStardate() + ": " + source + " at (" +
				playerShip.getX() + ", " + playerShip.getY() + ") fired " +
				playerShip.getType().getMissleWeapon().getName() + " at " + dest +
				" at (" + target.getX() + ", " + target.getY() + "); ";

		if(target.getShield() > 0)
			entry += "target's shields now at " + target.getShield();
		else
			entry += "target destroyed.";
		game.getLog().add(0, entry);

		advance(game);
		game.nextStardate();
	}

	private void advance(Game game) {
		ArrayList<Ship> ships = game.getShips();
		for(int i = 0; i < ships.size(); i++) {
			Ship ship = ships.get(i);
			// See if this is an AI ship.
			if(game.getPropertyMapping().get(ship) == null) {
				Sector sector = ship.getSector();
				// Search the sector for an enemy ship.
				ArrayList<Ship> enemies = new ArrayList<Ship>();
				ArrayList<Ship> sectorShips = sector.getShips();
				for(int j = 0; j < sectorShips.size(); j++) {
					Ship possEnemyShip = sectorShips.get(j);
					if(!possEnemyShip.getType().getEmpire().getName().equals(ship.getType().getEmpire().getName()))
						enemies.add(possEnemyShip);
				}

				// Continue search if enemy ships are in the sector.
				if(enemies.size() > 0) {
					// Find the closest.
					Ship closest = null;
					for(int k = 0; k < enemies.size(); k++) {
						Ship enemyShip = enemies.get(k);
						if(closest == null) {
							closest = enemyShip;
						}
						else {
							// Calculate its distance.
							int newDistance = Math.abs(ship.getX() - enemyShip.getX()) + Math.abs(ship.getY() - enemyShip.getY());
							int oldDistance = Math.abs(ship.getX() - closest.getX()) + Math.abs(ship.getY() - closest.getY());
							if(newDistance < oldDistance) closest = enemyShip;
						}
					}

					// Attack.
					String dest = "";
					if(game.getOwner(closest) == null) dest = closest.getType().getName();
					else dest = game.getOwner(closest);
					if(closest.getAlert() == AlertLevel.GREEN) {
						// The ship is immediately destroyed.
						closest.setShield(-1);
						game.destroy(closest);
					}
					else if(closest.getAlert() == AlertLevel.YELLOW) {
						// Damaged by 50% of the missile's yield.
						closest.setShield(closest.getShield() - (ship.getType().getMissleWeapon().getMaxYield() / 2));
						if(closest.getShield() < 0) game.destroy(closest);
					}
					else {
						// Damaged by 100% of the missile's yield.
						closest.setShield(closest.getShield() - ship.getType().getMissleWeapon().getMaxYield());
						if(closest.getShield() < 0) game.destroy(closest);
					}

					// Remove a missile.
					ship.setMissles(ship.getMissles() - 1);

					// Log the event.
					String entry = game.getStardate() + ": " + ship.getType().getName() + " at (" +
							ship.getX() + ", " + ship.getY() + ") fired " +
							ship.getType().getMissleWeapon().getName() + " at " + dest +
							" at (" + closest.getX() + ", " + closest.getY() + "); ";

					if(closest.getShield() > 0)
						entry += "target's shields now at " + closest.getShield();
					else
						entry += "target destroyed.";
					game.getLog().add(0, entry);
				}
			}
		}
	}

	private void addSampleData() {

		Planet planet = new Planet(gameList.get(0), gameList.get(0).getSector(1, 1), 3, 2);
		gameList.get(0).addPlanet(planet);

		Base base = new Base(gameList.get(0).getUniverse().getEmpire("Klingon"), gameList.get(0), gameList.get(0).getSector(1, 1), 3, 4);
		gameList.get(0).addBase(base);

		base = new Base(gameList.get(0).getUniverse().getEmpire("Federation"), gameList.get(0), gameList.get(0).getSector(1, 1), 2, 5);
		gameList.get(0).addBase(base);

		// AI ship
		Ship ship = new Ship(gameList.get(0).getUniverse().getEmpire("Klingon").getShip("Bird of Prey"), gameList.get(0), gameList.get(0).getSector(1, 1), 7, 4);
		ship.setAlert(AlertLevel.RED);

		gameList.get(0).addShip(ship);
	}
}
