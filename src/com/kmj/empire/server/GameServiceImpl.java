package com.kmj.empire.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Random;

import com.kmj.empire.client.ui.NewPlayerDialog;
import com.kmj.empire.common.AlertLevel;
import com.kmj.empire.common.Base;
import com.kmj.empire.common.EmpireType;
import com.kmj.empire.common.Game;
import com.kmj.empire.common.GameService;
import com.kmj.empire.common.GameState;
import com.kmj.empire.common.Planet;
import com.kmj.empire.common.Player;
import com.kmj.empire.common.Sector;
import com.kmj.empire.common.Ship;
import com.kmj.empire.common.ShipType;
import com.kmj.empire.common.UniverseType;
import com.kmj.empire.common.WeaponType;
import com.kmj.empire.common.exceptions.ActionException;
import com.kmj.empire.common.exceptions.AuthenticationFailedException;
import com.kmj.empire.common.exceptions.BadDestinationException;
import com.kmj.empire.common.exceptions.ConnectionFailedException;

public class GameServiceImpl implements GameService {
	
	private Server server;
	private User user;
	
	public GameServiceImpl(Server server, User user) {
		this.server = server;
		this.user = user;
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
				EmpireType empire = EmpireType.fromString(line);
				universe.getEmpireList().add(empire);
				line = br.readLine();
			}
		
			//read weapons
			br.readLine();
			line = br.readLine();
			while (!line.equals("")) {
				WeaponType weapon = WeaponType.fromString(line);
				universe.getWeaponTypes().add(weapon);
				line = br.readLine();
			}
			
			//read shiptypes
			br.readLine();
			line = br.readLine();
			while (!line.equals("")) {
				ShipType newType = ShipType.fromString(line, universe);
				newType.getEmpire().getShipTypes().add(newType);
				line = br.readLine();
			}
			
			//universe complete; create game
			Game restoredGame = new Game(title, universe);
			
			//read bases
			br.readLine();
			line = br.readLine();
			while (!line.equals("")) {
				Base base = Base.fromString(line, restoredGame);
				restoredGame.addBase(base);
				line = br.readLine();
			}
			
			//read ships
			br.readLine();
			line = br.readLine();
			while (!line.equals("")) {
				Ship ship = Ship.fromString(line, restoredGame);
				restoredGame.addShip(ship);
				line = br.readLine();
			}

			//read players
			br.readLine();
			line = br.readLine();
			while (!line.equals("") && line != null) {
				Player player = Player.fromString(line, restoredGame);
				restoredGame.getPossessionMapping().put(player.getUserame(), player.getShip().getId());
				restoredGame.getPropertyMapping().put(player.getShip().getId(),player.getUserame());
				restoredGame.addPlayer(player);
				line = br.readLine();
			}
			br.close();
			
			restoredGame.setStardate(stardate);
			
			int gameId = server.addGame(restoredGame);
			return gameId;
		} catch (IOException ioe) {
			System.err.println("Failed to read .dat file, inproper format.");
			return -1;
		}
	}

	@Override
	public GameState getGameState(int gameId) {
		System.out.println("Game id of server is: "+gameId);
		Game game = server.getGame(gameId);
		return new GameState(game);
	}

	@Override
	public int authenticate(String user, String password) throws AuthenticationFailedException {
		if (password.equals("p")) return 0;
		else return -1;
	}

	@Override
	public int createGame() {
		
		return 0;
	}
	
	@Override
	public ArrayList<Game> getGamesList(int sessionId)
			throws AuthenticationFailedException, ConnectionFailedException {
		return server.getGamesList();
	}

	@Override
	public void disconnect(int sessionId) throws ConnectionFailedException {
	}

	@Override
	public void navigate(int sessionId, int x, int y) throws BadDestinationException, ConnectionFailedException {
		Game game = server.getPlayerGame(sessionId);
		Ship playerShip = game.getPlayerShip(user.getUsername());

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
		//advance(game);
		//game.nextStardate();
	}

	@Override
	public void warp(int sessionId, Sector sector) throws BadDestinationException, ConnectionFailedException {
		Game game = server.getPlayerGame(sessionId);
		Ship playerShip = game.getPlayerShip(user.getUsername());

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
					return;
				}
			}
		}

		// If this is reached, then the sector is full.
		throw new BadDestinationException("The sector is full.");
	}

	@Override
	public void setAlertLevel(int sessionId, AlertLevel level) throws ConnectionFailedException {
		user.getPlayer().getShip().setAlert(level);

		// Log entry.
		server.getPlayerGame(sessionId).getLog().add(0, server.getPlayerGame(sessionId).getStardate() + ": " + 
				user.getUsername() + " is on " + level.toString().toLowerCase() + " alert.");
	}

	@Override
	public void fireTorpedo(int sessionId, Sector sector, int x, int y) throws ActionException, ConnectionFailedException {
		Game game = server.getPlayerGame(sessionId);
		Ship playerShip = game.getPlayerShip(user.getUsername());

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
			target.setShield(target.getShield() - (playerShip.getType().getMissileWeapon().getMaxYield() / 2));
			if(target.getShield() < 0) game.destroy(target);
		}
		else {
			// Damaged by 100% of the missile's yield.
			target.setShield(target.getShield() - playerShip.getType().getMissileWeapon().getMaxYield());
			if(target.getShield() < 0) game.destroy(target);
		}

		// Remove a missile.
		playerShip.setMissles(playerShip.getMissles() - 1);

		// Log the event.
		String entry = game.getStardate() + ": " + source + " at (" +
				playerShip.getX() + ", " + playerShip.getY() + ") fired " +
				playerShip.getType().getMissileWeapon().getName() + " at " + dest +
				" at (" + target.getX() + ", " + target.getY() + "); ";

		if(target.getShield() > 0)
			entry += "target's shields now at " + target.getShield();
		else
			entry += "target destroyed.";
		game.getLog().add(0, entry);
	}

	@Override
	public void joinGame(int sessionId, int id) throws ConnectionFailedException {
		for(Game g : server.getGamesList()) {
			if(g.getId() == id) {
				System.out.println("Session " + sessionId + " joining " + g.getName() + "(" + g.getId() + ").");

				String username = user.getUsername();
				
				try {
					user.getOut().writeBoolean(g.hasPlayed(username));
				} catch (IOException e) {
					throw new ConnectionFailedException("");
				}

				if(g.hasPlayed(username)) {
					Ship ship = g.getPlayerShip(username);
					Player player = new Player(username, ship.getType().getEmpire(), ship);
					g.addPlayer(player);
				}
				else {
					System.out.println("Adding new player to game.");
					// Get information from user
					String shipType = "";
					try {
						shipType = user.getIn().readUTF();
					} catch (IOException e) {
						throw new ConnectionFailedException("");
					}
					System.out.println("shipType String: "+shipType);
					shipType = shipType.substring(shipType.indexOf('(')+1,shipType.indexOf(')'));
					System.out.println("id : "+shipType);
					
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
					Ship ship = new Ship(g.getUniverse().getShip(shipType), g, sector, x, y);
					Player player = new Player(username, ship.getType().getEmpire(), ship);
					g.addPlayer(player);
					user.setPlayer(player);
				}
				server.setPlayerGame(sessionId, g);
				break;
			}
		}
	}

}
