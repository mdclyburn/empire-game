package com.kmj.empire.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import com.kmj.empire.common.AuthenticationFailedException;
import com.kmj.empire.common.ConnectionFailedException;
import com.kmj.empire.common.GameService;
import com.kmj.empire.common.InvalidGameFileException;
import com.kmj.empire.common.Player;

class User implements Runnable {

	private Socket socket;
	private DataInputStream in;
	private DataOutputStream out;
	private User user[] = new User[Server.MAX_USERS];
	private Server server;
	private GameService gameService;
	
	private boolean disconnected = false;
	//will be false what autheniticated is truely implemented
	private boolean autheniticated = true;
	private String username, password;
	private int sessionId;
	private Player player;
	
	public User(Socket socket, User[] user, Server server, int pid)
	{
		this.socket = socket;
		try {
			in = new DataInputStream(socket.getInputStream());
			out = new DataOutputStream(socket.getOutputStream()); 
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		this.user = user;
		this.server = server;
		this.sessionId = pid;

		gameService = new GameServiceImpl(server);
	}
	
	public void run()
	{
		System.out.println("User thread started...");
		/* output codes */
		/* 1 - restoreGame() */
		/* 2 - getGameState() */
		/* 3 - getGamesList */
		/* 4 - authenticate */
		/* 5 - createGame */
		/* 6 - joinGame */
		
		//server loop
		while (!Thread.interrupted()) {
			
			//read code from the client
			int code = -1;
			try {
				code = in.readInt();
			} catch (IOException e3) {
				e3.printStackTrace();
			}
			
			//if unauthenticated user attempts prohibited action
			if (!autheniticated && code != 4) {
				disconnected = true;
				break;
			}
			
			switch(code) {
				case -1: 
					System.err.println("Failed to read code"); 
					System.exit(1); 
					
				case 1: try {
					String gameData = in.readUTF();
					getGameService().restoreGame(gameData);
				} catch (ConnectionFailedException e2) {
					e2.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InvalidGameFileException e) {
					e.printStackTrace();
				} break;
				
				case 2: try {
					getGameService().getGameState(code);
				} catch (ConnectionFailedException e1) {
					e1.printStackTrace();
				} break;
				
				case 3: try {
					getGameService().getGamesList(code);
				} catch (AuthenticationFailedException
						| ConnectionFailedException e) {
					e.printStackTrace();
				} break;
				
				case 4: try {
					username = in.readUTF();
					password = in.readUTF();
					getGameService().authenticate(username, password);
				} catch (AuthenticationFailedException
						| ConnectionFailedException e) {
					e.printStackTrace();
				} catch (IOException e) {
					System.err.println("Failed to read username and password.");
					System.exit(1);
				} break;
				
				case 5: try {
					getGameService().createGame();
				} catch (ConnectionFailedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} break;
				
				case 6: try {
					getGameService().joinGame(code, null);
				} catch (ConnectionFailedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} break;
			}
		}
	}
	
	public void disconnect() {
		disconnected = true;
	}
	
	public String getUsername() {
		return username;
	}
	
	public int getSessionId() {
		return sessionId;
	}
	
	public GameService getGameService() {
		return gameService;
	}
}