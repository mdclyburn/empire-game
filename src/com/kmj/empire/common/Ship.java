package com.kmj.empire.common;

public class Ship {

	protected ShipType shipType;
	protected EmpireType empireType;
	protected int energy;
	protected AlertLevel alert;
	protected int shield;

	protected Location sectorLocation;
	protected Location universeLocation;
	
	public Ship() {
		shipType = null;
		empireType = null;
		energy = 2800;
		alert = AlertLevel.GREEN;
		shield = 100;
		
		sectorLocation = new Location();
		sectorLocation.x = 1;
		sectorLocation.y = 1;
		universeLocation = new Location();
		universeLocation.x = 1;
		universeLocation.y = 1;
	}
	
	public Ship(ShipType shipType, EmpireType empireType) {
		this();
		this.shipType = shipType;
		this.empireType = empireType;
	}
	
	public ShipType getType() { return shipType; }
	public EmpireType getEmpire() { return empireType; }
	public int getEnergy() { return energy; }
	public AlertLevel getAlertLevel() { return alert; }
	public int getShieldLevel() { return shield; }
	
	public Location getSectorLocation() { return sectorLocation; }
	public Location getUniverseLocation() { return universeLocation; }
}
