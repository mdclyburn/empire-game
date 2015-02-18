package com.kmj.empire.client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import com.kmj.empire.common.AlertLevel;
import com.kmj.empire.common.ConnectionFailedException;
import com.kmj.empire.common.Game;
import com.kmj.empire.common.GameService;
import com.kmj.empire.common.Player;

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
	
	protected static final int GAME_LOG_WIDTH = (4 * WINDOW_WIDTH / 5) - (2 * PADDING);
	protected static final int GAME_LOG_HEIGHT = (WINDOW_HEIGHT - DISPLAY_HEIGHT - (2 * PADDING)) / 2;
	protected static final int GAME_LOG_X = PADDING;
	protected static final int GAME_LOG_Y = UNIVERSE_VIEW_Y + DISPLAY_HEIGHT + PADDING;

	protected static final int PLAYER_LIST_WIDTH = DISPLAY_WIDTH / 3;
	protected static final int PLAYER_LIST_HEIGHT = GAME_LOG_HEIGHT - (3 * PADDING);
	protected static final int PLAYER_LIST_X = PADDING;
	protected static final int PLAYER_LIST_Y = PADDING + DISPLAY_HEIGHT + PADDING + GAME_LOG_HEIGHT + PADDING;
	
	protected static final int SHIP_ATTR_WIDTH = DISPLAY_WIDTH / 3;
	protected static final int SHIP_ATTR_HEIGHT = PLAYER_LIST_HEIGHT;
	protected static final int SHIP_ATTR_X = PLAYER_LIST_X + PLAYER_LIST_WIDTH + PADDING;
	protected static final int SHIP_ATTR_Y = PLAYER_LIST_Y;
	
	protected static final int NAVIGATE_ACTION_X = SHIP_ATTR_X + SHIP_ATTR_WIDTH + PADDING;
	protected static final int NAVIGATE_ACTION_Y = SHIP_ATTR_Y;
	protected static final int NAVIGATE_ACTION_WIDTH = DISPLAY_WIDTH / 3;
	
	protected static final int WEAPON_ACTION_X = NAVIGATE_ACTION_X + NAVIGATE_ACTION_WIDTH + PADDING;
	protected static final int WEAPON_ACTION_Y = SHIP_ATTR_Y;
	protected static final int WEAPON_ACTION_WIDTH = DISPLAY_WIDTH / 3;
	
	protected static final int OTHER_ACTION_X = WEAPON_ACTION_X + WEAPON_ACTION_WIDTH + PADDING;
	protected static final int OTHER_ACTION_Y = SHIP_ATTR_Y;
	protected static final int OTHER_ACTION_WIDTH = DISPLAY_WIDTH / 3;
	
	protected static final String ACTION_IMPULSE = "impulse";
	protected static final String ACTION_WARP = "warp";
	protected static final String ACTION_MISSILE = "missile";
	protected static final String ACTION_ALERT = "alert";
	protected static final String ACTION_REFRESH = "refresh";

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
		universeView = new UniverseView(this, gameState, server, sessionId);
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
		ArrayList<String> names = new ArrayList<String>();
		for (Player p : gameState.getActivePlayers())
			names.add(p.getUserame());
		playerListModel.setTableSource(names);
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
		jsp = new JScrollPane(shipAttributes, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		jsp.setBounds(shipAttributes.getBounds());
		add(jsp);
		
		sectorView.setTableModel(shipAttributeModel);
		
		// Navigate Label
		JLabel label = new JLabel("Navigate");
		label.setBounds(NAVIGATE_ACTION_X, NAVIGATE_ACTION_Y, DISPLAY_WIDTH * 3 / 5, LINE_HEIGHT);
		add(label);
		
		// Impulse button
		JButton impulseButton = new JButton("Impulse");
		impulseButton.setBounds(NAVIGATE_ACTION_X, NAVIGATE_ACTION_Y + label.getHeight() + PADDING, (3 * DISPLAY_WIDTH / 5) / 2, LINE_HEIGHT);
		impulseButton.setActionCommand(ACTION_IMPULSE);
		impulseButton.addActionListener(this);
		add(impulseButton);
		
		// Warp button
		JButton warpButton = new JButton("Warp");
		warpButton.setBounds(impulseButton.getX(), impulseButton.getY() + impulseButton.getHeight() + PADDING,
				impulseButton.getWidth(), impulseButton.getHeight());
		warpButton.setActionCommand(ACTION_WARP);
		warpButton.addActionListener(this);
		add(warpButton);
		
		// Weapon Label
		label = new JLabel("Weapons");
		label.setBounds(WEAPON_ACTION_X, WEAPON_ACTION_Y, WEAPON_ACTION_WIDTH, LINE_HEIGHT);
		add(label);
		
		// Missile button
		JButton missileButton = new JButton("Missile");
		missileButton.setBounds(WEAPON_ACTION_X, WEAPON_ACTION_Y + label.getHeight() + PADDING, WEAPON_ACTION_WIDTH, LINE_HEIGHT);
		missileButton.setActionCommand(ACTION_MISSILE);
		missileButton.addActionListener(this);
		add(missileButton);
		
		// Other Label
		label = new JLabel("Other Actions");
		label.setBounds(OTHER_ACTION_X, OTHER_ACTION_Y, OTHER_ACTION_WIDTH, LINE_HEIGHT);
		add(label);
		
		// Set Alert button
		JButton alertButton = new JButton("Alert...");
		alertButton.setBounds(OTHER_ACTION_X, OTHER_ACTION_Y + label.getHeight() + PADDING, OTHER_ACTION_WIDTH, LINE_HEIGHT);
		alertButton.setActionCommand(ACTION_ALERT);
		alertButton.addActionListener(this);
		add(alertButton);
		
		// Refresh button
		JButton refreshButton = new JButton("Refresh");
		refreshButton.setBounds(OTHER_ACTION_X, alertButton.getY() + LINE_HEIGHT + PADDING, OTHER_ACTION_WIDTH, LINE_HEIGHT);
		refreshButton.setActionCommand(ACTION_REFRESH);
		refreshButton.addActionListener(this);
		add(refreshButton);
		
		setVisible(true);
	}
	
	// Update statuses.
	public void refresh() {
		// Get game state from server.
		try {
			gameState = server.getGameState(sessionId);
		} catch (ConnectionFailedException e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), "Connection Error", JOptionPane.ERROR_MESSAGE);
		}
		// See if player is still alive.
		if(gameState.getPlayerShip(Configuration.getInstance().getUsername()) == null) {
			JOptionPane.showMessageDialog(this, "Your ship has been destroyed.", "Game Over", JOptionPane.INFORMATION_MESSAGE);
			dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
		}
		
		// Update player list.
		ArrayList<String> names = new ArrayList<String>();
		for (Player p : gameState.getActivePlayers())
			names.add(p.getUserame());
		playerListModel.setTableSource(names);

		playerListModel.fireTableDataChanged();
		gameLogModel.fireTableDataChanged();
		shipAttributeModel.fireTableDataChanged();
	}
	
	public void actionPerformed(ActionEvent e) {
		String s = e.getActionCommand();
		
		if(s.equals(ACTION_IMPULSE)) {
			// Switch view to current sector.
			sectorView.setSector(gameState.getPlayerShip(Configuration.getInstance().getUsername()).getSector());
			
			sectorView.setMode(SectorView.MODE_NAVIGATE);
		}
		else if(s.equals(ACTION_WARP)) {
			universeView.setMode(UniverseView.MODE_WARP);
		}
		else if(s.equals(ACTION_MISSILE)) {
			// Switch view to current sector.
			sectorView.setSector(gameState.getPlayerShip(Configuration.getInstance().getUsername()).getSector());
			
			sectorView.setMode(SectorView.MODE_MISSILE);
		}
		else if(s.equals(ACTION_ALERT)) {
			SetAlertDialog sad = new SetAlertDialog(this, "Set Alert Level", gameState.getPlayerShip(Configuration.getInstance().getUsername()));
			sad.setVisible(true);
			AlertLevel level;
			String result = sad.getChoice();
			if(result.equals("Green"))
				level = AlertLevel.GREEN;
			else if(result.equals("Yellow"))
				level = AlertLevel.YELLOW;
			else if(result.equals("Red"))
				level = AlertLevel.RED;
			else return;
			
			try {
				server.setAlertLevel(sessionId, level);
			} catch (ConnectionFailedException c) {
				JOptionPane.showMessageDialog(this, c.getMessage(), "Connection Error", JOptionPane.ERROR_MESSAGE);
			}
			
			refresh();
		}
		else if(s.equals(ACTION_REFRESH)) {
			refresh();
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
