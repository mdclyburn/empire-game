/* GameServiceImpl Class
 * 
 * This class handles requests from the server dealing with the domain model.
 * When a server receives a request to needs to take an action, get a game, etc,
 * it calls the methods of this class and returns necessary information.
 */

package com.kmj.empire.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Random;

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

	/**
	 * Restores a game to the server and returns the id.
	 * <p>
	 * Accepts a string argument called gameData, and parses it for
	 * data of an old game. It then adds the game to the servers
	 * game list and returns the id that was assigned to it.
	 * 
	 * @param 	gameData	the string containing the game data to be restored
	 * @return				the id of the restored game
	 */
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
				restoredGame.getPossessionMapping().put(player.getUsername(), player.getShip().getId());
				restoredGame.getPropertyMapping().put(player.getShip().getId(),player.getUsername());
				restoredGame.getHasPlayed().add(player.getUsername());
				line = br.readLine();
			}
			br.close();
			
			//set the stardate of the game
			restoredGame.setStardate(stardate);
			
			int gameId = server.addGame(restoredGame);
			return gameId;
		} catch (IOException ioe) {
			server.printMessage("Failed to read .dat file, inproper format.");
			return -1;
		}
	}

	/* 

	/**
	 * Gets a GameState object from the server of the desired game.
	 * <p>
	 * Accepts a integer argument gameId and retrieves that game from the server's
	 * game list.
	 * 
	 * @param 	gameId	the id of the desired game
	 * @return			the GameState object of the specified game
	 */
	@Override
	public GameState getGameState(int gameId) {
		Game game = server.getGame(gameId);
		return new GameState(game);
	}

	/**
	 *  Authenticates a user.
	 *  <p>
	 *  Accepts username and password stings and determines if they are associated with
	 *  a valid user. If so it returns 0, indicating success. Otherwise it returns -1.
	 *  
	 *  @param	user		username of the caller
	 *  @param	password	password of the caller
	 *  @return				success
	 */
	@Override
	public int authenticate(String user, String password) throws AuthenticationFailedException {
		if (password.equals("p")) return 0;
		else return -1;
	}

	/* not yet implemented */
	@Override
	public int createGame() {
		
		return 0;
	}
	
	/**
	 * Returns a list of active games.
	 * <p>
	 * Accepts the session id of the user and returns the ArrayList of
	 * games belonging to the server.
	 * 
	 *  @param	sessionId	users sessionId
	 *  @return	gamesList	ArrayList of active games on the server
	 */
	@Override
	public ArrayList<Game> getGamesList(int sessionId)
			throws AuthenticationFailedException, ConnectionFailedException {
		return server.getGamesList();
	}

	/**
	 *  Removes the player from their current game.
	 *  <p>
	 *  Accepts a sessionId and removes the associated user from the
	 *  game to which they are currently connected.
	 *  
	 *  @param	sessionID	the users sessionId
	 *  @return	void 
	 */
	@Override
	public void disconnect(int sessionId) throws ConnectionFailedException {
		server.getPlayerGame(sessionId).removePlayer(user.getUsername());
	}

	/**
	 *  Allows a user to navigate their ship within current sector.
	 *  <p>
	 *  Accepts a sessionId that identifies the user and thus ship that is being
	 *  requested to move. It also accepts the x and y coordinates of the desired
	 *  destination. With this it determines whether or not the destination can be
	 *  reached and throws appropriate exceptions if it cannot be. Otherwise it
	 *  returns nothing.
	 *  
	 *  @param	sessionId	users sessionId
	 *  @param	x			x cord of desired navigate destination
	 *  @param	y			y cord of desired navigate destination
	 *  @return	void
	 */
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
	}

	/**
	 *  Warps a ship to a nearby sector.
	 *  <p>
	 *  Accepts the sessionId to identify the ship and the desired warp destination
	 *  sector. It determines if the sector can be reached and if not throws
	 *  appropriate exceptions. It returns nothing if successful.
	 *  
	 *  @param	sessionId	users sessionId
	 *  @param	sector		the sector attempting to warp to
	 */
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

	/** 
	 * Sets a ships alert level.
	 * <p>
	 * Accepts a sessionId to identify a ship and an AlertLevel to
	 * set it to. This is printed to the attack log on the server.
	 * 
	 * @param	sessionId	users sessionId
	 * @param	level		desired AlertLevel
	 * @return	void
	 */
	@Override
	public void setAlertLevel(int sessionId, AlertLevel level) throws ConnectionFailedException {
		Game game = server.getPlayerGame(sessionId);
		Ship playerShip = game.getPlayerShip(user.getUsername());
		playerShip.setAlert(level);

		// Log entry.
		server.getPlayerGame(sessionId).getLog().add(0, server.getPlayerGame(sessionId).getStardate() + ": " + 
				user.getUsername() + " is on " + level.toString().toLowerCase() + " alert.");
	}

	/**
	 * Fires a torpedo from a ship.
	 * <p>
	 * Accepts a sessionId, sector, x coordinate, and y coordinate. It 
	 * determines if the desired firing location is valid for the ship
	 * identified by sessionId, and fires a torpedo. This is recorded in
	 * the servers attack log.
	 * 
	 * @param	sessionId	users id
	 * @param	sector		the sector that the ship is currently in
	 * @param	x			x coordinate of desired firing target
	 * @param	y			y coordinate of desired firing target
	 * @return	void
	 */
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

	/**
	 * Places the user in the desired game.
	 * <p>
	 * Accepts sessionId to identify user and the id of the game to be
	 * joined. It determines if the user is rejoining or not and prompts them
	 * for necessary information if they are joining for the first time.
	 * 
	 * @param	sessionId	users is
	 * @param	id			id of the game to join
	 * @return 	void
	 */
	@Override
	public void joinGame(int sessionId, int id) throws ConnectionFailedException {
		for(Game g : server.getGamesList()) {
			if(g.getId() == id) {
				server.printMessage("Session " + sessionId + " joining " + g.getName() + "(" + g.getId() + ").");

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
					server.printMessage("Adding new player to game.");
					// Get information from user
					String shipType = "";
					try {
						shipType = user.getIn().readUTF();
						if (shipType.equals("")) return;
					} catch (IOException e) {
						throw new ConnectionFailedException("");
					}
					shipType = shipType.substring(shipType.indexOf('(')+1,shipType.indexOf(')'));
					
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
					server.printMessage("Placing ship in " + sx + "-" + sy + ", " + x + "-" + y);
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
