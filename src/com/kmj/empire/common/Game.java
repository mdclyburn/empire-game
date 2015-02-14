package com.kmj.empire.common;

public class Game {
	
	protected String name;
	protected int activePlayers;

	public Game() {
		name = "Empire Session";
		activePlayers = 0;
	}
	
	public Game(String name) {
		this.name = name;
	}
	
	public String getName() { return name; }
	public int getActivePlayers() { return activePlayers; }

}
