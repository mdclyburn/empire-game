package com.kmj.empire.common;

public class MapEntity {
	
	private Game game;
	protected Sector sector;
	protected int x, y;
	
	public MapEntity() {
		x = y = 1;
	}
	
	public MapEntity(Game game, Sector sector, int x, int y) {
		this.game = game;
		this.sector = sector;
		this.x = x;
		this.y = y;
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
