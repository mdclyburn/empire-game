package com.kmj.empire.common;

import java.util.ArrayList;
import java.util.HashMap;

public class Game {

	protected String name;
	protected ArrayList<String> players;
	protected ArrayList<Ship> ships;
	protected ArrayList<Base> bases;
	protected ArrayList<String> log;
	protected HashMap<String, Ship> possessionMapping;
	protected HashMap<Ship, String> propertyMapping;
	
	protected int stardate;
	protected Sector[][] sectorGrid;
	protected UniverseType universeType;
	
	public Game() {
		stardate = 2500;
		this.sectorGrid = new Sector[8][8];
		for(int y = 0; y < 8; y++) {
			for(int x = 0; x < 8; x++) {
				sectorGrid[x][y] = new Sector();
			}
		}
		players = new ArrayList<String>();
		ships = new ArrayList<Ship>();
		bases = new ArrayList<Base>();
		log = new ArrayList<String>();
		possessionMapping = new HashMap<String, Ship>();
		propertyMapping = new HashMap<Ship, String>();
		name = "Empire Session";
	}
	
	public Game(String name, UniverseType universeType) {
		this();
		this.universeType = universeType;
		this.name = name;
	}
	
	public UniverseType getUniverseType() {
		return universeType;
	}
	
	public Sector[][] getSectorGrid() {
		return sectorGrid;
	}
	
	public Sector getSector(int x, int y) {
		return sectorGrid[x][y];
	}
	
	public int getStardate() { return stardate; }
	public String getName() { return name; }
	public ArrayList<String> getActivePlayers() { return players; }
	public ArrayList<String> getLog() { return log; }
	
	public void addPlayer(String username, Ship ship) {
		possessionMapping.put(username, ship);
		propertyMapping.put(ship, username);
		players.add(username);
		sectorGrid[0][0].getShips().add(ship);
	}
	
	public void addShip(Ship ship) {
		int x = ship.getUniverseLocation().x;
		int y = ship.getUniverseLocation().y;
		sectorGrid[x - 1][y - 1].getShips().add(ship);
	}
	
	public void addPlanet(Planet planet) {
		int x = planet.getUniverseLocation().x;
		int y = planet.getUniverseLocation().y;
		sectorGrid[x - 1][y - 1].getPlanets().add(planet);
	}
	
	public void addBase(Base base) {
		int x = base.getUniverseLocation().x;
		int y = base.getUniverseLocation().y;
		sectorGrid[x - 1][y - 1].getBases().add(base);
	}
	
	public Ship getPlayerShip(String player) {
		return possessionMapping.get(player);
	}
	
	public String getOwner(Ship ship) {
		return propertyMapping.get(ship);
	}

}
