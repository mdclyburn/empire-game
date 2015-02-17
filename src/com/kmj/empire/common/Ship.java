package com.kmj.empire.common;

public class Ship extends MapEntity {

	private int id;

	protected ShipType shipType;
	protected int energy, maxEnergy;
	protected int shield, maxShield;
	protected int missles, maxMissle;
	protected int maxSpeed;
	protected AlertLevel alert;
	protected WeaponType energyWeapon, missleWeapon;
	
	public Ship(ShipType shipType, Game game, Sector sector, int x, int y) {
		super(game, sector, x, y);
		this.shipType = shipType;
		maxEnergy = energy = shipType.getMaxEnergy();
		maxShield = shield = shipType.getMaxShield();
		maxMissle = missles = shipType.getMaxMissle();
		maxSpeed = shipType.getMaxSpeed();
		alert = AlertLevel.GREEN;
		energyWeapon = shipType.getEnergyWeapon();
		missleWeapon = shipType.getMissleWeapon();
	}
	
	public ShipType getType() { return shipType; }
	public int getEnergy() { return energy; }
	public AlertLevel getAlertLevel() { return alert; }
	public int getShieldLevel() { return shield; }
	
	public void consumeImpulseEnergy(int mvmt) {
		energy -= (10 * mvmt);
	}
	
	public void consumeWarpEnergy(int mvmt) {
		energy -= (100 * mvmt);
	}
	
	public int getId() {
		return id;
	}
	
	public int getMaxEnergy() {
		return maxEnergy;
	}

	public int getShield() {
		return shield;
	}

	public void setShield(int shield) {
		this.shield = shield;
	}

	public int getMaxShield() {
		return maxShield;
	}

	public int getMissles() {
		return missles;
	}

	public void setMissles(int missles) {
		this.missles = missles;
	}

	public int getMaxMissle() {
		return maxMissle;
	}

	public int getMaxSpeed() {
		return maxSpeed;
	}

	public AlertLevel getAlert() {
		return alert;
	}

	public void setAlert(AlertLevel alert) {
		this.alert = alert;
	}

	public WeaponType getEnergyWeapon() {
		return energyWeapon;
	}

	public WeaponType getMissleWeapon() {
		return missleWeapon;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setEnergy(int energy) {
		this.energy = energy;
	}
}
