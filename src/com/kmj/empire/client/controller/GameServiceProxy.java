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

/*
 * The object the client will interact with to send and
 * receive information to and from the server.
 */
public class GameServiceProxy implements GameService {

	private DataInputStream in;
	private DataOutputStream out;
	
	public GameServiceProxy(Socket socket) {
		try {
			this.in = new DataInputStream(socket.getInputStream());
			this.out = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			System.err.println("Failed to get input/output stream.");
			e.printStackTrace();
		}
	}
	
	// Send data for a game to the server for restoration. The
	// client is given the ID of the game in return.
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

	// Get the GameState of the Game the user is currently
	// playing.
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

	// Send authentication details to the server the user
	// is connecting to.
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

	// Add a new game to the server the user is currently connected
	// to.
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

	// Retrieves a list of currently loaded games on the server
	// for displaying to the user.
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

	// Add a user to the game they request.
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

	// Remove the user from the game they've connected to.
	@Override
	public void disconnect(int sessionId) throws ConnectionFailedException {
		try {
			out.writeInt(DISCONNECT);
		} catch (IOException e) {
			throw new ConnectionFailedException("Failed to connect to server.");
		}
	}

	// Allow the user to navigate within their current sector.
	@Override
	public void navigate(int sessionId, int x, int y) throws BadDestinationException, ConnectionFailedException, ActionException {
		try {
			out.writeInt(NAVIGATE);
			boolean canMove = in.readBoolean();
			if (!canMove) {
				boolean dead = in.readBoolean();
				if (dead) new ActionException("You are dead.");
				throw new ActionException("Cannot navigate at this time.");
			}
			out.writeInt(x);
			out.writeInt(y);
			boolean success = in.readBoolean();
			if (!success) throw new BadDestinationException("Bad navigate destination");
		} catch (IOException e) {
			throw new ConnectionFailedException("Failed to connect to server.");
		}
	}

	// Allow the user to warp to other sectors in the universe.
	@Override
	public void warp(int sessionId, Sector sector) throws BadDestinationException, ConnectionFailedException, ActionException {
		try {
			out.writeInt(WARP);
			boolean canMove = in.readBoolean();
			if (!canMove) {
				boolean dead = in.readBoolean();
				if (dead) new ActionException("You are dead.");
				throw new ActionException("Cannot navigate at this time.");
			}
			out.writeInt(sector.getX());
			out.writeInt(sector.getY());
			boolean success = in.readBoolean();
			if (!success) throw new BadDestinationException("Bad navigate destination");
		} catch (IOException e) {
			throw new ConnectionFailedException("Failed to connect to server.");
		}
	}

	// Send a request to the server to set the user's ship's alert
	// level.
	@Override
	public void setAlertLevel(int sessionId, AlertLevel level) throws ConnectionFailedException, ActionException {
		try {
			out.writeInt(SET_ALERT_LEVEL);
			boolean canMove = in.readBoolean();
			if (!canMove) {
				boolean dead = in.readBoolean();
				if (dead) new ActionException("You are dead.");
				throw new ActionException("Cannot navigate at this time.");
			}
			out.writeUTF(level.toString());
		} catch (IOException e) {
			throw new ConnectionFailedException("Failed to connect to server.");
		}
	}

	// Fire a torpedo at another ship within the same sector.
	@Override
	public void fireTorpedo(int sessionId, Sector sector, int x, int y) throws ActionException, ConnectionFailedException {
		try {
			out.writeInt(FIRE_TORPEDO);
			boolean canMove = in.readBoolean();
			if (!canMove) {
				boolean dead = in.readBoolean();
				if (dead) new ActionException("You are dead.");
				throw new ActionException("Cannot navigate at this time.");
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
