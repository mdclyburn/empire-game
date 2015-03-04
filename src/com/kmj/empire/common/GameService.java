package com.kmj.empire.common;

import java.util.ArrayList;

import com.kmj.empire.common.exceptions.ActionException;
import com.kmj.empire.common.exceptions.AuthenticationFailedException;
import com.kmj.empire.common.exceptions.BadDestinationException;
import com.kmj.empire.common.exceptions.ConnectionFailedException;
import com.kmj.empire.common.exceptions.InvalidGameFileException;

/*
 * Common API shared to facilitate communication between the
 * client and the server.
 */
public interface GameService {
	
	public static final int RESTORE_GAME = 1;
	public static final int GET_GAME_STATE = 2;
	public static final int GET_GAMES_LIST = 3;
	public static final int AUTHENTICATE = 4;
	public static final int CREATE_GAME = 5;
	public static final int JOIN_GAME = 6;
	public static final int DISCONNECT = 7;
	public static final int NAVIGATE = 8;
	public static final int SET_ALERT_LEVEL = 9;
	public static final int WARP = 10;
	public static final int FIRE_TORPEDO = 11;
	
	/**
	 * Restores a game to the server and returns the id.
	 * <p>
	 * Accepts a string argument called gameData, and parses it for
	 * data of an old game. It then adds the game to the servers
	 * game list and returns the id that was assigned to it.
	 * 
	 * @param 	gameData	the string containing the game data to be restored
	 * @return				the id of the restored game
	 */
	public int restoreGame(String gameData) throws InvalidGameFileException, ConnectionFailedException;
	
	/**
	 * Gets a GameState object from the server of the desired game.
	 * <p>
	 * Accepts a integer argument gameId and retrieves that game from the server's
	 * game list.
	 * 
	 * @param 	gameId	the id of the desired game
	 * @return			the GameState object of the specified game
	 */
	public GameState getGameState(int sessionId) throws ConnectionFailedException;
	
	/**
	 * Returns a list of active games.
	 * <p>
	 * Accepts the session id of the user and returns the ArrayList of
	 * games belonging to the server.
	 * 
	 *  @param	sessionId	users sessionId
	 *  @return	gamesList	ArrayList of active games on the server
	 */
	public ArrayList<Game> getGamesList(int sessionId) throws AuthenticationFailedException, ConnectionFailedException;
	
	/**
	 *  Authenticates a user.
	 *  <p>
	 *  Accepts username and password stings and determines if they are associated with
	 *  a valid user. If so it returns 0, indicating success. Otherwise it returns -1.
	 *  
	 *  @param	user		username of the caller
	 *  @param	password	password of the caller
	 *  @return				success
	 */
	public int authenticate(String user, String password) throws AuthenticationFailedException, ConnectionFailedException;
	
	/* Creates a game on this server */
	public int createGame() throws ConnectionFailedException;
	
	/**
	 * Places the user in the desired game.
	 * <p>
	 * Accepts sessionId to identify user and the id of the game to be
	 * joined. It determines if the user is rejoining or not and prompts them
	 * for necessary information if they are joining for the first time.
	 * 
	 * @param	sessionId	users is
	 * @param	id			id of the game to join
	 * @return 	void
	 */
	public void joinGame(int sessionId, int id) throws ConnectionFailedException;
	
	/**
	 *  Removes the player from their current game.
	 *  <p>
	 *  Accepts a sessionId and removes the associated user from the
	 *  game to which they are currently connected.
	 *  
	 *  @param	sessionID	the users sessionId
	 *  @return	void 
	 */
	public void disconnect(int sessionId) throws ConnectionFailedException;
	
	/**
	 *  Allows a user to navigate their ship within current sector.
	 *  <p>
	 *  Accepts a sessionId that identifies the user and thus ship that is being
	 *  requested to move. It also accepts the x and y coordinates of the desired
	 *  destination. With this it determines whether or not the destination can be
	 *  reached and throws appropriate exceptions if it cannot be. Otherwise it
	 *  returns nothing.
	 *  
	 *  @param	sessionId	users sessionId
	 *  @param	x			x cord of desired navigate destination
	 *  @param	y			y cord of desired navigate destination
	 *  @return	void
	 */
	public void navigate(int sessionId, int x, int y) throws ActionException, BadDestinationException, ConnectionFailedException;
	
	/** 
	 * Sets a ships alert level.
	 * <p>
	 * Accepts a sessionId to identify a ship and an AlertLevel to
	 * set it to. This is printed to the attack log on the server.
	 * 
	 * @param	sessionId	users sessionId
	 * @param	level		desired AlertLevel
	 * @return	void
	 */
	public void setAlertLevel(int sessionId, AlertLevel level) throws ActionException, ConnectionFailedException;
	
	/**
	 *  Warps a ship to a nearby sector.
	 *  <p>
	 *  Accepts the sessionId to identify the ship and the desired warp destination
	 *  sector. It determines if the sector can be reached and if not throws
	 *  appropriate exceptions. It returns nothing if successful.
	 *  
	 *  @param	sessionId	users sessionId
	 *  @param	sector		the sector attempting to warp to
	 */
	public void warp(int sessionId, Sector sector) throws ActionException, BadDestinationException, ConnectionFailedException;
	
	/**
	 * Fires a torpedo from a ship.
	 * <p>
	 * Accepts a sessionId, sector, x coordinate, and y coordinate. It 
	 * determines if the desired firing location is valid for the ship
	 * identified by sessionId, and fires a torpedo. This is recorded in
	 * the servers attack log.
	 * 
	 * @param	sessionId	users id
	 * @param	sector		the sector that the ship is currently in
	 * @param	x			x coordinate of desired firing target
	 * @param	y			y coordinate of desired firing target
	 * @return	void
	 */
	public void fireTorpedo(int sessionId, Sector sector, int x, int y) throws ActionException, ConnectionFailedException;

}
