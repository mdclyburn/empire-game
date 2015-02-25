package com.kmj.empire.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Random;

import com.kmj.empire.client.controller.ActionException;
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
	public void navigate(int sessionId, int x, int y)
			throws BadDestinationException, ConnectionFailedException {
	}

	@Override
	public void warp(int sessionId, Sector sector)
			throws BadDestinationException, ConnectionFailedException {
	}

	@Override
	public void setAlertLevel(int sessionId, AlertLevel level)
			throws ConnectionFailedException {
	}

	@Override
	public void fireTorpedo(int sessionId, Sector sector, int x, int y)
			throws ActionException, ConnectionFailedException {
	}

	@Override
	public void joinGame(int sessionId, int id) throws ConnectionFailedException {
		for(Game g : server.getGamesList()) {
			if(g.getId() == id) {
				System.out.println("Session " + sessionId + " joining " + g.getName() + "(" + g.getId() + ").");

				String username = user.getUsername();

				if(g.hasPlayed(username)) {
					Ship ship = g.getPlayerShip(username);
					Player player = new Player(username, ship.getType().getEmpire(), ship);
					g.addPlayer(player);
				}
				else {
					System.out.println("Adding new player to game.");
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
				System.out.println("Player ship exists: "+g.getPlayerShip(username));
				break;
			}
		}
	}

}
