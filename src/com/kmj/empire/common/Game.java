package com.kmj.empire.common;

import java.util.ArrayList;

public class Game {

	protected String name;
	protected ArrayList<Player> players;
	
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
		players = new ArrayList<Player>();
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
	
	public String getName() { return name; }
	public ArrayList<Player> getActivePlayers() { return players; }

}
