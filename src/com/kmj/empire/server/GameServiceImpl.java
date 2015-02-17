package com.kmj.empire.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.Socket;
import java.util.ArrayList;

import com.kmj.empire.common.AlertLevel;
import com.kmj.empire.common.AuthenticationFailedException;
import com.kmj.empire.common.Base;
import com.kmj.empire.common.ConnectionFailedException;
import com.kmj.empire.common.EmpireType;
import com.kmj.empire.common.Game;
import com.kmj.empire.common.GameService;
import com.kmj.empire.common.Player;
import com.kmj.empire.common.Ship;
import com.kmj.empire.common.ShipType;
import com.kmj.empire.common.UniverseType;
import com.kmj.empire.common.WeaponType;

public class GameServiceImpl implements GameService {
	
	private Server server;
	private Socket socket;
	
	public GameServiceImpl(Server server, Socket socket) {
		this.server = server;
		this.socket = socket;
	}

	@Override
	public int restoreGame(String gameData) {
		BufferedReader br = new BufferedReader(new StringReader(gameData));
		try {
			br.readLine();
			String line = br.readLine();
			String title = line.substring(0, line.indexOf('\t'));
			int stardate = Integer.valueOf(line.substring(line.indexOf('\t'+1), line.indexOf('\n')));
			br.readLine();
			br.readLine();
			UniverseType universe = new UniverseType(title);
			
			//read empires
			line = br.readLine();
			while (!line.equals("\n")) {
				String id = line.substring(0, line.indexOf('\t'));
				line = line.substring(line.indexOf('\t')+1);
				String name = line.substring(0, line.indexOf('\t'));
				line = line.substring(line.indexOf('\t')+1);
				String mission = line.substring(0, line.indexOf('\n'));
				EmpireType empire = new EmpireType(id, name, mission);
				universe.getEmpireList().add(empire);
				line = br.readLine();
			}
		
			//read weapons
			br.readLine();
			line = br.readLine();
			while (!line.equals("\n")) {
				String id = line.substring(0, line.indexOf('\t'));
				line = line.substring(line.indexOf('\t')+1);
				String name = line.substring(0, line.indexOf('\t'));
				line = line.substring(line.indexOf('\t')+1);
				String type = line.substring(0, line.indexOf('\t'));
				line = line.substring(line.indexOf('\t')+1);
				int maxYield = Integer.valueOf(line.substring(0, line.indexOf('\n')));
				WeaponType weapon = new WeaponType(id, name, type, maxYield);
				universe.getWeaponTypes().add(weapon);
				line = br.readLine();
			}
			
			//read shiptypes
			br.readLine();
			line = br.readLine();
			while (!line.equals("\n")) {
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
				int maxMissile = Integer.valueOf(line.substring(0, line.indexOf('\n')));
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
			while (!line.equals("\n")) {
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
				int py = Integer.valueOf(line.substring(0, line.indexOf('\n')));
				Base base = new Base(empire);
				base.setSector(sx, sy);
				base.setLocation(px, py);
				restoredGame.addBase(base);
				line = br.readLine();
			}
			
			//read ships
			br.readLine();
			line = br.readLine();
			while (!line.equals("\n")) {
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
				int shield = Integer.valueOf(line.substring(0, line.indexOf('\n')));
				Ship ship = new Ship(shipType);
				ship.setId(id);
				ship.setSector(sx, sy);
				ship.setLocation(px, py);
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
			while (!line.equals("\n")) {
				String playerid = line.substring(0, line.indexOf('\t'));
				line = line.substring(line.indexOf('\t')+1);
				EmpireType empire = universe.getEmpire(line.substring(0, line.indexOf('\t')));
				line = line.substring(line.indexOf('\t')+1);
				int shipId = Integer.valueOf(line.substring(0, line.indexOf('\n')));
				Player player = new Player(playerid, empire, restoredGame.getIdShip(shipId));
				restoredGame.addPlayer(player);
				line = br.readLine();
			}
			br.close();
			
			server.getGamesList().add(restoredGame);
		} catch (IOException ioe) {
			System.err.println("Failed to read .dat file, inproper format.");
			return -1;
		}
		return 0;
	}

	@Override
	public Game getGameState(int gameId) {
		
		return null;
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

}
