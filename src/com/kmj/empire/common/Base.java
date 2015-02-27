package com.kmj.empire.common;

/*
 * Model object representing the base the player is
 * currently at.
 */
public class Base extends MapEntity {
	
	protected EmpireType empireType;
	private int id;
	
	public Base() {
		super();
	}
	
	public Base(EmpireType empireType, Game game, Sector sector, int x, int y) {
		super(game, sector, x, y);
		this.empireType = empireType;
	}

	public EmpireType getEmpire() { return empireType; }
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	

	public String toString() {
		String baseString = id + "\t" + empireType.getId() + "\t" + sector.getX() + "\t" + sector.getY() + "\t"
			+ x + "\t" + y;
		return baseString;
	}
	
	// Updates the object's variables from a string.
	public static Base fromString(String line, Game game) {
		int id = Integer.valueOf(line.substring(0, line.indexOf('\t')));
		line = line.substring(line.indexOf('\t')+1);
		EmpireType empire = game.getUniverse().getEmpire(line.substring(0, line.indexOf('\t')));
		line = line.substring(line.indexOf('\t')+1);
		int sx = Integer.valueOf(line.substring(0, line.indexOf('\t')));
		line = line.substring(line.indexOf('\t')+1);
		int sy = Integer.valueOf(line.substring(0, line.indexOf('\t')));
		line = line.substring(line.indexOf('\t')+1);
		int px = Integer.valueOf(line.substring(0, line.indexOf('\t')));
		line = line.substring(line.indexOf('\t')+1);
		int py = Integer.valueOf(line.substring(0, line.length()));
		Base base = new Base(empire, game, game.getSector(sx, sy), px, py);
		base.setId(id);
		return base;
	}
	
}
