package com.kmj.empire.client;

import com.kmj.empire.client.ui.ServerConnectionWindow;

public class Main {

	public static void main(String[] args) {
		
		// Load config and bring up the server connection window to
		// prompt the user to enter details. The window
		// handles the launching of the needed tasks. This
		// is all that is needed here.
		Configuration.getInstance().load();
		ServerConnectionWindow d = new ServerConnectionWindow();
	}
}
