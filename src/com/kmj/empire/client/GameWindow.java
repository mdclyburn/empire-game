package com.kmj.empire.client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;

// The GameWindow is where the user will spend most of their time
// during gameplay. It will consist of a canvas handled by a custom
// engine and buttons at the bottom of the display to facilitate
// user actions.

public class GameWindow extends JFrame implements ActionListener, WindowListener {
	
	protected int sessionId;
	protected String name;
	
	protected ServerListWindow serverListWindow;
	
	UniverseView universeView;
	SectorView sectorView;
	
	protected static final int WINDOW_WIDTH = 1000;
	protected static final int WINDOW_HEIGHT = 800;
	protected static final int PADDING = 15;
	
	protected static final int DISPLAY_WIDTH = (WINDOW_WIDTH / 2) - (2 * PADDING);
	protected static final int DISPLAY_HEIGHT = DISPLAY_WIDTH;
	protected static final int UNIVERSE_VIEW_X = PADDING;
	protected static final int UNIVERSE_VIEW_Y = PADDING;
	protected static final int SECTOR_VIEW_X = DISPLAY_WIDTH + (3 * PADDING);
	protected static final int SECTOR_VIEW_Y = PADDING;

	public GameWindow() {
		super();
		setSessionId(-1);
		System.out.println("The default GameWindow constructor is in use. No valid\n" +
				"session ID was given. Correct this.");
	}
	
	public GameWindow(int sessionId, String name, ServerListWindow serverListWindow) {
		super(name);
		setSessionId(sessionId);
		setName(name);
		this.serverListWindow = serverListWindow;
		launch();
	}
	
	protected void launch() {
		setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		setResizable(false);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLayout(null);
		addWindowListener(this);
		
		universeView = new UniverseView();
		universeView.setBounds(UNIVERSE_VIEW_X, UNIVERSE_VIEW_Y, DISPLAY_WIDTH, DISPLAY_HEIGHT);
		add(universeView);
		
		sectorView = new SectorView();
		sectorView.setBounds(SECTOR_VIEW_X, SECTOR_VIEW_Y, DISPLAY_WIDTH, DISPLAY_HEIGHT);
		add(sectorView);
		
		setVisible(true);
	}
	
	public void actionPerformed(ActionEvent e) {
		return;
	}
	
	public int getSessionId() { return sessionId; }
	public String getName() { return name; }
	
	public void setSessionId(int sessionId) { this.sessionId = sessionId; }
	public void setName(String name) { this.name = name; }

	@Override
	public void windowClosing(WindowEvent e) {
		// Show the server connection window.
		serverListWindow.setVisible(true);
		
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
