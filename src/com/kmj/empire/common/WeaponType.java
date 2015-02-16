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
		return type.equals("MISSLE");
	}
}
