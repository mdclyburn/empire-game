package com.kmj.empire.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

class User implements Runnable {

	DataOutputStream out;
	DataInputStream in;
	User user[] = new User[Server.MAX_USERS];
	Server server;
	
	private boolean disconnected = false;
	private boolean autheniticated = false;
	private String username, password;
	private int playerid;
	
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
		//connect and authenticate
		try {
			username = in.readUTF();
			password = in.readUTF();
			//if authentication successful, send player session ID
			if (server.authenticate(username, password) != -1)
				out.writeInt(playerid);
			//else send -1 to indicate failed authentication
			else {
				out.writeInt(-1);
				disconnected = true;
			}
		} catch (IOException e1) {
			server.printMessage("Authentication attempt failed: "+playerid);
		}
		//add self
		userQueue.add(this);
		//get other users and init this for other users
		for(int i = 0; i < Server.MAX_USERS; i++) {
			if (user[i] != null && user[i] != this) {	
				user[i].userQueue.add(this);
				userQueue.add(user[i]);
			}
		}
		/* output codes */
		/* 1 - restoreGame() */
		/* 2 - getGameState() */
		/* 3 - getGamesList */
		/* 4 - authenticate */
		/* 5 - createGame */
		/* 6 - joinGame */
		
		//server loop
		while (!Thread.interrupted()) {
			String code = in.readInt();
			switch(code) {
				case 1: restoreGame();
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
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getHP() {
		return hp;
	}
	
	public int getMaxHP() {
		return maxhp;
	}
	
	public void setHP(int hp) {
		this.hp = hp;
	}
	
	public void setupUser(User user) throws Exception {
		out.writeUTF("pa");
		out.writeInt(user.getPlayerID());
		out.writeInt(user.getX());
		out.writeInt(user.getY());
		out.writeUTF(user.getUsername());
		
		userQueue.remove(user);
	}
	
	public void removeUser(User user) throws Exception {
		
		int pid = user.getPlayerID();
		out.writeUTF("pr");
		out.writeInt(pid);
		
		removeQueue.remove(user);
		this.user[pid] = null;
		
	}
	
}