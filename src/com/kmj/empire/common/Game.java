package com.kmj.empire.common;

import java.util.ArrayList;
import java.util.HashMap;

public class Game {

	private int id;
	private String name;
	private ArrayList<Player> players;
	private ArrayList<Ship> ships;
	private ArrayList<Base> bases;
	private ArrayList<Planet> planets;
	private ArrayList<String> log;
	private HashMap<String, Ship> possessionMapping;
	private HashMap<Ship, String> propertyMapping;
	
	private int stardate;
	private Sector[][] sectorGrid;
	private UniverseType universe;
	
	public Game() {
		stardate = 2500;
		this.sectorGrid = new Sector[8][8];
		for(int y = 0; y < 8; y++) {
			for(int x = 0; x < 8; x++) {
				sectorGrid[x][y] = new Sector(x + 1, y + 1);
			}
		}
		players = new ArrayList<Player>();
		ships = new ArrayList<Ship>();
		bases = new ArrayList<Base>();
		planets = new ArrayList<Planet>();
		log = new ArrayList<String>();
		possessionMapping = new HashMap<String, Ship>();
		propertyMapping = new HashMap<Ship, String>();
		name = "Empire Session";
	}
	
	public Game(String name, UniverseType universe) {
		this();
		this.universe = universe;
		this.name = name;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public UniverseType getUniverse() {
		return universe;
	}
	
	public void setUniverseType(UniverseType universe) {
		this.universe = universe;
	}
	
	public Sector[][] getSectorGrid() {
		return sectorGrid;
	}
	
	public Sector getSector(int x, int y) {
		return sectorGrid[x - 1][y - 1];
	}
	
	public int getStardate() { return stardate; }
	
	public void setStardate(int stardate) {
		this.stardate = stardate;
	}
	public String getName() {
		return name;
	}
	
	public ArrayList<Player> getActivePlayers() {
		return players;
	}
	
	public ArrayList<Ship> getShips() {
		return ships;
	}
	
	public ArrayList<Base> getBases() {
		return bases;
	}
	
	public ArrayList<Planet> getPlanets() {
		return planets;
	}
	
	public ArrayList<String> getLog() {
		return log;
	}

	public HashMap<String, Ship> getPossessionMapping() {
		return possessionMapping;
	}

	public HashMap<Ship, String> getPropertyMapping() {
		return propertyMapping;
	}
	
	public void map(String username, Ship ship) {
		possessionMapping.put(username, ship);
		propertyMapping.put(ship, username);
	}
	
	public void addPlayer(Player player) {
		if(!hasPlayed(player.getUserame())) {
			possessionMapping.put(player.getUserame(), player.getShip());
			propertyMapping.put(player.getShip(), player.getUserame());
			sectorGrid[0][0].getShips().add(player.getShip());
			ships.add(player.getShip());
		}
		players.add(player);
	}
	
	public void addShip(Ship ship) {
		int x = ship.getSector().x;
		int y = ship.getSector().y;
		sectorGrid[x - 1][y - 1].getShips().add(ship);
		ships.add(ship);
	}
	
	public void addPlanet(Planet planet) {
		int x = planet.getSector().x;
		int y = planet.getSector().y;
		sectorGrid[x - 1][y - 1].getPlanets().add(planet);
		planets.add(planet);
	}
	
	public void addBase(Base base) {
		int x = base.getSector().x;
		int y = base.getSector().y;
		sectorGrid[x - 1][y - 1].getBases().add(base);
		bases.add(base);
	}
	
	public Ship getIdShip(int id) {
		for (Ship s : ships) {
			if (s.getId() == id)
				return s;
		}
		return null;
	}
	
	public Ship getPlayerShip(String player) {
		return possessionMapping.get(player);
	}
	
	public String getOwner(Ship ship) {
		return propertyMapping.get(ship);
	}
	
	public void removePlayer(String username) {
		for(Player p : players) {
			if(p.getUserame().equals(username)) {
				players.remove(p);
				break;
			}
		}
	}
	
	public boolean hasPlayed(String username) {
		return possessionMapping.containsKey(username);
	}
	
	public void destroy(Ship ship) {
		ships.remove(ship);
		ship.getSector().getShips().remove(ship);
		String username = getOwner(ship);
		if(username == null) return;

		for(Player p : players)
			if(p.getUserame().equals(username))
				players.remove(p);
		possessionMapping.remove(username);
		propertyMapping.remove(ship);
	}
	
	public void nextStardate() {
		stardate++;
		
		// AI action
		aiAction();
		
		// Deplete the energy of anyone on red or yellow alert.
		for(Ship s : ships) s.consumeEnergy();
	}
	
	public void aiAction() {
		for(Ship s : ships) {
			if(propertyMapping.get(s) == null) {
			}
		}
	}
	
}
