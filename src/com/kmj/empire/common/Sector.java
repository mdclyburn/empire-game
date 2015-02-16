package com.kmj.empire.common;

public class Sector {
	
	protected int planets;
	protected int enemyEntities;
	protected int friendlyEntities;
	
	public Sector() {
		planets = enemyEntities = friendlyEntities = 0;
	}
	
	public void setPlanets(int planets) { this.planets = planets; }
	public void setEnemyEntities(int enemyEntites) { this.enemyEntities = enemyEntites; }
	public void setFriendlyEntities(int friendlyEntities) { this.friendlyEntities = friendlyEntities; }
	
	public int getPlanets() { return planets; }
	public int getEnemyEntities() { return enemyEntities; }
	public int getFriendlyEntities() { return friendlyEntities; }

}
