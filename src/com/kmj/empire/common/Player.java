package com.kmj.empire.common;

/*
 * Model object representing a single player. Contains everything
 * that the player is linked to.
 */
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
	
	public String getUsername() { return username; }
	public Ship getShip() { return ship; }
	public EmpireType getEmpire() { return empire; }
	
	public String toString() {
		String playerString = username + "\t" + empire.getId() + "\t" + ship.getId();
		return playerString;
	}
	
	public static Player fromString(String playerString, Game game) {
		String playerid = playerString.substring(0, playerString.indexOf('\t'));
		playerString = playerString.substring(playerString.indexOf('\t')+1);
		EmpireType empire = game.getUniverse().getEmpire(playerString.substring(0, playerString.indexOf('\t')));
		playerString = playerString.substring(playerString.indexOf('\t')+1);
		int shipId = Integer.valueOf(playerString.substring(0, playerString.length()));
		Player player = new Player(playerid, empire, game.getIdShip(shipId));
		return player;
	}
}
