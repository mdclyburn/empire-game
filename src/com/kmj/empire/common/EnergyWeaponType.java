package com.kmj.empire.common;

public class EnergyWeaponType extends WeaponType {

	public EnergyWeaponType(String id, String name, int maxYield) {
		super(id, name, "ENERGY", maxYield);
	}
	
	@Override
	public boolean isEnergyWeapon() {
		return true;
	}
	
	@Override
	public boolean isMissleWeapon() {
		return false;
	}

}
