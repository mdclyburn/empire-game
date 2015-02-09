package com.kmj.empire.server;

public class Game {
	
	private Sector[][] sectorGrid;
	
	public Game() {
		sectorGrid = new Sector[8][8];
	}
	
	public Sector[][] getSectorGrid() {
		return sectorGrid;
	}
	
	public Sector getSector(int x, int y) {
		return sectorGrid[x][y];
	}

}
