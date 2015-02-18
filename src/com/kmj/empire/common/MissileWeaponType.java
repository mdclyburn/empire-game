package com.kmj.empire.common;

public class MissileWeaponType extends WeaponType {

	public MissileWeaponType(String id, String name, int maxYield) {
		super(id, name, "MISSLE", maxYield);
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
