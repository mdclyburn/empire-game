package com.kmj.empire.common;

/*
 * Model object representing a ship in-game. This includes
 * both player-controlled and AI-controlled ships.
 */
public class Ship extends MapEntity {

	private int id;

	protected ShipType shipType;
	protected int energy, maxEnergy;
	protected int shield, maxShield;
	protected int missiles, maxMissile;
	protected int maxSpeed;
	protected AlertLevel alert;
	protected WeaponType energyWeapon, missileWeapon;
	
	public Ship(ShipType shipType, Game game, Sector sector, int x, int y) {
		super(game, sector, x, y);
		this.shipType = shipType;
		maxEnergy = energy = shipType.getMaxEnergy();
		maxShield = shield = shipType.getMaxShield();
		maxMissile = missiles = shipType.getMaxMissile();
		maxSpeed = shipType.getMaxSpeed();
		alert = AlertLevel.GREEN;
		energyWeapon = shipType.getEnergyWeapon();
		missileWeapon = shipType.getMissileWeapon();
	}
	
	public ShipType getType() { return shipType; }
	public int getEnergy() { return energy; }
	public AlertLevel getAlertLevel() { return alert; }
	public int getShieldLevel() { return shield; }
	
	// Deplete the energy of a ship depending on its alert level.
	public void consumeEnergy() {
		// Deplete energy by 2% if on yellow; 5% if on red
		if(alert == AlertLevel.YELLOW)
			energy -= (shipType.getMaxEnergy() / 50);
		else if(alert == AlertLevel.RED)
			energy -= (shipType.getMaxEnergy() / 20);
	}
	
	// Deplete the energy of a ship on impulse movement.
	public void consumeImpulseEnergy(int mvmt) {
		energy -= (10 * mvmt);
	}
	
	// Deplete the energy of a ship on warp movement.
	public void consumeWarpEnergy(int mvmt) {
		energy -= (100 * mvmt);
	}
	
	public int getId() {
		return id;
	}
	
	public int getMaxEnergy() {
		return maxEnergy;
	}

	public int getShield() {
		return shield;
	}

	public void setShield(int shield) {
		this.shield = shield;
	}

	public int getMaxShield() {
		return maxShield;
	}

	public int getMissles() {
		return missiles;
	}

	public void setMissles(int missles) {
		this.missiles = missles;
	}

	public int getMaxMissle() {
		return maxMissile;
	}

	public int getMaxSpeed() {
		return maxSpeed;
	}

	public AlertLevel getAlert() {
		return alert;
	}

	public void setAlert(AlertLevel alert) {
		this.alert = alert;
	}

	public WeaponType getEnergyWeapon() {
		return energyWeapon;
	}

	public WeaponType getMissleWeapon() {
		return missileWeapon;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setEnergy(int energy) {
		this.energy = energy;
	}
	
	public String toString() {
		String shipString = id + "\t" + shipType.getId() + "\t" + sector.getX() + "\t" + sector.getY() + "\t"
			+ x + "\t" + y + "\t" + energy + "\t" + missiles + "\t" + alert + "\t" + shield;
		return shipString;
	}
	
	public static Ship fromString(String line, Game game) {
		int id = Integer.valueOf(line.substring(0, line.indexOf('\t')));
		line = line.substring(line.indexOf('\t')+1);
		ShipType shipType = game.getUniverse().getShip(line.substring(0, line.indexOf('\t')));
		line = line.substring(line.indexOf('\t')+1);
		int sx = Integer.valueOf(line.substring(0, line.indexOf('\t')));
		line = line.substring(line.indexOf('\t')+1);
		int sy = Integer.valueOf(line.substring(0, line.indexOf('\t')));
		line = line.substring(line.indexOf('\t')+1);
		int px = Integer.valueOf(line.substring(0, line.indexOf('\t')));
		line = line.substring(line.indexOf('\t')+1);
		int py = Integer.valueOf(line.substring(0, line.indexOf('\t')));
		line = line.substring(line.indexOf('\t')+1);
		int energy = Integer.valueOf(line.substring(0, line.indexOf('\t')));
		line = line.substring(line.indexOf('\t')+1);
		int missiles = Integer.valueOf(line.substring(0, line.indexOf('\t')));
		line = line.substring(line.indexOf('\t')+1);
		AlertLevel alert = AlertLevel.GREEN;
		String alertString = line.substring(0, line.indexOf('\t'));
		if (alertString.equals("YELLOW")) alert = AlertLevel.YELLOW;
		if (alertString.equals("RED")) alert = AlertLevel.RED;
		line = line.substring(line.indexOf('\t')+1);
		int shield = Integer.valueOf(line.substring(0, line.length()));
		Ship ship = new Ship(shipType, game, game.getSector(sx, sy), px, py);
		ship.setId(id);
		ship.setEnergy(energy);
		ship.setMissles(missiles);
		ship.setAlert(alert);
		ship.setShield(shield);
		return ship;
	}
	
}
