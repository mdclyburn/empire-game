package com.kmj.empire.common;

public class ShipType {

	protected String name;
	
	public ShipType() {
		name = "null";
	}
	
	public ShipType(String name) {
		this.name = name;
	}
	
	public String getName() { return name; }
}
