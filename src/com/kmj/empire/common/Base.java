package com.kmj.empire.common;

public class Base extends MapEntity {
	
	protected EmpireType empireType;
	
	public Base() {
		super();
	}
	
	public Base(EmpireType empireType) {
		this.empireType = empireType;
	}

	public EmpireType getEmpire() { return empireType; }
}
