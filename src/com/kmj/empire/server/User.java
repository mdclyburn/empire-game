/* User Class
 * 
 * This class encapsulates the communication of 1 user to the server.
 * It begins upon user connection and ends upon user disconnect. It reads
 * user codes and calls appropriate GameService methods, sending results back
 * to the user.
 * 
 * Coded by Joseph Savold  */

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
	private boolean dead = false;
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
			
			//if unauthenticated user attempts prohibited action
			if (disconnected || (!authenticated && code != GameService.AUTHENTICATE)) {
				server.printMessage("ending user thread...");
				break;
			}
			
			//choose action based on code recieved
			switch(code) {
				case -1: 
					server.printMessage("Failed to read client request code."); 
					System.exit(1); 

				/* restoreGame() request received */
				case GameService.RESTORE_GAME: try {
						String gameData = in.readUTF();
						int gameId = getGameService().restoreGame(gameData);
						out.writeInt(gameId);
					} catch (ConnectionFailedException e2) {
						disconnect();
					} catch (IOException e) {
						disconnect();
					} catch (InvalidGameFileException e) {
						disconnect();
					} break;
				
				/* getGameState() request received */
				case GameService.GET_GAME_STATE: try {
						GameState gameState = getGameService().getGameState(server.getPlayerGame(sessionId).getId());
						out.writeUTF(new Gson().toJson(gameState));
					} catch (ConnectionFailedException e1) {
						disconnect();
					} catch (IOException e) {
						disconnect();
					} break;

				/* getGamesList() request received */
				case GameService.GET_GAMES_LIST: try {
						for (Game g : getGameService().getGamesList(code))
							out.writeUTF(new Gson().toJson(new GameState(g)));
						out.writeUTF("");
					} catch (ConnectionFailedException e) {
						disconnect();
					} catch (AuthenticationFailedException e) {
						System.err.println(username+" attempted unauthorized action.");
						disconnect();
					} catch (IOException e) {
						disconnect();
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
							disconnect();
						}
					} catch (AuthenticationFailedException e) {
						disconnect();
					} catch (ConnectionFailedException e) {
						disconnect();
					} catch (IOException e) {
						server.printMessage("Failed to read username and password.");
						disconnect();
					} break;

				/* createGame() request received */
				case GameService.CREATE_GAME: try {
						getGameService().createGame();
					} catch (ConnectionFailedException e) {
						disconnect();
					} break;
				
				/* Received join request */
				case GameService.JOIN_GAME: try {
						int gameId = in.readInt();
						getGameService().joinGame(sessionId, gameId);
					} catch (ConnectionFailedException e) {
						disconnect();
					} catch (IOException e) {
						disconnect();
					} break;
				
				/* Received navigate request */
				case GameService.NAVIGATE: try {
						out.writeBoolean(canMove);
						if (!canMove) {
							out.writeBoolean(dead);
							continue;
						}
						setCanMove(false);
						int x = in.readInt();
						int y = in.readInt();
						try {
							getGameService().navigate(sessionId, x, y);
							out.writeUTF("success");
						} catch (BadDestinationException e) {
							out.writeUTF(e.getMessage());
							setCanMove(true);
						}
					} catch (ConnectionFailedException e) {
						disconnect();
					} catch (IOException e) {
						disconnect();
					} catch (ActionException e) {
						disconnect();
					} break;

				/* Received warp request */
				case GameService.WARP: try {
						out.writeBoolean(canMove);
						if (!canMove) {
							out.writeBoolean(dead);
							continue;
						}
						setCanMove(false);
						int x = in.readInt();
						int y = in.readInt();
						Sector sector = server.getPlayerGame(sessionId).getSector(x,y);
						try {
							getGameService().warp(sessionId, sector);
							out.writeUTF("success");
						} catch (BadDestinationException e) {
							out.writeUTF(e.getMessage());
							setCanMove(true);
						}
					} catch (IOException e) {
						disconnect();
					} catch (ConnectionFailedException e) {
						disconnect();
					} catch (ActionException e) {
						disconnect();
					} break;
					

				/* Received set alert level request */
				case GameService.SET_ALERT_LEVEL: try {
						out.writeBoolean(canMove);
						if (!canMove) {
							out.writeBoolean(dead);
							continue;
						}
						setCanMove(false);
						String alertString = in.readUTF();
						if (alertString.equals("GREEN"))
							getGameService().setAlertLevel(sessionId, AlertLevel.GREEN);
						if (alertString.equals("YELLOW"))
							getGameService().setAlertLevel(sessionId, AlertLevel.YELLOW);
						if (alertString.equals("RED"))
							getGameService().setAlertLevel(sessionId, AlertLevel.RED);
					} catch (IOException e) {
						disconnect();
					} catch (ConnectionFailedException e) {
						disconnect();
					} catch (ActionException e) {
						disconnect();
					} break;
					

				/* Received fire torpedo request */
				case GameService.FIRE_TORPEDO: try {
						out.writeBoolean(canMove);
						if (!canMove) {
							out.writeBoolean(dead);
							continue;
						}
						setCanMove(false);
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
							setCanMove(true);
						}
					} catch (IOException e) {
						disconnect();
					}  catch (ConnectionFailedException e) {
						disconnect();
					} break;
					

				/* Received disconnect request */
				case GameService.DISCONNECT: try {
					getGameService().disconnect(sessionId);
				} catch (ConnectionFailedException e) {
					disconnect();
				}
				break;
			}
		}
	}
	
	/* disconnect the user and remove them from the list of active users */
	public void disconnect() {
		disconnected = true;
		user[sessionId] = null;
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
	
	public void setCanMove(boolean canMove) {
		this.canMove = canMove;
	}
	
	public DataOutputStream getOut() {
		return out;
	}
	
	public DataInputStream getIn() {
		return in;
	}
}