package com.kmj.empire.common;

import java.util.ArrayList;

public class Sector {
	
	protected ArrayList<Planet> planets;
	protected ArrayList<Ship> ships;
	
	public Sector() {
		planets = new ArrayList<Planet>();
		ships = new ArrayList<Ship>();
	}
	
	public ArrayList<Planet> getPlanets() { return planets; }
	public ArrayList<Ship> getShips() { return ships; }

}
