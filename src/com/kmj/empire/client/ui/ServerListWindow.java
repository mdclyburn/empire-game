 package com.kmj.empire.client.ui;

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

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import com.kmj.empire.client.controller.Configuration;
import com.kmj.empire.client.controller.Session;
import com.kmj.empire.client.ui.model.GameListTableModel;
import com.kmj.empire.common.Game;
import com.kmj.empire.common.exceptions.AuthenticationFailedException;
import com.kmj.empire.common.exceptions.ConnectionFailedException;
import com.kmj.empire.common.exceptions.InvalidGameFileException;

/**
 * A window that displays a list of games present
 * on a server. The session ID should be set before
 * the client queries the server.
 */

public class ServerListWindow extends JFrame implements ActionListener, MouseListener, WindowListener {
	
	private static final long serialVersionUID = -3929227307873962693L;
	
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

	/**
	 * Default constructor. Subsequently calls the launch() function which constructs
	 * the user interface.
	 */
	public ServerListWindow() {
		super();
		launch();
	}
	
	/**
	 * Set up the window to be presented to the user.
	 */
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
		
		Session.getInstance().setServerListWindow(this);
		
		return;
	}
	
	/**
	 * Refresh the list of games available on the server.
	 */
	protected void refresh() {
		// Get new list from server.
		try {
			model.setTableSource(Session.getInstance().getGamesList());
		}
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

	/**
	 * Convenience function to close the window. It's much shorter
	 * than writing out the dispatchEvent.
	 */
	public void close() {
		dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
		return;
	}
	
	/**
	 * Responses for action events.
	 */
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
				System.out.println("Requesting to join game " + model.getValueAt(table.getSelectedRow(), 2));
				for (Game g : Session.getInstance().getLocalGamesList()) {
					if (g.getId() == (Integer) model.getValueAt(table.getSelectedRow(), 2)) {
						if (g.hasPlayed(Configuration.getInstance().getUsername()) 
								&& !g.getPossessionMapping().containsKey(Configuration.getInstance().getUsername())) {
							JOptionPane.showMessageDialog(this, "You have already died in this game.", "Join Error", JOptionPane.ERROR_MESSAGE);
							return;
						}
					}	
				}
				Session.getInstance().joinGame((Integer) model.getValueAt(table.getSelectedRow(), 2));
				System.out.println("Joined game");
			} catch (ConnectionFailedException c) {
				if(c.getMessage().length() != 0)
					JOptionPane.showMessageDialog(this, c.getMessage(), "Connection Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			System.out.println("Creating game window.");
			GameWindow w = new GameWindow(gameName, this);
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
				Session.getInstance().restoreGame(gameData);
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

	/**
	 * Custom function to make the window visible. When the window
	 * becomes visible, the list of games is refreshed.
	 */
	@Override public void setVisible(boolean b) {
		super.setVisible(b);
		if(b && (model != null)) refresh();
	}

	/**
	 * Responses for mouse clicks.
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		
		// The only mouse clicks being listened for are thos that occur inside the
		// table view. When it does happen, enable the joinButton.
		joinButton.setEnabled(true);
	}

	// Custom function to close the window. When this window
	// closes, the connection window should reappear.
	/**
	 * Custom function to close the window. When this window
	 * closes, the connection window will reappear.
	 * @param e the window event
	 */
	@Override
	public void windowClosing(WindowEvent e) {
		// Reopen the server connection window.
		ServerConnectionWindow w = new ServerConnectionWindow();
		
		// Close this window.
		dispose();
		
		return;
	}
	
	// Unused Functions =========================

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
