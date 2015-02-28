package com.kmj.empire.common;

/*
 * Model object representing a missile weapon type.
 */
public class MissileWeaponType extends WeaponType {

	public MissileWeaponType(String id, String name, int maxYield) {
		super(id, name, "MISSILE", maxYield);
	}
	
	@Override
	public boolean isEnergyWeapon() {
		return false;
	}
	
	@Override
	public boolean isMissleWeapon() {
		return true;
	}
	
}
