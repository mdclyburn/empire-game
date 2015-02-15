package com.kmj.empire.server;

public class Game {
	
	private Sector[][] sectorGrid;
	private UniverseType universeType;
	
	public Game() {
		this.sectorGrid = new Sector[8][8];
	}
	
	public Game(UniverseType universeType) {
		this();
		this.universeType = universeType;
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

}
