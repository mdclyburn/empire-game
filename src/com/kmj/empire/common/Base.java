package com.kmj.empire.common;

public class Base extends MapEntity {
	
	protected EmpireType empireType;
	
	public Base() {
		super();
	}
	
	public Base(EmpireType empireType, Game game, Sector sector, int x, int y) {
		super(game, sector, x, y);
		this.empireType = empireType;
	}

	public EmpireType getEmpire() { return empireType; }
}
