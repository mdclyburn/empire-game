package com.kmj.empire.client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import com.kmj.empire.common.ConnectionFailedException;
import com.kmj.empire.common.Game;
import com.kmj.empire.common.GameService;

// The GameWindow is where the user will spend most of their time
// during gameplay. It will consist of a canvas handled by a custom
// engine and buttons at the bottom of the display to facilitate
// user actions.

public class GameWindow extends JFrame implements ActionListener, WindowListener {
	
	protected int sessionId;
	protected String name;
	
	protected Game gameState;
	protected GameService server;
	
	protected ServerListWindow serverListWindow;
	
	JTable playerList;
	JTable gameLog;
	JTable shipAttributes;
	PlayerListTableModel playerListModel;
	GameLogTableModel gameLogModel;
	ShipAttributeTableModel shipAttributeModel;
	
	UniverseView universeView;
	SectorView sectorView;
	
	protected static final int WINDOW_WIDTH = 1000;
	protected static final int WINDOW_HEIGHT = 800;
	protected static final int PADDING = 15;
	protected static final int LINE_HEIGHT = 25;
	
	protected static final int DISPLAY_WIDTH = (WINDOW_WIDTH / 2) - (2 * PADDING);
	protected static final int DISPLAY_HEIGHT = DISPLAY_WIDTH;
	protected static final int UNIVERSE_VIEW_X = PADDING;
	protected static final int UNIVERSE_VIEW_Y = PADDING;
	protected static final int SECTOR_VIEW_X = DISPLAY_WIDTH + (3 * PADDING);
	protected static final int SECTOR_VIEW_Y = PADDING;
	
	protected static final int PLAYER_LIST_LABEL_X = PADDING;
	protected static final int PLAYER_LIST_LABEL_Y = PADDING + DISPLAY_HEIGHT + PADDING;
	protected static final int PLAYER_LIST_WIDTH = DISPLAY_WIDTH / 2;
	protected static final int PLAYER_LIST_HEIGHT = WINDOW_HEIGHT - (4 * PADDING) - DISPLAY_HEIGHT;
	protected static final int PLAYER_LIST_X = PADDING;
	protected static final int PLAYER_LIST_Y = PADDING + DISPLAY_HEIGHT + PADDING;
	
	protected static final int GAME_LOG_WIDTH = PLAYER_LIST_WIDTH;
	protected static final int GAME_LOG_HEIGHT = PLAYER_LIST_HEIGHT;
	protected static final int GAME_LOG_X = PADDING + PLAYER_LIST_WIDTH + PADDING;
	protected static final int GAME_LOG_Y = PLAYER_LIST_Y;
	
	protected static final int SHIP_ATTR_WIDTH = 2 * DISPLAY_WIDTH / 5;
	protected static final int SHIP_ATTR_HEIGHT = PLAYER_LIST_HEIGHT;
	protected static final int SHIP_ATTR_X = GAME_LOG_X + GAME_LOG_WIDTH + PADDING;
	protected static final int SHIP_ATTR_Y = GAME_LOG_Y;
	
	protected static final int ACTION_X = SHIP_ATTR_X + SHIP_ATTR_WIDTH + PADDING;
	protected static final int ACTION_Y = SHIP_ATTR_Y;
	
	protected static final String ACTION_IMPULSE = "impulse";

	public GameWindow() {
		super();
		setSessionId(-1);
		System.out.println("The default GameWindow constructor is in use. No valid\n" +
				"session ID was given. Correct this.");
	}
	
	public GameWindow(int sessionId, String name, ServerListWindow serverListWindow, GameService server) {
		super(name);
		setSessionId(sessionId);
		setName(name);
		this.serverListWindow = serverListWindow;
		this.server = server;
		launch();
	}
	
	protected void launch() {
		setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		setResizable(false);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLayout(null);
		addWindowListener(this);

		// Retrieve information from server.
		try {
			gameState = server.getGameState(sessionId);
		} catch (ConnectionFailedException e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), "Connection Error", JOptionPane.ERROR_MESSAGE);
			System.exit(-1);
		}
		
		// Update views.
		universeView = new UniverseView(gameState);
		universeView.setBounds(UNIVERSE_VIEW_X, UNIVERSE_VIEW_Y, DISPLAY_WIDTH, DISPLAY_HEIGHT);
		add(universeView);
		
		sectorView = new SectorView(this, gameState, server, sessionId);
		sectorView.setBounds(SECTOR_VIEW_X, SECTOR_VIEW_Y, DISPLAY_WIDTH, DISPLAY_HEIGHT);
		add(sectorView);
		
		// Give the universe view the sector view so that
		// changes to universe view can be reflected in the
		// sector view.
		universeView.setSectorView(sectorView);
		
		// Player List
		playerList = new JTable();
		playerListModel = new PlayerListTableModel();
		playerListModel.setTableSource(gameState.getActivePlayers());
		playerList.setModel(playerListModel);
		playerList.setBounds(PLAYER_LIST_X, PLAYER_LIST_Y, PLAYER_LIST_WIDTH, PLAYER_LIST_HEIGHT);
		JScrollPane jsp = new JScrollPane(playerList);
		jsp.setBounds(playerList.getBounds());
		add(jsp);
		
		// Game log
		gameLog = new JTable();
		gameLogModel = new GameLogTableModel();
		gameLogModel.setTableSource(gameState.getLog());
		gameLog.setModel(gameLogModel);
		gameLog.setBounds(GAME_LOG_X, GAME_LOG_Y, GAME_LOG_WIDTH, GAME_LOG_HEIGHT);
		jsp = new JScrollPane(gameLog);
		jsp.setBounds(gameLog.getBounds());
		add(jsp);
		
		// Ship Attributes
		shipAttributes = new JTable();
		shipAttributeModel = new ShipAttributeTableModel();
		shipAttributeModel.setTableSource(gameState.getPlayerShip(Configuration.getInstance().getUsername()));
		shipAttributes.setBounds(SHIP_ATTR_X, SHIP_ATTR_Y, SHIP_ATTR_WIDTH, SHIP_ATTR_HEIGHT);
		shipAttributes.setModel(shipAttributeModel);
		jsp = new JScrollPane(shipAttributes);
		jsp.setBounds(shipAttributes.getBounds());
		add(jsp);
		
		sectorView.setTableModel(shipAttributeModel);
		
		// Navigate Label
		JLabel label = new JLabel("Navigate");
		label.setBounds(ACTION_X, ACTION_Y, DISPLAY_WIDTH * 3 / 5, LINE_HEIGHT);
		add(label);
		
		// Impulse button
		JButton impulseButton = new JButton("Impulse");
		impulseButton.setBounds(ACTION_X, ACTION_Y + label.getHeight() + PADDING, (3 * DISPLAY_WIDTH / 5) / 2, LINE_HEIGHT);
		impulseButton.setActionCommand(ACTION_IMPULSE);
		impulseButton.addActionListener(this);
		add(impulseButton);
		
		// Warp button
		JButton warpButton = new JButton("Warp");
		warpButton.setBounds(impulseButton.getX() + impulseButton.getWidth(),
				impulseButton.getY(), impulseButton.getWidth(), impulseButton.getHeight());
		add(warpButton);
		
		setVisible(true);
	}
	
	// Update tables.
	public void refresh() {
		playerListModel.fireTableDataChanged();
		gameLogModel.fireTableDataChanged();
		shipAttributeModel.fireTableDataChanged();
	}
	
	public void actionPerformed(ActionEvent e) {
		String s = e.getActionCommand();
		
		if(s.equals(ACTION_IMPULSE)) {
			sectorView.setMode(SectorView.MODE_NAVIGATE);
		}
		return;
	}
	
	public int getSessionId() { return sessionId; }
	public String getName() { return name; }
	
	public void setSessionId(int sessionId) { this.sessionId = sessionId; }
	public void setName(String name) { this.name = name; }

	@Override
	public void windowClosing(WindowEvent e) {
		try {
			server.disconnect(sessionId);
		} catch (ConnectionFailedException c) {
			JOptionPane.showMessageDialog(this, c.getMessage(), "Connection Error", JOptionPane.ERROR_MESSAGE);
		}
		
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
