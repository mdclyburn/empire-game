package com.kmj.empire.common;

public class MapEntity {
	
	private Game game;
	protected Sector sector;
	protected int x, y;
	
	public MapEntity() {
		sector = game.getSector(0, 1);
		x = y = 1;
	}

	public void setSector(int x, int y) {
		sector = game.sectorGrid[x-1][y-1];
	}
	
	public void setLocation(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public Sector getSector() {
		return sector;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public void setX(int x) {
		this.x = x;
	}
	
	public void setY(int y) {
		this.y = y;
	}
}
