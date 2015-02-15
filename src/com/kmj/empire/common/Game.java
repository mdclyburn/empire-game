package com.kmj.empire.server;

public class Game {

	protected String name;
	protected int activePlayers;
	
	private Sector[][] sectorGrid;
	private UniverseType universeType;
	
	public Game() {
		this.sectorGrid = new Sector[8][8];
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
	public int getActivePlayers() { return activePlayers; }

}
