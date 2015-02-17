package com.kmj.empire.common;

public class ShipType {

	private String name;
	private String id;
	private String shipClass;
	private EmpireType empire;
	private int maxEnergy, maxSpeed, maxShield, maxMissle;
	private WeaponType energyWeapon, missleWeapon;
	
	public ShipType() {
		name = "null";
	}
	
	public ShipType(String id, String name, String shipClass, EmpireType empire, int maxEnergy, int maxSpeed, int maxShield,
			int maxMissle, WeaponType energyWeapon, WeaponType missleWeapon) {
		this.id = id;
		this.name = name;
		this.shipClass = shipClass;
		this.empire = empire;
		this.maxEnergy = maxEnergy;
		this.maxSpeed = maxSpeed;
		this.maxShield = maxShield;
		this.maxMissle = maxMissle;
		this.energyWeapon = energyWeapon;
		this.missleWeapon = missleWeapon;
	}
	
	public String getId() {
		return id;
	}
	
	public String getName() { return name; }
	
	public String getShipClass() {
		return shipClass;
	}

	public EmpireType getEmpire() {
		return empire;
	}

	public int getMaxEnergy() {
		return maxEnergy;
	}

	public int getMaxSpeed() {
		return maxSpeed;
	}

	public int getMaxShield() {
		return maxShield;
	}

	public int getMaxMissle() {
		return maxMissle;
	}

	public WeaponType getEnergyWeapon() {
		return energyWeapon;
	}

	public WeaponType getMissleWeapon() {
		return missleWeapon;
	}

}
