package com.kmj.empire.common;

import java.util.ArrayList;

public class Sector {
	
	protected ArrayList<Planet> planets;
	protected ArrayList<Ship> ships;
	protected ArrayList<Base> bases;
	
	public Sector() {
		planets = new ArrayList<Planet>();
		ships = new ArrayList<Ship>();
		bases = new ArrayList<Base>();
	}
	
	public ArrayList<Planet> getPlanets() { return planets; }
	public ArrayList<Ship> getShips() { return ships; }
	public ArrayList<Base> getBases() { return bases; }

}
