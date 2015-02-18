package com.kmj.empire.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import com.kmj.empire.client.ActionException;
import com.kmj.empire.common.AlertLevel;
import com.kmj.empire.common.AuthenticationFailedException;
import com.kmj.empire.common.BadDestinationException;
import com.kmj.empire.common.Base;
import com.kmj.empire.common.ConnectionFailedException;
import com.kmj.empire.common.EmpireType;
import com.kmj.empire.common.Game;
import com.kmj.empire.common.GameService;
import com.kmj.empire.common.Player;
import com.kmj.empire.common.Sector;
import com.kmj.empire.common.Ship;
import com.kmj.empire.common.ShipType;
import com.kmj.empire.common.UniverseType;
import com.kmj.empire.common.WeaponType;

public class GameServiceImpl implements GameService {
	
	private Server server;
	
	public GameServiceImpl(Server server) {
		this.server = server;
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
				Player player = new Player(playerid, empire, restoredGame.getIdShip(shipId));
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
	public Game getGameState(int gameId) {
		Game gameState = server.getGame(gameId);
		return gameState;
	}

	@Override
	public int authenticate(String user, String password) {
		
		return 0;
	}

	@Override
	public int createGame() {
		
		return 0;
	}

	@Override
	public void joinGame(int sessionId, String name)
			throws ConnectionFailedException {
	}
	
	@Override
	public ArrayList<Game> getGamesList(int sessionId)
			throws AuthenticationFailedException, ConnectionFailedException {
		return null;
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

}
