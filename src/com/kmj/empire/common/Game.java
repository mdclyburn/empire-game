package com.kmj.empire.common;

public class Game {

	protected String name;
	protected int activePlayers;
	
	private Sector[][] sectorGrid;
	private UniverseType universeType;
	
	public Game() {
		this.sectorGrid = new Sector[8][8];
		for(int y = 0; y < 8; y++) {
			for(int x = 0; x < 8; x++) {
				sectorGrid[x][y] = new Sector();
			}
		}
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
