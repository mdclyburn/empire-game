package com.kmj.empire.common;

public class Ship {

	protected ShipType type;
	protected int energy;
	protected AlertLevel alert;
	protected int shield;

	protected Location sectorLocation;
	protected Location universeLocation;
	
	public Ship() {
		type = null;
		energy = 2800;
		alert = AlertLevel.GREEN;
		shield = 100;
		
		sectorLocation = new Location();
		universeLocation = new Location();
	}
	
	public Ship(ShipType type) {
		this();
		this.type = type;
	}
	
	public ShipType getType() { return type; }
	public int getEnergy() { return energy; }
	public AlertLevel getAlertLevel() { return alert; }
	public int getShieldLevel() { return shield; }
	
	public Location getSectorLocation() { return sectorLocation; }
	public Location getUniverseLocation() { return universeLocation; }
}
