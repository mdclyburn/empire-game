package com.kmj.empire.client;

import com.kmj.empire.client.controller.Configuration;
import com.kmj.empire.client.ui.ServerConnectionWindow;

public class Main {

	public static void main(String[] args) {
		
		// Load the user's configuration from file
		// and start the server connection window.
		Configuration.getInstance().load();
		ServerConnectionWindow d = new ServerConnectionWindow();
	}
}
