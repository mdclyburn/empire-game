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
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
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

import com.kmj.empire.common.AuthenticationFailedException;
import com.kmj.empire.common.ConnectionFailedException;
import com.kmj.empire.common.Game;
import com.kmj.empire.common.GameService;


public class Server extends JFrame implements GameService {

	private static final long serialVersionUID = 1L;

	public static final int MAX_USERS = 10;

	public static Random random = new Random();
	
	public ArrayList<Game> gameList;
	
	private ServerSocket serverSocket;
	private Socket socket;
	private DataOutputStream out;
	private DataInputStream in;
	private static User user[] = new User[MAX_USERS];
	
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
		
		//Run Server
		printMessage("Server Starting...");
		serverSocket = new ServerSocket(port);
		printMessage("Server Online");
		while(true) {
			socket = serverSocket.accept();
			for (int i = 0; i < MAX_USERS; i++)
			{
				printMessage("Connection from: "+ socket.getInetAddress());
				out = new DataOutputStream(socket.getOutputStream());
				in = new DataInputStream(socket.getInputStream());
				if (user[i] == null)
				{
					user[i] = new User(out, in, user, this, i);
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
					else {
						printMessage("Server", message);
						for (int i = 0; i < MAX_USERS; i++) {
							if (user[i] != null) {
								user[i].chatQueue.add(new String("Server: "+message));
							}
						}
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
	}

	
	public static void main(String args[]) throws Exception
	{
		@SuppressWarnings("unused")
		Server server = new Server(8080);
	}
	
	public void printMessage(String message)
	{
		chatPane.setText(chatPane.getText()+message+"\n");
		chatPane.setCaretPosition(chatPane.getText().length());
	}
	
	public void printMessage(String name, String message)
	{
		chatPane.setText(chatPane.getText()+name+": "+message+"\n");
		chatPane.setCaretPosition(chatPane.getText().length());
	}
	
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
				printMessage(user[i].getPlayerID() + ". "+user[i].getUsername());
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
						user[i].chatQueue.add(new String(user[kickID].getUsername()+" has been kicked"));
					}
				}
				for (int i = 0; i < Server.MAX_USERS; i++)
				{
					if (user[i] != null && user[i].getPlayerID() != kickID)
					{
						user[i].removeQueue.add(user[kickID]);
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

	
	@Override
	public int restoreGame(Game game) {
		gameList.add(game);
		
		return 0;
	}

	
	@Override
	public Game getGameState(int gameId) {
		return gameList.get(gameId);
	}

	
	@Override
	public int authenticate(String user, String password) {
		//check username and password and return 0 if successful
		if (password.equals("p")) return 0;
		
		//return -1 if authentication unsuccessful
		return -1;
	}

	
	@Override
	public int createGame() {
		gameList.add(new Game());
		return 1;
	}

	
	@Override
	public void joinGame() {
	}

	@Override
	public ArrayList<Game> getGamesList(int sessionId)
			throws AuthenticationFailedException, ConnectionFailedException {
		// TODO Auto-generated method stub
		return null;
	}
}