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
	private HashMap<String, Integer> possessionMapping;
	private HashMap<Integer, String> propertyMapping;
	
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
		possessionMapping = new HashMap<String, Integer>();
		propertyMapping = new HashMap<Integer, String>();
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
	
	public int getStardate() { 
		return stardate; 
	}
	
	public void setStardate(int stardate) {
		this.stardate = stardate;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
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

	public HashMap<String, Integer> getPossessionMapping() {
		return possessionMapping;
	}

	public HashMap<Integer, String> getPropertyMapping() {
		return propertyMapping;
	}
	
	public void setPossessionMapping(HashMap<String, Integer> possessionMapping) {
		this.possessionMapping = possessionMapping;
	}

	public void setPropertyMapping(HashMap<Integer, String> propertyMapping) {
		this.propertyMapping = propertyMapping;
	}
	
	public void map(String username, Ship ship) {
		possessionMapping.put(username, ship.getId());
		propertyMapping.put(ship.getId(), username);
	}
	
	public void addPlayer(Player player) {
		
		if(!hasPlayed(player.getUserame())) {
			//assign the players ship a new id
			int id = 0;
			while (getIdShip(id) != null) id++;
			player.getShip().setId(id);
			
			//add player ship relationship to mapping
			possessionMapping.put(player.getUserame(), player.getShip().getId());
			propertyMapping.put(player.getShip().getId(), player.getUserame());
			sectorGrid[player.getShip().getSector().getX() - 1][player.getShip().getSector().getY() - 1].getShips().add(player.getShip());
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
		return getIdShip(possessionMapping.get(player));
	}
	
	public String getOwner(Ship ship) {
		return propertyMapping.get(ship.getId());
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

		for(int i = 0; i < players.size(); i++) {
			Player p = players.get(i);
			if(p.getUserame().equals(username))
				players.remove(p);
		}
		possessionMapping.remove(username);
		propertyMapping.remove(ship.getId());
	}
	
	public void nextStardate() {
		stardate++;
		
		// Deplete the energy of anyone on red or yellow alert.
		for(Ship s : ships) s.consumeEnergy();
	}
	
	public void empty() {
		players.removeAll(players);
		ships.removeAll(ships);
		bases.removeAll(bases);
		planets.removeAll(planets);
		log.removeAll(log);
		possessionMapping = new HashMap<String, Integer>();
		propertyMapping = new HashMap<Integer, String>();
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				sectorGrid[i][j].bases.removeAll(sectorGrid[i][j].bases);
				sectorGrid[i][j].ships.removeAll(sectorGrid[i][j].ships);
				sectorGrid[i][j].planets.removeAll(sectorGrid[i][j].planets);
			}
		}
	}
	
}
