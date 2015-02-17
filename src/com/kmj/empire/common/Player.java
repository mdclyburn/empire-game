package com.kmj.empire.common;

public class Player {
	
	private String username;
	private EmpireType empire;
	private Ship ship;
	
	public Player() {
		username = null;
	}
	
	public Player(String username, EmpireType empire, Ship ship) {
		this.username = username;
		this.empire = empire;
		this.ship = ship;
	}
	
	public String getUserame() { return username; }
	public Ship getShip() { return ship; }
	public EmpireType getEmpire() { return empire; }

}
