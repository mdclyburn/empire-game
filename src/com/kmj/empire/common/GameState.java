/* 
 * Class: GameState
 * 
 * This class holds information about the state of a game without recursive cycles
 * so it can be transferred to/from the server with Gson.
 */

package com.kmj.empire.common;

import java.util.ArrayList;
import java.util.HashMap;

public class GameState {

	private int id;
	private String name;
	private ArrayList<String> empires;
	private ArrayList<String> shipTypes;
	private ArrayList<String> weapons;
	private ArrayList<String> players;
	private ArrayList<String> ships;
	private ArrayList<String> bases;
	private ArrayList<String> planets;
	private ArrayList<String> log;
	private HashMap<String, String> possessionMapping;
	private HashMap<String, String> propertyMapping;
	
	
	private int stardate;
	private String universeName;
	
	public GameState(Game game) {
		//single layer, just copy
		id = game.getId();
		name = game.getName();
		stardate = game.getStardate();
		log = game.getLog();
		universeName = game.getUniverse().getName();
		
		//important
		empires = new ArrayList<String>();
		shipTypes = new ArrayList<String>();
		weapons = new ArrayList<String>();
		players = new ArrayList<String>();
		ships = new ArrayList<String>();
		bases = new ArrayList<String>();
		planets = new ArrayList<String>();
		possessionMapping = new HashMap<String, String>();
		propertyMapping = new HashMap<String, String>();
		
		for (EmpireType e: game.getUniverse().getEmpireList())
			empires.add(e.toString());
		for (EmpireType e: game.getUniverse().getEmpireList())
			for (ShipType s: e.getShipTypes()) 
				shipTypes.add(s.toString());
		for (WeaponType w: game.getUniverse().getWeaponTypes())
			weapons.add(w.toString());
		for (Ship s: game.getShips())
			ships.add(s.toString());
		for (Base b: game.getBases())
			bases.add(b.toString());
		for (Player p: game.getActivePlayers())
			players.add(p.toString());
		for (Planet p: game.getPlanets())
			planets.add(p.toString());
		for (String s: game.getPossessionMapping().keySet()) {
			possessionMapping.put(s, game.getPossessionMapping().get(s).toString());
		}
		for (Ship s: game.getPropertyMapping().keySet()) {
			propertyMapping.put(s.toString(), game.getPropertyMapping().get(s));
		}
		
	}
	
	public Game toGame() {
		UniverseType universe = new UniverseType(universeName);
		for (String s : empires) universe.getEmpireList().add(EmpireType.fromString(s));
		for (String s : weapons) universe.getWeaponTypes().add(WeaponType.fromString(s));
		for (String s : shipTypes) {
			ShipType shipType = ShipType.fromString(s, universe);
			universe.getEmpire(shipType.getEmpire().getId()).getShipTypes().add(shipType);
		}
		Game game = new Game(name, universe);
		game.setId(id);
		game.setStardate(stardate);
		for (String s : ships) game.addShip(Ship.fromString(s, game));
		for (String s : possessionMapping.keySet())
			game.getPossessionMapping().put(s, Ship.fromString(possessionMapping.get(s), game));
		for (String s : propertyMapping.keySet())
			game.getPropertyMapping().put(Ship.fromString(s, game), propertyMapping.get(s));
		for (String s : bases) game.addBase(Base.fromString(s, game));
		for (String s : planets) game.addPlanet(Planet.fromString(s, game));
		for (String s : players) game.addPlayer(Player.fromString(s, game));
		for (String s : log) game.getLog().add(s);
		return game;
	}

}
