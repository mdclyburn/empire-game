package com.kmj.empire.common;

public class Ship {

	protected ShipType type;
	protected int energy;
	protected AlertLevel alert;
	protected int shield;
	
	public Ship() {
		type = null;
		energy = 2800;
		alert = AlertLevel.GREEN;
		shield = 100;
	}
	
	public Ship(ShipType type) {
		this();
		this.type = type;
	}
	
	public ShipType getType() { return type; }
	public int getEnergy() { return energy; }
	public AlertLevel getAlertLevel() { return alert; }
	public int getShieldLevel() { return shield; }
}
