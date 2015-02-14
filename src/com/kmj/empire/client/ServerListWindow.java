package com.kmj.empire.client;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.kmj.empire.common.ConnectionFailedException;
import com.kmj.empire.common.Game;
import com.kmj.empire.common.GameService;

// A window that displays a list of games present
// on a server. The session ID should be set before
// the client queries the server.

public class ServerListWindow extends JFrame implements WindowListener {

	protected int sessionId;
	protected ArrayList<Game> gameList;
	
	protected GameService server;

	public ServerListWindow() {
		super();
		sessionId = -1;
	}
	
	public ServerListWindow(int sessionId) {
		super();
		this.sessionId = sessionId;
		launch();
	}
	
	protected void launch() {
		setSize(800, 450);
		setResizable(false);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		addWindowListener(this);
		setVisible(true);
		
		// Get active game list. This uses the dummy service for
		// the prototype and should be changed later.
		server = new DummyServerConnectionProxy();
		try {
			gameList = server.getGameList(sessionId);
		}
		catch(ConnectionFailedException e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), "Connection Error", JOptionPane.ERROR_MESSAGE);
			close();
		}
		
		return;
	}
	
	public void setSessionId(int sessionId) { this.sessionId = sessionId; }
	
	public void close() {
		dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
		return;
	}

	@Override
	public void windowClosing(WindowEvent e) {
		// Reopen the server connection window.
		ServerConnectionWindow w = new ServerConnectionWindow();
		
		// Close this window.
		dispose();
		
		return;
	}

	@Override
	public void windowOpened(WindowEvent e) {}

	@Override
	public void windowClosed(WindowEvent e) {}

	@Override
	public void windowIconified(WindowEvent e) {}

	@Override
	public void windowDeiconified(WindowEvent e) {}

	@Override
	public void windowActivated(WindowEvent e) {}

	@Override
	public void windowDeactivated(WindowEvent e) {}
}
