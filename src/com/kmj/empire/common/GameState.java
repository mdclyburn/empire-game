/* 
 * Class: GameState
 * 
 * This class holds information about the state of a game without recursive cycles
 * so it can be transferred to/from the server with Gson.
 */

package com.kmj.empire.common;

import java.util.ArrayList;
import java.util.HashMap;

/*
 * String representation of the Game model object used when
 * returning a Game back to a client to avoid cycling calls
 * with Gson.
 */
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
	private HashMap<String, Integer> possessionMapping;
	private HashMap<Integer, String> propertyMapping;
	
	
	private int stardate;
	private String universeName;
	
	public GameState(Game game) {
		//single layer, just copy
		id = game.getId();
		name = game.getName();
		stardate = game.getStardate();
		log = game.getLog();
		universeName = game.getUniverse().getName();
		possessionMapping = game.getPossessionMapping();
		propertyMapping = game.getPropertyMapping();
		
		//important
		empires = new ArrayList<String>();
		shipTypes = new ArrayList<String>();
		weapons = new ArrayList<String>();
		players = new ArrayList<String>();
		ships = new ArrayList<String>();
		bases = new ArrayList<String>();
		planets = new ArrayList<String>();
		
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
	}
	
	public Game toGame(Game game) {
		//empty game
		System.out.println("Updating game state");
		
		UniverseType universe = new UniverseType(universeName);
		for (String s : empires) universe.getEmpireList().add(EmpireType.fromString(s));
		for (String s : weapons) universe.getWeaponTypes().add(WeaponType.fromString(s));
		for (String s : shipTypes) {
			ShipType shipType = ShipType.fromString(s, universe);
			universe.getEmpire(shipType.getEmpire().getId()).getShipTypes().add(shipType);
		}
		if (game == null) game = new Game(name, universe);
		game.empty();
		game.setName(name);
		game.setUniverseType(universe);
		game.setId(id);
		game.setStardate(stardate);
		game.setPossessionMapping(possessionMapping);
		game.setPropertyMapping(propertyMapping);
		for (String s : ships) game.addShip(Ship.fromString(s, game));
		for (String s : bases) game.addBase(Base.fromString(s, game));
		for (String s : planets) game.addPlanet(Planet.fromString(s, game));
		for (String s : players) game.addPlayer(Player.fromString(s, game));
		for (String s : log) game.getLog().add(s);
		return game;
	}

}
