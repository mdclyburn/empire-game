package com.kmj.empire.common;

import java.util.ArrayList;

/*
 * Model object representing one of 64 sectors. Can contain a number
 * of ships, planets and bases. Each sector has 64 sub-sectors.
 */
public class Sector {
	
	protected int x, y;
	protected ArrayList<Planet> planets;
	protected ArrayList<Ship> ships;
	protected ArrayList<Base> bases;
	
	public Sector() {
		planets = new ArrayList<Planet>();
		ships = new ArrayList<Ship>();
		bases = new ArrayList<Base>();
	}
	
	public Sector(int x, int y) {
		this();
		this.x = x;
		this.y = y;
	}
	
	public ArrayList<Planet> getPlanets() { return planets; }
	public ArrayList<Ship> getShips() { return ships; }
	public ArrayList<Base> getBases() { return bases; }
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
