package com.kmj.empire.client;

import javax.swing.JFrame;

// A window that displays a list of games present
// on a server. The session ID should be set before
// the client queries the server.

public class ServerListWindow extends JFrame {

	protected int sessionId;

	public ServerListWindow() {
		sessionId = -1;
	}
	
	public ServerListWindow(int sessionId) {
		this.sessionId = sessionId;
	}
}
