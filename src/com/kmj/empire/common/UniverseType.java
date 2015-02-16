package com.kmj.empire.common;

import java.util.ArrayList;

public class UniverseType {
	
	private String name;
	private ArrayList<EmpireType> empireTypes;
	
	public UniverseType(String name) {
		this.name = name;
		empireTypes = new ArrayList<EmpireType>();
	}
	
	public String getName() {
		return name;
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

}
