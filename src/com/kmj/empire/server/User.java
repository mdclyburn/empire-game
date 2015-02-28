package com.kmj.empire.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import com.google.gson.Gson;
import com.kmj.empire.common.AlertLevel;
import com.kmj.empire.common.Game;
import com.kmj.empire.common.GameService;
import com.kmj.empire.common.GameState;
import com.kmj.empire.common.Player;
import com.kmj.empire.common.Sector;
import com.kmj.empire.common.exceptions.ActionException;
import com.kmj.empire.common.exceptions.AuthenticationFailedException;
import com.kmj.empire.common.exceptions.BadDestinationException;
import com.kmj.empire.common.exceptions.ConnectionFailedException;
import com.kmj.empire.common.exceptions.InvalidGameFileException;

class User implements Runnable {

	private DataInputStream in;
	private DataOutputStream out;
	private User user[] = new User[Server.MAX_USERS];
	private Server server;
	private GameService gameService;
	
	private boolean disconnected = false;
	private boolean authenticated = false;
	private boolean canMove = true;
	private String username, password;
	private int sessionId;
	private Player player;
	
	public User(Socket socket, User[] user, Server server, int pid)
	{
		try {
			in = new DataInputStream(socket.getInputStream());
			out = new DataOutputStream(socket.getOutputStream()); 
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		this.user = user;
		this.server = server;
		this.sessionId = pid;

		gameService = new GameServiceImpl(server, this);
	}
	
	public void run()
	{
		System.out.println("User thread started...");
		
		//server loop
		while (!Thread.interrupted()) {
			
			//read code from the client
			int code = -1;
			try {
				code = in.readInt();
			} catch (IOException e3) {
				server.printMessage(username+" has disconnected");
				disconnected = true;
			}
		
			/*/respond with whether or not the user can take an action
			try {
				out.writeBoolean(canMove);
			} catch (IOException e) {
				disconnect();
			}//*/
			
			System.out.println("code: "+code);
			
			//if unauthenticated user attempts prohibited action
			if (disconnected || (!authenticated && code != GameService.AUTHENTICATE)) {
				System.out.println("ending user thread...");
				break;
			}
			
			canMove = false;
			
			switch(code) {
				case -1: 
					System.err.println("Failed to read code"); 
					System.exit(1); 

				/* restoreGame() request received */
				case GameService.RESTORE_GAME: try {
						System.out.println("restore game, reading utf");
						String gameData = in.readUTF();
						int gameId = getGameService().restoreGame(gameData);
						out.writeInt(gameId);
					} catch (ConnectionFailedException e2) {
						disconnected = true;
					} catch (IOException e) {
						disconnected = true;
					} catch (InvalidGameFileException e) {
						disconnected = true;
					} break;
				
				/* getGameState() request received */
				case GameService.GET_GAME_STATE: try {
						GameState gameState = getGameService().getGameState(server.getPlayerGame(sessionId).getId());
						out.writeUTF(new Gson().toJson(gameState));
					} catch (ConnectionFailedException e1) {
						disconnected = true;
					} catch (IOException e) {
						disconnected = true;
					} break;

				/* getGamesList() request received */
				case GameService.GET_GAMES_LIST: try {
						System.out.println("sending game list");
						for (Game g : getGameService().getGamesList(code)) {
							out.writeUTF(new Gson().toJson(new GameState(g)));
	
							System.out.println("sending game");
						}
						out.writeUTF("");
						System.out.println("sent game list");
					} catch (ConnectionFailedException e) {
						disconnected = true;
					} catch (AuthenticationFailedException e) {
						System.err.println(username+" attempted unauthorized action.");
						disconnected = true;
					} catch (IOException e) {
						disconnected = true;
					} break;

				/* authenticate() request received */
				case GameService.AUTHENTICATE: try {
						username = in.readUTF();
						password = in.readUTF();
						if (getGameService().authenticate(username, password) == 0) {
							out.writeInt(sessionId);
							authenticated = true;
						}
						else {
							out.writeInt(-1);
							disconnected = true;
						}
					} catch (AuthenticationFailedException e) {
						disconnected = true;
					} catch (ConnectionFailedException e) {
						disconnected = true;
					} catch (IOException e) {
						System.err.println("Failed to read username and password.");
						disconnected = true;
					} break;

				/* createGame() request received */
				case GameService.CREATE_GAME: try {
						getGameService().createGame();
					} catch (ConnectionFailedException e) {
						disconnected = true;
					} break;
				
				/* Received join request */
				case GameService.JOIN_GAME: try {
						int gameId = in.readInt();
						System.out.println("Joining gameid: "+gameId);
						getGameService().joinGame(sessionId, gameId);
					} catch (ConnectionFailedException e) {
						disconnected = true;
					} catch (IOException e) {
						disconnected = true;
					} break;
				
				case GameService.NAVIGATE: try {
						int x = in.readInt();
						int y = in.readInt();
						try {
							getGameService().navigate(sessionId, x, y);
							out.writeBoolean(true);
						} catch (BadDestinationException e) { 
							out.writeBoolean(false);
						}
					} catch (ConnectionFailedException e) {
						disconnected = true;
					} catch (IOException e) {
						disconnected = true;
					} break;
					
				case GameService.WARP: try {
						int x = in.readInt();
						int y = in.readInt();
						Sector sector = server.getPlayerGame(sessionId).getSector(x,y);
						try {
							getGameService().warp(sessionId, sector);
							out.writeBoolean(true);
						} catch (BadDestinationException e) {
							out.writeBoolean(false);
						}
					} catch (IOException e) {
						disconnected = true;
					}  catch (ConnectionFailedException e) {
						disconnected = true;
					} break;
					
				case GameService.SET_ALERT_LEVEL: try {
						String alertString = in.readUTF();
						if (alertString.equals("GREEN"))
							getGameService().setAlertLevel(sessionId, AlertLevel.GREEN);
						if (alertString.equals("YELLOW"))
							getGameService().setAlertLevel(sessionId, AlertLevel.YELLOW);
						if (alertString.equals("RED"))
							getGameService().setAlertLevel(sessionId, AlertLevel.RED);
					} catch (IOException e) {
						disconnected = true;
					} catch (ConnectionFailedException e) {
						disconnected = true;
					} break;
					
				case GameService.FIRE_TORPEDO: try {
						int sx = in.readInt();
						int sy = in.readInt();
						Sector sector = server.getPlayerGame(sessionId).getSector(sx, sy);
						int x = in.readInt();
						int y = in.readInt();
						try {
							getGameService().fireTorpedo(sessionId, sector, x, y);
							out.writeBoolean(true);
						} catch (ActionException e) {
							out.writeBoolean(false);
						}
					} catch (IOException e) {
						disconnected = true;
					}  catch (ConnectionFailedException e) {
						disconnected = true;
					} break;
					
				case GameService.DISCONNECT:
					disconnected = true;
					break;
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
	
	public Player getPlayer() {
		return player;
	}
	
	public void setPlayer(Player player) {
		this.player = player;
	}
	
	public DataOutputStream getOut() {
		return out;
	}
	
	public DataInputStream getIn() {
		return in;
	}
}