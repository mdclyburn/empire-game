package com.kmj.empire.client.controller;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.kmj.empire.common.AlertLevel;
import com.kmj.empire.common.Game;
import com.kmj.empire.common.GameService;
import com.kmj.empire.common.GameState;
import com.kmj.empire.common.Sector;
import com.kmj.empire.common.exceptions.AuthenticationFailedException;
import com.kmj.empire.common.exceptions.BadDestinationException;
import com.kmj.empire.common.exceptions.ConnectionFailedException;
import com.kmj.empire.common.exceptions.InvalidGameFileException;

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
			throw new ConnectionFailedException();
		}
		
		return gameId;
	}

	@Override
	public GameState getGameState(int sessionId) throws ConnectionFailedException {
		GameState gameState = null;
		try {
			out.writeInt(GET_GAME_STATE);
			String gameData = in.readUTF();
			gameState = new Gson().fromJson(gameData, GameState.class);
		} catch (IOException e) {
			throw new ConnectionFailedException();
		}
		return gameState;
	}

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
			throw new ConnectionFailedException();
		}
	}

	@Override
	public int createGame() throws ConnectionFailedException {
		try {
			out.writeInt(CREATE_GAME);
			int gameId = in.readInt();
			return gameId;
		} catch (IOException e) {
			throw new ConnectionFailedException();
		}
	}

	@Override
	public ArrayList<Game> getGamesList(int sessionId)
			throws AuthenticationFailedException, ConnectionFailedException {
		try {
			out.writeInt(GET_GAMES_LIST);
			ArrayList<Game> gamesList = new ArrayList<Game>();
			String gameString;
			while (!(gameString = in.readUTF()).equals("")) {
				System.out.println("Received game: "+gameString);
				Game game = null;
				game = new Gson().fromJson(gameString, GameState.class).toGame(game);
				gamesList.add(game);
				System.out.println("Game: "+game);
			}
			return gamesList;
		} catch (IOException e) {
			throw new ConnectionFailedException();
		}
	}

	@Override
	public void joinGame(int sessionId, int id) throws ConnectionFailedException {
		try {
			out.writeInt(JOIN_GAME);
			out.writeInt(id);
		} catch (IOException e) {
			throw new ConnectionFailedException("Connection failed while joining game.");
		}
	}

	@Override
	public void disconnect(int sessionId) throws ConnectionFailedException {
		try {
			out.writeInt(DISCONNECT);
		} catch (IOException e) {
			throw new ConnectionFailedException("Connection failed while disconnecting.");
		}
	}

	@Override
	public void navigate(int sessionId, int x, int y) throws BadDestinationException, ConnectionFailedException {
		try {
			out.writeInt(NAVIGATE);
			out.writeInt(x);
			out.writeInt(y);
			boolean success = in.readBoolean();
			if (!success) throw new BadDestinationException("Bad navigate destination");
		} catch (IOException e) {
			throw new ConnectionFailedException("Connection failed while navigating.");
		}
	}

	@Override
	public void warp(int sessionId, Sector sector) throws BadDestinationException, ConnectionFailedException {
		try {
			out.writeInt(WARP);
			out.writeInt(sector.getX());
			out.writeInt(sector.getY());
			boolean success = in.readBoolean();
			if (!success) throw new BadDestinationException("Bad navigate destination");
		} catch (IOException e) {
			throw new ConnectionFailedException("Connection failed while warping.");
		}
	}

	@Override
	public void setAlertLevel(int sessionId, AlertLevel level) throws ConnectionFailedException {
		try {
			out.writeInt(SET_ALERT_LEVEL);
			out.writeUTF(level.toString());
		} catch (IOException e) {
			throw new ConnectionFailedException("Connection failed while setting alert level.");
		}
	}

	@Override
	public void fireTorpedo(int sessionId, Sector sector, int x, int y) throws ActionException, ConnectionFailedException {
		try {
			out.writeInt(FIRE_TORPEDO);
			out.writeInt(sector.getX());
			out.writeInt(sector.getY());
			out.writeInt(x);
			out.writeInt(y);
			boolean success = in.readBoolean();
			if (!success) throw new ActionException("Fire torpedo failed.");
		} catch (IOException e) {
			throw new ConnectionFailedException("Connection failed while firing torpedo.");
		}
	}

}
