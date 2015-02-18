package com.kmj.empire.common;

public class WeaponType {
	
	protected String id;
	protected String name;
	protected String type;
	protected int maxYield;
	
	public WeaponType(String id, String name, String type, int maxYield) {
		this.id = id;
		this.name = name;
		this.type = type;
		this.maxYield = maxYield;
	}

	public String getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public String getType() {
		return type;
	}
	
	public int getMaxYield() {
		return maxYield;
	}
	
	public boolean isEnergyWeapon() {
		return type.equals("ENERGY");
	}

	public boolean isMissleWeapon() {
		return type.equals("MISSILE");
	}
	
	public String toString() {
		String weaponString = id + "\t" + name + "\t" + type + "\t" + maxYield;
		return weaponString;
	}
	
	public static WeaponType fromString(String weaponString) {
		String id = weaponString.substring(0, weaponString.indexOf('\t'));
		weaponString = weaponString.substring(weaponString.indexOf('\t')+1);
		String name = weaponString.substring(0, weaponString.indexOf('\t'));
		weaponString = weaponString.substring(weaponString.indexOf('\t')+1);
		String type = weaponString.substring(0, weaponString.indexOf('\t'));
		weaponString = weaponString.substring(weaponString.indexOf('\t')+1);
		int maxYield = Integer.valueOf(weaponString.substring(0, weaponString.length()));
		WeaponType weapon; 
		if (type.equals("MISSILE")) weapon = new MissileWeaponType(id, name, maxYield);
		else weapon = new EnergyWeaponType(id, name, maxYield);
		return weapon;
	}
}
