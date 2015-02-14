package com.kmj.empire.client;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;

// A window that displays a list of games present
// on a server. The session ID should be set before
// the client queries the server.

public class ServerListWindow extends JFrame implements WindowListener {

	protected int sessionId;

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
		
		return;
	}
	
	public void setSessionId(int sessionId) { this.sessionId = sessionId; }

	@Override
	public void windowClosing(WindowEvent e) {
		System.out.println("Closing serverlistwindow.");
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
