package com.kmj.empire.client.ui;

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

import com.kmj.empire.client.controller.Configuration;
import com.kmj.empire.client.controller.Session;
import com.kmj.empire.client.controller.SessionObserver;
import com.kmj.empire.client.ui.model.GameLogTableModel;
import com.kmj.empire.client.ui.model.PlayerListTableModel;
import com.kmj.empire.client.ui.model.ShipAttributeTableModel;
import com.kmj.empire.common.AlertLevel;
import com.kmj.empire.common.Game;
import com.kmj.empire.common.exceptions.ConnectionFailedException;

// The GameWindow is where the user will spend most of their time
// during gameplay. It will consist of a canvas handled by a custom
// engine and buttons at the bottom of the display to facilitate
// user actions.

public class GameWindow extends JFrame implements SessionObserver, ActionListener, WindowListener {
	
	private static final long serialVersionUID = 4943992678626809378L;

	protected ServerListWindow serverListWindow;

	protected JLabel stardate;
	protected JLabel actionStatus;
	protected JTable playerList;
	protected JTable gameLog;
	protected JTable shipAttributes;
	protected PlayerListTableModel playerListModel;
	protected GameLogTableModel gameLogModel;
	protected ShipAttributeTableModel shipAttributeModel;

	protected UniverseView universeView;
	protected SectorView sectorView;

	protected static final int WINDOW_WIDTH = 800;
	protected static final int WINDOW_HEIGHT = 700;
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
		System.out.println("The default GameWindow constructor is in use. No valid\n" +
				"session ID was given. Correct this.");
	}

	public GameWindow(String name, ServerListWindow serverListWindow) {
		super(name);
		this.serverListWindow = serverListWindow;
		launch();
	}

	protected void launch() {
		setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		setResizable(false);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLayout(null);
		addWindowListener(this);

		// Get the initial state from the server.
		try {
			Session.getInstance().refresh();
		} catch (ConnectionFailedException e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), "Connection Error", JOptionPane.ERROR_MESSAGE);
			System.exit(-1);
		}

		// Update views.
		universeView = new UniverseView(this);
		universeView.setBounds(UNIVERSE_VIEW_X, UNIVERSE_VIEW_Y, DISPLAY_WIDTH, DISPLAY_HEIGHT);
		add(universeView);

		sectorView = new SectorView(this);
		sectorView.setBounds(SECTOR_VIEW_X, SECTOR_VIEW_Y, DISPLAY_WIDTH, DISPLAY_HEIGHT);
		add(sectorView);

		// Give the universe view the sector view so that
		// changes to universe view can be reflected in the
		// sector view.
		universeView.setSectorView(sectorView);
		
		Game game = Session.getInstance().getGame();

		// Player List
		playerList = new JTable();
		playerListModel = new PlayerListTableModel();
		playerList.setModel(playerListModel);
		playerList.setBounds(PLAYER_LIST_X, PLAYER_LIST_Y, PLAYER_LIST_WIDTH, PLAYER_LIST_HEIGHT);
		JScrollPane jsp = new JScrollPane(playerList);
		jsp.setBounds(playerList.getBounds());
		add(jsp);

		// Game log
		gameLog = new JTable();
		gameLogModel = new GameLogTableModel();
		gameLog.setModel(gameLogModel);
		gameLog.setBounds(GAME_LOG_X, GAME_LOG_Y, GAME_LOG_WIDTH, GAME_LOG_HEIGHT);
		jsp = new JScrollPane(gameLog);
		jsp.setBounds(gameLog.getBounds());
		add(jsp);

		// Stardate display
		stardate = new JLabel("Stardate " + Integer.toString(game.getStardate()));
		stardate.setBounds(gameLog.getX() + gameLog.getWidth() + PADDING, GAME_LOG_Y, WINDOW_WIDTH - GAME_LOG_WIDTH - (3 * PADDING), stardate.getPreferredSize().height);
		add(stardate);

		// Action status
		actionStatus = new JLabel("Idling");
		actionStatus.setBounds(gameLog.getX() + gameLog.getWidth() + PADDING, stardate.getY() + stardate.getHeight() + 5, WINDOW_WIDTH - GAME_LOG_WIDTH - (3 * PADDING), actionStatus.getPreferredSize().height);
		universeView.setStatus(actionStatus);
		sectorView.setStatus(actionStatus);
		add(actionStatus);

		// Ship Attributes
		shipAttributes = new JTable();
		shipAttributeModel = new ShipAttributeTableModel();
		shipAttributeModel.setTableSource(game.getPlayerShip(Configuration.getInstance().getUsername()));
		shipAttributes.setBounds(SHIP_ATTR_X, SHIP_ATTR_Y, SHIP_ATTR_WIDTH, SHIP_ATTR_HEIGHT);
		shipAttributes.setModel(shipAttributeModel);
		jsp = new JScrollPane(shipAttributes, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		jsp.setBounds(shipAttributes.getBounds());
		add(jsp);

		sectorView.setTableModel(shipAttributeModel);

		// Impulse button
		JButton impulseButton = new JButton("Impulse");
		impulseButton.setBounds(NAVIGATE_ACTION_X, NAVIGATE_ACTION_Y, (3 * DISPLAY_WIDTH / 5) / 2, impulseButton.getPreferredSize().height);
		impulseButton.setActionCommand(ACTION_IMPULSE);
		impulseButton.addActionListener(this);
		add(impulseButton);

		// Warp button
		JButton warpButton = new JButton("Warp");
		warpButton.setBounds(impulseButton.getX(), impulseButton.getY() + impulseButton.getHeight() + 5,
				impulseButton.getWidth(), impulseButton.getHeight());
		warpButton.setActionCommand(ACTION_WARP);
		warpButton.addActionListener(this);
		add(warpButton);

		// Missile button
		JButton missileButton = new JButton("Missile");
		missileButton.setBounds(WEAPON_ACTION_X, WEAPON_ACTION_Y, WEAPON_ACTION_WIDTH, missileButton.getPreferredSize().height);
		missileButton.setActionCommand(ACTION_MISSILE);
		missileButton.addActionListener(this);
		add(missileButton);

		// Set Alert button
		JButton alertButton = new JButton("Alert...");
		alertButton.setBounds(OTHER_ACTION_X, OTHER_ACTION_Y, OTHER_ACTION_WIDTH, alertButton.getPreferredSize().height);
		alertButton.setActionCommand(ACTION_ALERT);
		alertButton.addActionListener(this);
		add(alertButton);

		// Refresh button
		JButton refreshButton = new JButton("Refresh");
		refreshButton.setBounds(OTHER_ACTION_X, alertButton.getY() + alertButton.getHeight() + 5, OTHER_ACTION_WIDTH, refreshButton.getPreferredSize().height);
		refreshButton.setActionCommand(ACTION_REFRESH);
		refreshButton.addActionListener(this);
		add(refreshButton);
		
		// Listen for changes.
		Session.getInstance().addObserver(this);
		Session.getInstance().addObserver(playerListModel);
		Session.getInstance().addObserver(playerListModel);
		Session.getInstance().addObserver(gameLogModel);

		setVisible(true);
	}
	
	@Override
	public void onIdChanged(int newId) {}
	
	@Override
	public void onGameChanged(Game newGame) {
		// See if the player is still alive.\
		checkGameOver();
		
		// Set the stardate.
		stardate.setText("Stardate " + Integer.toString(Session.getInstance().getGame().getStardate()));
	}

	// Update statuses.
	private void checkGameOver() {
		// See if player is still alive.
		if(Session.getInstance().isGameOver()) {
			JOptionPane.showMessageDialog(this, "Your ship has been destroyed.", "Game Over", JOptionPane.INFORMATION_MESSAGE);
			dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
		}
	}

	public void actionPerformed(ActionEvent e) {
		String s = e.getActionCommand();
		Game game = Session.getInstance().getGame();

		if(s.equals(ACTION_IMPULSE)) {
			actionStatus.setText("Impulse Movement");
			// Switch view to current sector.
			sectorView.setSector(game.getPlayerShip(Configuration.getInstance().getUsername()).getSector());

			sectorView.setMode(SectorView.MODE_NAVIGATE);
		}
		else if(s.equals(ACTION_WARP)) {
			actionStatus.setText("Warping...");
			universeView.setMode(UniverseView.MODE_WARP);
		}
		else if(s.equals(ACTION_MISSILE)) {
			actionStatus.setText("Readying Missile...");
			// Switch view to current sector.
			sectorView.setSector(game.getPlayerShip(Configuration.getInstance().getUsername()).getSector());

			sectorView.setMode(SectorView.MODE_MISSILE);
		}
		else if(s.equals(ACTION_ALERT)) {
			actionStatus.setText("Going on Alert...");
			SetAlertDialog sad = new SetAlertDialog(this, "Set Alert Level", game.getPlayerShip(Configuration.getInstance().getUsername()));
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
				Session.getInstance().setAlertLevel(level);
			} catch (ConnectionFailedException c) {
				JOptionPane.showMessageDialog(this, c.getMessage(), "Connection Error", JOptionPane.ERROR_MESSAGE);
			}
			actionStatus.setText("Idling");
		}
		else if(s.equals(ACTION_REFRESH)) {
			actionStatus.setText("Refreshing...");
			try {
				Session.getInstance().refresh();
			} catch (ConnectionFailedException c) {
				JOptionPane.showMessageDialog(this, c.getMessage(), "Connection Error", JOptionPane.ERROR_MESSAGE);
				
			}
			actionStatus.setText("Idling");
		}

		return;
	}

	@Override
	public void windowClosing(WindowEvent e) {
		try {
			Session.getInstance().disconnect();
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
