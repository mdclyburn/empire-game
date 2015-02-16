package com.kmj.empire.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.kmj.empire.common.AuthenticationFailedException;
import com.kmj.empire.common.ConnectionFailedException;
import com.kmj.empire.common.Player;

class User implements Runnable {

	DataOutputStream out;
	DataInputStream in;
	User user[] = new User[Server.MAX_USERS];
	Server server;
	
	private boolean disconnected = false;
	private boolean autheniticated = false;
	private String username, password;
	private int playerid;
	private Player player;
	
	public User(DataOutputStream out, DataInputStream in, User[] user, Server server, int pid)
	{
		this.out = out;
		this.in = in;
		this.user = user;
		this.server = server;
		this.playerid = pid;
	}
	
	public void run()
	{

		/* output codes */
		/* 1 - restoreGame() */
		/* 2 - getGameState() */
		/* 3 - getGamesList */
		/* 4 - authenticate */
		/* 5 - createGame */
		/* 6 - joinGame */
		
		//server loop
		while (!Thread.interrupted()) {
			int code = -1;
			try {
				code = in.readInt();
			} catch (IOException e3) {
				e3.printStackTrace();
			}
			switch(code) {
				case -1: 
					System.err.println("Failed to read code"); 
					System.exit(1); 
					
				case 1: try {
					String gameData = in.readUTF();
					server.getGameService().restoreGame(null);
				} catch (ConnectionFailedException e2) {
					e2.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} break;
				
				case 2: try {
					server.getGameService().getGameState(code);
				} catch (ConnectionFailedException e1) {
					e1.printStackTrace();
				} break;
				
				case 3: try {
					server.getGameService().getGamesList(code);
				} catch (AuthenticationFailedException
						| ConnectionFailedException e) {
					e.printStackTrace();
				} break;
				
				case 4: try {
					server.getGameService().authenticate(null, null);
				} catch (AuthenticationFailedException
						| ConnectionFailedException e) {
					e.printStackTrace();
				} break;
				
				case 5: try {
					server.getGameService().createGame();
				} catch (ConnectionFailedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} break;
				
				case 6: try {
					server.getGameService().joinGame(code, null);
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
	
	public int getPlayerID() {
		return playerid;
	}

	public void setupUser(User user) throws Exception {
		out.writeUTF("pa");
		out.writeInt(user.getPlayerID());
		out.writeUTF(user.getUsername());
		
	}
	
	public void removeUser(User user) throws Exception {
		
		int pid = user.getPlayerID();
		out.writeUTF("pr");
		out.writeInt(pid);
		
		this.user[pid] = null;
		
	}
	
}