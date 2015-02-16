package com.kmj.empire.common;

public class Ship extends MapEntity {

	protected ShipType shipType;
	protected EmpireType empireType;
	protected int energy;
	protected AlertLevel alert;
	protected int shield;
	
	public Ship() {
		super();
		shipType = null;
		empireType = null;
		energy = 2800;
		alert = AlertLevel.GREEN;
		shield = 100;
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
}
