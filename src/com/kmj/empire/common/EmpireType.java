package com.kmj.empire.common;

import java.util.ArrayList;

public class EmpireType {
	
	private String id;
	private String name;
	private String missionType;
	private ArrayList<ShipType> shipTypes;
	
	public EmpireType() {
		this.missionType = "Exploration";
		shipTypes = new ArrayList<ShipType>();
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
	
	public String toString() {
		String empireString = id + "\t" + name + "\t" + missionType;
		return empireString; 
	}
	
	public static EmpireType fromString(String empireString) {
		String id = empireString.substring(0, empireString.indexOf('\t'));
		empireString = empireString.substring(empireString.indexOf('\t')+1);
		String name = empireString.substring(0, empireString.indexOf('\t'));
		empireString = empireString.substring(empireString.indexOf('\t')+1);
		String mission = empireString.substring(0, empireString.length());
		EmpireType empire = new EmpireType(id, name, mission);
		return empire;
	}
}
