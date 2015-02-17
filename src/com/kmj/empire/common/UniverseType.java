package com.kmj.empire.common;

import java.util.ArrayList;

public class UniverseType {
	
	private String name;
	private ArrayList<EmpireType> empireTypes;
	private ArrayList<WeaponType> weaponTypes;
	
	public UniverseType(String name) {
		this.name = name;
		empireTypes = new ArrayList<EmpireType>();
		weaponTypes = new ArrayList<WeaponType>();
	}
	
	public String getName() {
		return name;
	}
	
	public ArrayList<WeaponType> getWeaponTypes() {
		return weaponTypes;
	}
	
	public ArrayList<EmpireType> getEmpireList() {
		return empireTypes;
	}
	
	public EmpireType getEmpire(String name) {
		for (EmpireType e: empireTypes) {
			if (e.getId().equals(name) || e.getName().equals(name))
				return e;
		}
		return null;
	}

	public WeaponType getWeapon(String name) {
		for (WeaponType w: weaponTypes) {
			if (w.getId().equals(name) || w.getName().equals(name))
				return w;
		}
		return null;
	}
	
	public ShipType getShip(String name) {
		for (EmpireType e: empireTypes) {
			for (ShipType s: e.getShipTypes()) {
				if (s.getId().equals(name) || s.getName().equals(name))
					return s;
			}
		}
		return null;
	}

}
