package com.kmj.empire.client.controller;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.kmj.empire.client.ui.NewPlayerDialog;
import com.kmj.empire.common.AlertLevel;
import com.kmj.empire.common.Game;
import com.kmj.empire.common.GameService;
import com.kmj.empire.common.GameState;
import com.kmj.empire.common.Sector;
import com.kmj.empire.common.exceptions.ActionException;
import com.kmj.empire.common.exceptions.AuthenticationFailedException;
import com.kmj.empire.common.exceptions.BadDestinationException;
import com.kmj.empire.common.exceptions.ConnectionFailedException;
import com.kmj.empire.common.exceptions.InvalidGameFileException;

/**
 * The object the client will interact with to send and
 * receive information to and from the server.
 */
public class GameServiceProxy implements GameService {

	/**
	 * Write-only connection to the server.
	 */
	private DataInputStream in;
	
	/**
	 * Read-only connection to the server.
	 */
	private DataOutputStream out;
	
	/**
	 * Constructor for the GameServiceProxy object.
	 * @param socket The socket connection to be used that is already
	 * configured to connect to the server.
	 */
	public GameServiceProxy(Socket socket) {
		try {
			this.in = new DataInputStream(socket.getInputStream());
			this.out = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			System.err.println("Failed to get input/output stream.");
			e.printStackTrace();
		}
	}
	
	/**
	 * Sends game data to the server for restoration.
	 * @param gameData String representation of the game to restore
	 * @return The ID of the restored game.
	 */
	@Override
	public int restoreGame(String gameData) throws InvalidGameFileException, ConnectionFailedException {
		int gameId = -1; 
		
		//send info to server
		try {
			//send game to server
			out.writeInt(RESTORE_GAME);
			out.writeUTF(gameData);
			gameId = in.readInt();
			if (gameId == -1)
				throw new InvalidGameFileException();
		} catch (IOException e) {
			throw new ConnectionFailedException("Could not connect to server.");
		}
		
		return gameId;
	}

	/**
	 * Retrieves the game state of the game the user is currently playing.
	 * @param sessionId The session ID of the user
	 * @throws ConnectionFailedException
	 */
	@Override
	public GameState getGameState(int sessionId) throws ConnectionFailedException {
		GameState gameState = null;
		try {
			out.writeInt(GET_GAME_STATE);
			String gameData = in.readUTF();
			gameState = new Gson().fromJson(gameData, GameState.class);
		} catch (IOException e) {
			throw new ConnectionFailedException("Failed to connect to server.");
		}
		return gameState;
	}

	/**
	 * Forward authentication details to the server the user is
	 * connected to.
	 * @param user The username of the user
	 * @param password The password of the user
	 * @throws AuthenticationFailedException
	 * @throws ConnectionFailedException
	 */
	@Override
	public int authenticate(String user, String password) throws AuthenticationFailedException, ConnectionFailedException {
		try {
			out.writeInt(AUTHENTICATE);
			out.writeUTF(user);
			out.writeUTF(password);
			int sessionId = in.readInt();
			if (sessionId == -1) throw new AuthenticationFailedException();
			return sessionId;
		} catch(IOException e) {
			throw new ConnectionFailedException("Failed to connect to server.");
		}
	}

	/**
	 * Create a new game on the server the user is connected to.
	 * @return The ID of the new game.
	 * @throws ConnectionFailedException
	 */
	@Override
	public int createGame() throws ConnectionFailedException {
		try {
			out.writeInt(CREATE_GAME);
			int gameId = in.readInt();
			return gameId;
		} catch (IOException e) {
			throw new ConnectionFailedException("Failed to connect to server.");
		}
	}

	/**
	 * Retrieves a list of currently active games on the server the user is
	 * connected to.
	 * @param The ID of the user's session
	 * @returns A collection of active games that are joinable.
	 * @throws ConnectionFailedException
	 */
	@Override
	public ArrayList<Game> getGamesList(int sessionId) throws ConnectionFailedException {
		try {
			out.writeInt(GET_GAMES_LIST);
			ArrayList<Game> gamesList = new ArrayList<Game>();
			String gameString;
			while (!(gameString = in.readUTF()).equals("")) {
				Game game = null;
				game = new Gson().fromJson(gameString, GameState.class).toGame(game);
				gamesList.add(game);
			}
			return gamesList;
		} catch (IOException e) {
			throw new ConnectionFailedException("Failed to connect to server.");
		}
	}

	/**
	 * Add a user to a game.
	 * @param sessionId The ID of the user's session
	 * @param id The ID of the game to be joined.
	 * @throws ConnectionFailedException
	 */
	@Override
	public void joinGame(int sessionId, int id) throws ConnectionFailedException {
		try {
			out.writeInt(JOIN_GAME);
			out.writeInt(id);
			boolean hasPlayed = in.readBoolean();
			if (!hasPlayed) {
				Game game = null;
				for (Game g : Session.getInstance().getLocalGamesList()) {
					if (g.getId() == id) {
						game = g;
						break;
					}
				}
				int x = Session.getInstance().getServerListWindow().getX() +
						(Session.getInstance().getServerListWindow().getWidth() / 2);
				int y = Session.getInstance().getServerListWindow().getY() +
						(Session.getInstance().getServerListWindow().getHeight() / 2);
				NewPlayerDialog d = new NewPlayerDialog(Session.getInstance().getServerListWindow(), "New Player", game);
				d.setBounds(x - (d.getWidth() / 2), y - (d.getHeight() / 2), d.getWidth(), d.getHeight());
				d.setVisible(true);
				
				out.writeUTF(d.getSelectedShip());
			}
		} catch (IOException e) {
			throw new ConnectionFailedException("Failed to connect to server.");
		}
	}

	/**
	 * Remove the user from the game they are connected to.
	 * @param sessionId The ID of the user's session
	 * @throws ConnectionFailedException
	 */
	@Override
	public void disconnect(int sessionId) throws ConnectionFailedException {
		try {
			out.writeInt(DISCONNECT);
		} catch (IOException e) {
			throw new ConnectionFailedException("Failed to connect to server.");
		}
	}

	/**
	 * Navigate a ship within its current sector.
	 * @param sessionId The ID of the user's session
	 * @param x The X-coordinate of the location to navigate to
	 * @param y The Y-coordinate of the location to navigate to
	 * @throws BadDestinationException
	 * @throws ConnectionFailedException
	 * @throws ActionException
	 */
	@Override
	public void navigate(int sessionId, int x, int y) throws BadDestinationException, ConnectionFailedException, ActionException {
		try {
			out.writeInt(NAVIGATE);
			boolean canMove = in.readBoolean();
			if (!canMove) {
				boolean dead = in.readBoolean();
				if (dead) new ActionException("You are dead.");
				throw new ActionException("You have already moved this turn.");
			}
			out.writeInt(x);
			out.writeInt(y);
			String result = in.readUTF();
			if (!result.equals("success")) throw new BadDestinationException(result);
		} catch (IOException e) {
			throw new ConnectionFailedException("Failed to connect to server.");
		}
	}

	/**
	 * Navigate to the specified sector.
	 * @param sessionId The ID of the user's session
	 * @param sector The sector to be warped to
	 * @throws BadDestinationException
	 * @throws ConnectionFailedException
	 * @throws ActionException
	 */
	@Override
	public void warp(int sessionId, Sector sector) throws BadDestinationException, ConnectionFailedException, ActionException {
		try {
			out.writeInt(WARP);
			boolean canMove = in.readBoolean();
			if (!canMove) {
				boolean dead = in.readBoolean();
				if (dead) new ActionException("You are dead.");
				throw new ActionException("You have already moved this turn.");
			}
			out.writeInt(sector.getX());
			out.writeInt(sector.getY());
			String result = in.readUTF();
			if (result.equals("bad dest")) throw new BadDestinationException("Bad navigate destination");
			if (result.equals("bad energy")) throw new BadDestinationException("Not enough energy");
		} catch (IOException e) {
			throw new ConnectionFailedException("Failed to connect to server.");
		}
	}

	/**
	 * Set the user's ship's alert level.
	 * @param sessionId The ID of the user's session
	 * @param level The AlertLevel that the ship should be set to
	 * @throws ConnectionFailedException
	 * @throws ActionException
	 */
	@Override
	public void setAlertLevel(int sessionId, AlertLevel level) throws ConnectionFailedException, ActionException {
		try {
			out.writeInt(SET_ALERT_LEVEL);
			boolean canMove = in.readBoolean();
			if (!canMove) {
				boolean dead = in.readBoolean();
				if (dead) new ActionException("You are dead.");
				throw new ActionException("You have already moved this turn.");
			}
			out.writeUTF(level.toString());
		} catch (IOException e) {
			throw new ConnectionFailedException("Failed to connect to server.");
		}
	}

	/**
	 * Fire a torpedo at a ship within the same sector.
	 * @param sessionId The ID of the user's session
	 * @param sector The sector that the user's ship is currently in
	 * @param x The X-coordinate to fire a torpedo at
	 * @param y The Y-coordinate to fire a torpedo at
	 * @throws ActionException
	 * @throws ConnectionFailedException
	 */
	@Override
	public void fireTorpedo(int sessionId, Sector sector, int x, int y) throws ActionException, ConnectionFailedException {
		try {
			out.writeInt(FIRE_TORPEDO);
			boolean canMove = in.readBoolean();
			if (!canMove) {
				boolean dead = in.readBoolean();
				if (dead) new ActionException("You are dead.");
				throw new ActionException("You have already moved this turn.");
			}
			out.writeInt(sector.getX());
			out.writeInt(sector.getY());
			out.writeInt(x);
			out.writeInt(y);
			boolean success = in.readBoolean();
			if (!success) throw new ActionException("Fire torpedo failed.");
		} catch (IOException e) {
			throw new ConnectionFailedException("Failed to connect to server.");
		}
	}

}
