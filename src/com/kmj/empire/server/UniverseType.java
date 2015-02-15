package com.kmj.empire.server;

import java.util.ArrayList;

public class UniverseType {
	
	private ArrayList<EmpireType> empireTypes;
	
	public UniverseType() {
		empireTypes = new ArrayList<EmpireType>();
	}
	
	public ArrayList<EmpireType> getEmpireList() {
		return empireTypes;
	}

}
