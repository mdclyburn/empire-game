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
	private ArrayList<Player> players;
	private ArrayList<Ship> ships;
	private ArrayList<Base> bases;
	private ArrayList<String> log;
	private HashMap<String, Ship> possessionMapping;
	private HashMap<Ship, String> propertyMapping;
	
	
	private int stardate;
	private Sector[][] sectorGrid;
	private UniverseType universe;
	
	public GameState(Game game) {
		//single layer, just copy
		id = game.getId();
		name = game.getName();
		stardate = game.getStardate();
		
		//multiple layers but not a big deal
		bases = game.getBases();
		log = game.getLog();
		
		//important
	}

}
