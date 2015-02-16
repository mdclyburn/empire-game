package com.kmj.empire.common;

public class MapEntity {
	
	protected Location sectorLocation;
	protected Location universeLocation;
	
	public MapEntity() {
		sectorLocation = new Location();
		universeLocation = new Location();
		
		sectorLocation.x =
				sectorLocation.y =
				universeLocation.x =
				universeLocation.y = 1;
	}
	
	public Location getSectorLocation() { return sectorLocation; }
	public Location getUniverseLocation() { return universeLocation; }

	public void setSectorLocation(int x, int y) {
		sectorLocation.x = x;
		sectorLocation.y = y;
	}
	
	public void setUniverseLocation(int x, int y) {
		universeLocation.x = x;
		universeLocation.y = y;
	}
}
