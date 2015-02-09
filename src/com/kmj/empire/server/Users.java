package com.kmj.empire.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

class Users implements Runnable {

	DataOutputStream out;
	DataInputStream in;
	Users user[] = new Users[Server.MAX_USERS];
	ArrayList<Users> userQueue = new ArrayList<Users>();
	ArrayList<Users> removeQueue = new ArrayList<Users>();
	ArrayList<String> chatQueue = new ArrayList<String>();
	Server server;
	int outputMode = 0;
	boolean newUser = false;
	
	private boolean disconnected = false;
	private String username, password;
	private int playerid;
	private int x, y;
	private int hp, maxhp;
	
	public Users(DataOutputStream out, DataInputStream in, Users[] user, Server server, int pid)
	{
		this.out = out;
		this.in = in;
		this.user = user;
		this.server = server;
		this.playerid = pid;
		this.x = this.y = 200;
		this.hp = this.maxhp = 100;
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
		/* pa - player add */
		/* pu - player update */
		/* pr - player remove */
		/* pm - player message */
		/* dc - disconnect */
		
		//server loop
		while (!Thread.interrupted()) {
			
			if (disconnected)
			{
				try {
					out.writeUTF("dc");
				} catch (IOException e) {
					e.printStackTrace();
				}
				Thread.currentThread().interrupt();
			}
			else if (removeQueue.size() > 0)
			{
				try {
					removeUser(removeQueue.get(0));
				} catch (Exception e) {
					System.out.println("Failed to Remove User");
					e.printStackTrace();
				}
			}
			
			else if (userQueue.size() > 0)
			{
				try {
					setupUser(userQueue.get(0));
				} catch (Exception e) {
					System.out.println("Failed to Setup User");
					e.printStackTrace();
				}
			}
			
			else {

				try {
					String message = in.readUTF();
					if (message.equals("pa"))
					{
						int x = in.readInt();
						int y = in.readInt();
						this.x = x;
						this.y = y;
					}
					else if (message.equals("pm"))
					{
						String s = in.readUTF();
						server.printMessage(s);
						for (int i = 0; i < Server.MAX_USERS; i++)
						{
							if (user[i] != null && user[i].playerid != getPlayerID()) {
								user[i].chatQueue.add(s);
							}
						}
					}
					else if (message.equals("ma"))
					{
						double mx = in.readDouble();
						double my = in.readDouble();
						double dir = in.readDouble();
						for (int i = 0; i < Server.MAX_USERS; i++) {
							if (user[i] != null && user[i].playerid != getPlayerID()) {
								out.writeUTF("ma");
								out.writeDouble(mx);
								out.writeDouble(my);
								out.writeDouble(dir);
							}
						}
					}
					for (int i = 0; i < Server.MAX_USERS; i++) {
						if (user[i] != null && user[i].playerid != getPlayerID()) {
							out.writeUTF("pu");
							out.writeInt(user[i].getPlayerID());
							out.writeInt(user[i].getX());
							out.writeInt(user[i].getY());
							out.writeInt(user[i].getHP());
						}
					}
					while (chatQueue.size() > 0)
					{
						out.writeUTF("pm");
						out.writeUTF(chatQueue.get(0));
						chatQueue.remove(0);
					}
				} catch (IOException e) {
					server.printMessage(username + " has disconnected");
					for (int i = 0; i < Server.MAX_USERS; i++)
					{
						if (user[i] != null)
						{
							user[i].removeQueue.add(user[playerid]);
						}
					}
					this.user[playerid] = null;
					
					Thread.currentThread().interrupt();
				}
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
	
	public void setupUser(Users user) throws Exception {
		out.writeUTF("pa");
		out.writeInt(user.getPlayerID());
		out.writeInt(user.getX());
		out.writeInt(user.getY());
		out.writeUTF(user.getUsername());
		
		userQueue.remove(user);
	}
	
	public void removeUser(Users user) throws Exception {
		
		int pid = user.getPlayerID();
		out.writeUTF("pr");
		out.writeInt(pid);
		
		removeQueue.remove(user);
		this.user[pid] = null;
		
	}
	
}