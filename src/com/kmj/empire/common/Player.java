package com.kmj.empire.common;

public class Player {
	
	protected String username;
	
	public Player() {
		username = null;
	}
	
	public Player(String username) {
		this.username = username;
	}
	
	public String getUserame() { return username; }

}
