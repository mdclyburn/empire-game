package com.kmj.empire.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

import com.google.gson.Gson;
import com.kmj.empire.server.Game;
import com.kmj.empire.server.GameService;

public class GameServiceProxy implements GameService {

	private Socket socket;
	private DataInputStream in;
	private DataOutputStream out;
	
	public GameServiceProxy(Socket socket) {
		this.socket = socket;
		try {
			this.in = new DataInputStream(socket.getInputStream());
			this.out = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			System.err.println("Failed to get input/output stream.");
			e.printStackTrace();
		}
	}
	
	@Override
	public int restoreGame(Game game) {
		int gameId = -1; 
		//package game to send to server
		
		//send info to server
		try {
			//send game to server
			out.writeUTF("rg");
			out.writeUTF(new Gson().toJson(game));
			gameId = in.readInt();
		} catch (IOException e) {
			System.out.println("Something went wrong restoring game.");
			e.printStackTrace();
		}
		
		return gameId;
	}

	@Override
	public Game getGameState(int gameId) {
		
		return null;
	}

	@Override
	public List<Game> getGamesList() {
		
		return null;
	}

	@Override
	public int authenticate(String user, String password) {
		
		return 0;
	}

	@Override
	public int createGame() {
		
		return 0;
	}

	@Override
	public void joinGame() {
	}

}
