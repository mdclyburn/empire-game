package com.kmj.empire.client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import com.kmj.empire.common.AuthenticationFailedException;
import com.kmj.empire.common.ConnectionFailedException;
import com.kmj.empire.common.Game;
import com.kmj.empire.common.GameService;
import com.kmj.empire.common.InvalidGameFileException;

// A window that displays a list of games present
// on a server. The session ID should be set before
// the client queries the server.

public class ServerListWindow extends JFrame implements ActionListener, MouseListener, WindowListener {

	protected int sessionId;
	protected ArrayList<Game> gameList;
	
	protected GameService server;
	
	protected JTable table;
	protected GameListTableModel model;
	
	protected JButton joinButton; // Needed for dynamic behaviour.
	
	protected static final int WINDOW_WIDTH = 800;
	protected static final int WINDOW_HEIGHT = 450;
	
	protected static final int PADDING = 15;
	
	protected static final int TABLE_WIDTH = (3 * WINDOW_WIDTH / 4) - (2 * PADDING);
	protected static final int TABLE_HEIGHT = WINDOW_HEIGHT - (4 * PADDING);
	
	protected static final int BUTTON_WIDTH = (WINDOW_WIDTH / 4) - (2 * PADDING);
	protected static final int BUTTON_START = TABLE_WIDTH + (2 * PADDING);

	protected static final String ACTION_CREATE = "create";
	protected static final String ACTION_JOIN = "join";
	protected static final String ACTION_RESTORE = "restore";
	protected static final String ACTION_REFRESH = "refresh";
	protected static final String ACTION_DISCONNECT = "disconnect";

	public ServerListWindow() {
		super();
		sessionId = -1;
	}
	
	public ServerListWindow(int sessionId, GameService server) {
		super();
		this.sessionId = sessionId;
		this.server = server;
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
		table.setBounds(PADDING, PADDING, TABLE_WIDTH, TABLE_HEIGHT);
		table.addMouseListener(this);
		JScrollPane jsp = new JScrollPane(table);
		jsp.setBounds(table.getBounds());
		add(jsp);
		
		// ===================
		// ===== BUTTONS =====
		// ===================
		JButton button;
		
		button = new JButton("Create");
		button.setBounds(BUTTON_START, PADDING, BUTTON_WIDTH, button.getPreferredSize().height);
		button.setActionCommand(ACTION_CREATE);
		button.addActionListener(this);
		add(button);
		
		button = new JButton("Join");
		button.setBounds(BUTTON_START, PADDING + button.getPreferredSize().height + PADDING, BUTTON_WIDTH, button.getPreferredSize().height);
		button.setActionCommand(ACTION_JOIN);
		button.addActionListener(this);
		button.setEnabled(false); // Only enable when an item is selected.
		add(button);
		joinButton = button;
		
		button = new JButton("Restore");
		button.setBounds(BUTTON_START, PADDING + (2 * button.getPreferredSize().height) + (2 * PADDING), BUTTON_WIDTH, button.getPreferredSize().height);
		button.setActionCommand(ACTION_RESTORE);
		button.addActionListener(this);
		add(button);
		
		button = new JButton("Refresh");
		button.setBounds(BUTTON_START, PADDING + (3 * button.getPreferredSize().height) + (3 * PADDING), BUTTON_WIDTH, button.getPreferredSize().height);
		button.setActionCommand(ACTION_REFRESH);
		button.addActionListener(this);
		add(button);
		
		button = new JButton("Disconnect");
		button.setBounds(BUTTON_START, PADDING + (4 * button.getPreferredSize().height) + (4 * PADDING), BUTTON_WIDTH, button.getPreferredSize().height);
		button.setActionCommand(ACTION_DISCONNECT);
		button.addActionListener(this);
		add(button);

		// Attach the table model for viewing.
		model = new GameListTableModel();
		table.setModel(model);
		refresh();
		
		return;
	}
	
	public void setSessionId(int sessionId) { this.sessionId = sessionId; }
	
	protected void refresh() {
		// Get new list from server.
		try { model.setTableSource(server.getGamesList(sessionId)); }
		catch(AuthenticationFailedException e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), "Authentication Error", JOptionPane.ERROR_MESSAGE);
		}
		catch(ConnectionFailedException e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), "Connection Error", JOptionPane.ERROR_MESSAGE);
		}
		
		// Update table component.
		model.fireTableDataChanged();
		
		return;
	}
	
	public void close() {
		dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
		return;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String s = e.getActionCommand();
		
		// Create a new game.
		if(s.equals(ACTION_CREATE)) {
			
		}
		
		// Join the selected game.
		else if(s.equals(ACTION_JOIN)) {
			String gameName = ((String) table.getValueAt(table.getSelectedRow(), 0));
			if(gameName == null) return;
			try {
				server.joinGame(sessionId, gameName);
			} catch (ConnectionFailedException c) {
				if(c.getMessage().length() != 0)
					JOptionPane.showMessageDialog(this, c.getMessage(), "Connection Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			GameWindow w = new GameWindow(sessionId, gameName, this, server);
			setVisible(false);
		}
		
		// Pick file to restore.
		else if(s.equals(ACTION_RESTORE)) {
			JFileChooser fc = new JFileChooser();
			int result = fc.showOpenDialog(this);
			File file = null;
			if(result == JFileChooser.APPROVE_OPTION) {
				file = fc.getSelectedFile();
			}
			else return;
			
			try {
				BufferedReader br = new BufferedReader(new FileReader(file));
				String gameData = "";
				String line = br.readLine();
				while (line != null) {
					gameData += line + "\n";
					line = br.readLine();
				}
				br.close();
				server.restoreGame(gameData);
			} catch (InvalidGameFileException igfe) {
				JOptionPane.showMessageDialog(this, igfe.getMessage(), "Restoration Error", JOptionPane.ERROR_MESSAGE);
			} catch (ConnectionFailedException c) {
				JOptionPane.showMessageDialog(this, c.getMessage(), "Restoration Error", JOptionPane.ERROR_MESSAGE);
			} catch (IOException i) {
				JOptionPane.showMessageDialog(this, i.getMessage(), "Restoration Error", JOptionPane.ERROR_MESSAGE);
			}
			refresh();
		}
		
		// Refresh the game list.
		else if(s.equalsIgnoreCase(ACTION_REFRESH)) {
			joinButton.setEnabled(false);
			refresh();
		}
		
		// Disconnect from the server.
		else if(s.equals(ACTION_DISCONNECT)) {
			close();
		}
		
		return;
	}
	
	@Override public void setVisible(boolean b) {
		super.setVisible(b);
		if(b && (model != null)) refresh();
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		joinButton.setEnabled(true);
	}

	@Override
	public void windowClosing(WindowEvent e) {
		// Reopen the server connection window.
		ServerConnectionWindow w = new ServerConnectionWindow(server);
		
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

	@Override
	public void mousePressed(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}
}
