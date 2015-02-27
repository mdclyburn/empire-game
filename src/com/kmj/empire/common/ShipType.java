package com.kmj.empire.common;

/*
 * Model object representing a type of ship. This describes all
 * of the specifications for a given ship.
 */
public class ShipType {

	private String name;
	private String id;
	private String shipClass;
	private EmpireType empire;
	private int maxEnergy, maxSpeed, maxShield, maxMissile;
	private WeaponType energyWeapon, missileWeapon;
	
	public ShipType() {
		name = "null";
	}
	
	public ShipType(String id, String name, String shipClass, EmpireType empire, int maxEnergy, int maxSpeed, int maxShield,
			int maxMissile, WeaponType energyWeapon, WeaponType missileWeapon) {
		this.id = id;
		this.name = name;
		this.shipClass = shipClass;
		this.empire = empire;
		this.maxEnergy = maxEnergy;
		this.maxSpeed = maxSpeed;
		this.maxShield = maxShield;
		this.maxMissile = maxMissile;
		this.energyWeapon = energyWeapon;
		this.missileWeapon = missileWeapon;
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

	public int getMaxMissile() {
		return maxMissile;
	}

	public WeaponType getEnergyWeapon() {
		return energyWeapon;
	}

	public WeaponType getMissileWeapon() {
		return missileWeapon;
	}
	
	public String toString() {
		String shipString = id + "\t" + name + "\t" + shipClass + "\t" + empire.getId() + "\t"
			+ maxEnergy + "\t" + maxSpeed + "\t" + maxShield + "\t" + energyWeapon.getId()
			+ "\t" + missileWeapon.getId() + "\t" + maxMissile;
		return shipString;
	}
	
	public static ShipType fromString(String shipString, UniverseType universe) {
		String id = shipString.substring(0, shipString.indexOf('\t'));
		shipString = shipString.substring(shipString.indexOf('\t')+1);
		String name = shipString.substring(0, shipString.indexOf('\t'));
		shipString = shipString.substring(shipString.indexOf('\t')+1);
		String shipClass = shipString.substring(0, shipString.indexOf('\t'));
		shipString = shipString.substring(shipString.indexOf('\t')+1);
		String empire = shipString.substring(0, shipString.indexOf('\t'));
		shipString = shipString.substring(shipString.indexOf('\t')+1);
		int maxEnergy = Integer.valueOf(shipString.substring(0, shipString.indexOf('\t')));
		shipString = shipString.substring(shipString.indexOf('\t')+1);
		int maxSpeed = Integer.valueOf(shipString.substring(0, shipString.indexOf('\t')));
		shipString = shipString.substring(shipString.indexOf('\t')+1);
		int maxShield = Integer.valueOf(shipString.substring(0, shipString.indexOf('\t')));
		shipString = shipString.substring(shipString.indexOf('\t')+1);
		WeaponType energyWeapon = universe.getWeapon(shipString.substring(0, shipString.indexOf('\t')));
		shipString = shipString.substring(shipString.indexOf('\t')+1);
		WeaponType missileWeapon = universe.getWeapon(shipString.substring(0, shipString.indexOf('\t')));
		shipString = shipString.substring(shipString.indexOf('\t')+1);
		int maxMissile = Integer.valueOf(shipString.substring(0, shipString.length()));
		ShipType ship = new ShipType(id, name, shipClass, universe.getEmpire(empire), maxEnergy, maxSpeed,
			maxShield, maxMissile, energyWeapon, missileWeapon);
		return ship;
	}

}
