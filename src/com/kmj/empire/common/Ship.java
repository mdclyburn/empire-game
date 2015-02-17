package com.kmj.empire.common;

public class Ship extends MapEntity {

	protected ShipType shipType;
	protected EmpireType empire;
	protected int energy, maxEnergy;
	protected int shield, maxShield;
	protected int missles, maxMissle;
	protected int maxSpeed;
	protected AlertLevel alert;
	protected WeaponType energyWeapon, missleWeapon;
	
	public Ship(ShipType shipType, Game game, Sector sector, int x, int y) {
		super(game, sector, x, y);
		this.shipType = shipType;
		empire = shipType.getEmpire();
		maxEnergy = energy = shipType.getMaxEnergy();
		maxShield = shield = shipType.getMaxShield();
		maxMissle = missles = shipType.getMaxMissle();
		maxSpeed = shipType.getMaxSpeed();
		alert = AlertLevel.GREEN;
		energyWeapon = shipType.getEnergyWeapon();
		missleWeapon = shipType.getMissleWeapon();
	}
	
	public ShipType getType() { return shipType; }
	public EmpireType getEmpire() { return empire; }
	public int getEnergy() { return energy; }
	public AlertLevel getAlertLevel() { return alert; }
	public int getShieldLevel() { return shield; }
	
	public void consumeImpulseEnergy(int mvmt) {
		energy -= (10 * mvmt);
	}
	
	public void consumeWarpEnergy(int mvmt) {
		energy -= (100 * mvmt);
	}
}
