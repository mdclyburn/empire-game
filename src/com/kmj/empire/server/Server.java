/* Server Class
 * 
 * This class runs and waits for incoming connections. When a user connects
 * it creates a thread to handle their connection. The amount of users is 
 * limited to the MAX_USERS value. The server also dictates a gui to manage
 * the server with.  
 * 
 * Coded by Joseph Savold  */

package com.kmj.empire.server;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import com.kmj.empire.common.Game;


public class Server extends JFrame {

	private static final long serialVersionUID = 1L;

	public static final int MAX_USERS = 10;

	public static Random random = new Random();
	private static int nextId = 0;
	
	private ArrayList<Game> gameList;
	
	private ServerSocket serverSocket;
	private Socket socket;
	private static User user[] = new User[MAX_USERS];
	private HashMap<Integer, Game> playerGameMapping;
	
	private JPanel chatWindow;
	private JTextPane chatPane;
	private JScrollPane scrollPane;
	private JTextField chatField;
	
	public Server(int port) throws Exception
	{
		//setup JFrame
		super("Server");
		initGui();
		initServer();
		initGameWatch();
		
		//Run Server
		printMessage("Server Starting...");
		serverSocket = new ServerSocket(port);
		printMessage("Server Online");
		while(true) {
			socket = serverSocket.accept();
			for (int i = 0; i < MAX_USERS; i++)
			{
				printMessage("Connection from: "+ socket.getInetAddress());
				if (user[i] == null)
				{
					user[i] = new User(socket, user, this, i);
					Thread thread = new Thread(user[i]);
					thread.start();
					break;
				}
			}
		}
	}	
	
	/* init server gui JFrame */
	private void initGui()
	{
		this.setPreferredSize(new Dimension(640, 480));
		this.setSize(new Dimension(640, 480));
		this.setResizable(false);
		this.setBackground(Color.WHITE);
		this.setLayout(new BorderLayout());
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		chatWindow = new JPanel();
		chatWindow.setLayout(new BorderLayout());
		
		chatPane = new JTextPane();
		chatPane.setEditable(false);
		
		StyleContext sc = StyleContext.getDefaultStyleContext();
        AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, Color.BLACK);

        aset = sc.addAttribute(aset, StyleConstants.FontFamily, "Lucida Console");
        aset = sc.addAttribute(aset, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);
        
        chatPane.setCharacterAttributes(aset, false);

		scrollPane = new JScrollPane(chatPane);
		scrollPane.setAutoscrolls(true);
		chatWindow.add(scrollPane, BorderLayout.CENTER);
		
		chatField = new JTextField();
		chatField.addKeyListener(new KeyListener() {
			
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
				{
					String message = chatField.getText();
					if (message.charAt(0) == '/')
					{
						serverCommand(message);
					}
					chatField.setText("");
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}

			@Override
			public void keyTyped(KeyEvent e) {
			}
		});
		
		chatWindow.add(chatField, BorderLayout.SOUTH);
		
		this.add(chatWindow, BorderLayout.CENTER);
		this.setVisible(true);
	}
	
	/* initialize server logic */
	private void initServer() {
		gameList = new ArrayList<Game>();
		playerGameMapping =  new HashMap<Integer, Game>();
	}

	/* changes stardate every 10 seconds */
	private void initGameWatch() {
		Thread gameWatch = new Thread() {
			public void run() {
				while (!this.isInterrupted()) {
					//while the thread is running sleep 10 seconds
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e) {
						printMessage("Game Watch Interrupted, server shutting down...");
						System.exit(1);
					}
					//if no games exists continue
					if (gameList.size() == 0) continue;
					printMessage("Games Advancing...");
					//else advance each game (ai moves, increment stardate, etc)
					for (Game g : gameList) {
						g.nextStardate();
						g.advance();
					}
					//give all players another action for this turn
					for (User u : user) {
						if (u != null) u.setCanMove(true);
					}
				}
			}
		};
		gameWatch.start();
	}
	
	public static void main(String args[]) throws Exception
	{
		@SuppressWarnings("unused")
		Server server = new Server(8080);
	}
	
	/* print a string to the server window */
	public void printMessage(String message)
	{
		chatPane.setText(chatPane.getText()+message+"\n");
		chatPane.setCaretPosition(chatPane.getText().length());
	}
	
	/* issue a command to the server (prefixed by '/') */
	public void serverCommand(String command)
	{
		String action, param = "";
		//get action command
		int actionEnd;
		if (command.contains(" ")) {
			actionEnd = command.indexOf(' ');
			param = command.substring(actionEnd+1, command.length());
		}
		else {
			actionEnd = command.length();
		}
		action = command.substring(1, actionEnd);
		//do action
		if (action.equals("players"))
		{
			printMessage("Players Online: ");
			for (int i = 0; i < MAX_USERS; i++)
			{
				if (user[i] != null)
				printMessage(user[i].getSessionId() + ". "+user[i].getUsername());
			}
		}
		else if (action.equals("kick"))
		{
			int kickID = 0;
			boolean found = false;
			for (int i = 0; i < Server.MAX_USERS; i++)
			{
				if (user[i] != null)
				{
					if (user[i].getUsername().equals(param))
					{	
						kickID = i;
						found = true;
						break;
					}
				}
			}
			if (found)
			{
				printMessage(user[kickID].getUsername()+" has been kicked");
				for (int i = 0; i < MAX_USERS; i++) {
					if (user[i] != null) {
						//user[i].chatQueue.add(new String(user[kickID].getUsername()+" has been kicked"));
					}
				}
				for (int i = 0; i < Server.MAX_USERS; i++)
				{
					if (user[i] != null && user[i].getSessionId() != kickID)
					{
						//user[i].removeQueue.add(user[kickID]);
					}
					user[kickID].disconnect();
					user[kickID] = null;
				}
			}
			else
			{
				printMessage("No Player: "+param);
			}
		}
		else if (action.equals("exit"))
		{
			System.exit(0);
		}
		else {
			printMessage("Unknown Command: "+action);
		}
	}
	
	public ArrayList<Game> getGamesList() {
		return gameList;
	}
	
	/* set the game associated with a players session id */
	public void setPlayerGame(int playerId, Game game) {
		playerGameMapping.put(playerId, game);
	}
	
	/* get the game associated with a player */
	public Game getPlayerGame(int playerId) {
		return playerGameMapping.get(playerId);
	}
	
	/* add a game to the active games list */
	public int addGame(Game game) {
		int id = nextId;
		nextId++;
		game.setId(id);
		gameList.add(game);
		return id;
	}

	/* return a game based on game id */
	public Game getGame(int gameId) {
		for (Game g : gameList) {
			if (g.getId() == gameId)
				return g;
		}
		return null;
	}

}
