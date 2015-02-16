package com.kmj.empire.common;

import java.util.ArrayList;

public class EmpireType {
	
	private String id;
	private String name;
	private String missionType;
	private ArrayList<ShipType> shipTypes;
	private ArrayList<WeaponType> weaponTypes;
	
	public EmpireType() {
		this.missionType = "Exploration";
		shipTypes = new ArrayList<ShipType>();
		weaponTypes = new ArrayList<WeaponType>();
	}
	
	public EmpireType(String id, String name, String mission) {
		this();
		this.id = id;
		this.name = name;
		this.missionType = mission;
	}
	
	public String getId() { return id; }
	
	public String getName() { return name; }

	public ArrayList<ShipType> getShipTypes() {
		return shipTypes;
	}
	
	public ArrayList<WeaponType> getWeaponTypes() {
		return weaponTypes;
	}
	
	public boolean isExploration() {
		return missionType.equals("Exploration");
	}

	public boolean isAggression() {
		return missionType.equals("Aggression");
	}
	
	public ShipType getShip(String name) {
		for (ShipType s: shipTypes) {
			if (s.getId().equals(name) || s.getName().equals(name))
				return s;
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
}
