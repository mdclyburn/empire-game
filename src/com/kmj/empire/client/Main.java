package com.kmj.empire.client;

import com.kmj.empire.client.controller.Configuration;
import com.kmj.empire.client.controller.DummyServerConnectionProxy;
import com.kmj.empire.client.controller.Session;
import com.kmj.empire.client.ui.ServerConnectionWindow;

public class Main {

	public static void main(String[] args) {
		
		// Prepare the Session, load the configuration from
		// file (if it's there), and start the GUI. That's all
		// that should be needed here.
		Session.getInstance().setProvider(new DummyServerConnectionProxy());
		Configuration.getInstance().load();
		ServerConnectionWindow d = new ServerConnectionWindow();
	}
}
