package com.kmj.empire.client;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;

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
	
	protected JTable table;
	
	protected static final int WINDOW_WIDTH = 800;
	protected static final int WINDOW_HEIGHT = 450;
	protected static final int PADDING = 15;

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
		setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		setResizable(false);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLayout(null);
		addWindowListener(this);
		setVisible(true);
		
		// Set up JTable.
		table = new JTable();
		table.setBounds(PADDING, PADDING, WINDOW_WIDTH - (2 * PADDING), 4 * WINDOW_HEIGHT / 5);
		JScrollPane jsp = new JScrollPane(table);
		jsp.setBounds(table.getBounds());
		add(jsp);
		
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

		// Attach the table model for viewing.
		GameListTableModel model = new GameListTableModel();
		model.setTableSource(gameList);
		table.setModel(model);
		
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
