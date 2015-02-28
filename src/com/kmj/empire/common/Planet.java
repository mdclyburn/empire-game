package com.kmj.empire.common;

/*
 * Model object representing a planet that a player can
 * visit in-game.
 */
public class Planet extends MapEntity {
	
	public Planet() {}
	
	public Planet(Game game, Sector sector, int x, int y) {
		super(game, sector, x, y);
	}
	
	public String toString() {
		return sector.getX() + "\t" + sector.getY() + "\t" + x + "\t" + y;
	}
	
	public static Planet fromString(String line, Game game) {
		int sx = Integer.valueOf(line.substring(0, line.indexOf('\t')));
		line = line.substring(line.indexOf('\t')+1);
		int sy = Integer.valueOf(line.substring(0, line.indexOf('\t')));
		line = line.substring(line.indexOf('\t')+1);
		int px = Integer.valueOf(line.substring(0, line.indexOf('\t')));
		line = line.substring(line.indexOf('\t')+1);
		int py = Integer.valueOf(line.substring(0, line.length()));
		Planet planet = new Planet(game, game.getSector(sx, sy), px, py);
		return planet;
	}

}
