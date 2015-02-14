package com.kmj.empire.client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.kmj.empire.common.AuthenticationFailedException;
import com.kmj.empire.common.ConnectionFailedException;

public class ServerConnectionWindow extends JFrame implements ActionListener {
	
	// =======================
	// ===== TEXT FIELDS =====
	// =======================
	protected JTextField addressField;
	protected JTextField portField;
	protected JTextField usernameField;
	protected JTextField passwordField;
	
	// ==============================
	// ===== POSITIONING VALUES =====
	// ==============================
	protected final static int WINDOW_WIDTH = 500;
	protected final static int WINDOW_HEIGHT = 225;
	protected final static int PADDING = 15;
	protected final static int LINE_START_X = PADDING;
	protected final static int LINE_START_Y = PADDING;
	protected final static int LINE_HEIGHT = 30;
	protected final static int LABEL_WIDTH = WINDOW_WIDTH / 5;
	protected final static int FIELD_WIDTH = WINDOW_WIDTH - LABEL_WIDTH - (3 * PADDING);
	protected final static int FIELD_HEIGHT = LINE_HEIGHT;
	protected final static int BUTTON_WIDTH = FIELD_WIDTH / 3;
	protected final static int BUTTON_HEIGHT = LINE_HEIGHT;
	
	// ==============================
	// ===== ACTION DEFINITIONS =====
	// ==============================
	protected final static String ACTION_CONNECT = "connect";
	protected final static String ACTION_CANCEL = "cancel";

	public ServerConnectionWindow() {
		super();
		setTitle("Connect To Server");
		setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(null);
		
		setResizable(false);
		
		// =======================
		// ===== LABEL SETUP =====
		// =======================
		JLabel label;
		
		// Server address label:
		label = new JLabel("Address:");
		label.setBounds(LINE_START_X, LINE_START_Y, LABEL_WIDTH, LINE_HEIGHT);
		add(label);
		
		// Server port label:
		label = new JLabel("Port:");
		label.setBounds(LINE_START_X, LINE_START_Y + (LINE_HEIGHT * 1), LABEL_WIDTH, LINE_HEIGHT);
		add(label);
		
		// Username label:
		label = new JLabel("Username:");
		label.setBounds(LINE_START_X, LINE_START_Y + (LINE_HEIGHT * 2), LABEL_WIDTH, LINE_HEIGHT);
		add(label);
		
		// Password label:
		label = new JLabel("Password:");
		label.setBounds(LINE_START_X, LINE_START_Y + (LINE_HEIGHT * 3), LABEL_WIDTH, LINE_HEIGHT);
		add(label);
		
		// ===========================
		// ===== TEXTFIELD SETUP =====
		// ===========================
		
		// Address field:
		addressField = new JTextField();
		addressField.setBounds(LINE_START_X + LABEL_WIDTH + PADDING, LINE_START_Y, FIELD_WIDTH, FIELD_HEIGHT);
		add(addressField);
		
		// Port field:
		portField = new JTextField();
		portField.setBounds(LINE_START_X + LABEL_WIDTH + PADDING, LINE_START_Y + (LINE_HEIGHT * 1), FIELD_WIDTH / 5, FIELD_HEIGHT);
		
		add(portField);
		
		// Username field:
		usernameField = new JTextField();
		usernameField.setBounds(LINE_START_X + LABEL_WIDTH + PADDING, LINE_START_Y + (LINE_HEIGHT * 2), FIELD_WIDTH, FIELD_HEIGHT);
		add(usernameField);
		
		// Password field:
		passwordField = new JPasswordField();
		passwordField.setBounds(LINE_START_X + LABEL_WIDTH + PADDING, LINE_START_Y + (LINE_HEIGHT * 3), FIELD_WIDTH, FIELD_HEIGHT);
		add(passwordField);
		
		// ========================
		// ===== BUTTON SETUP =====
		// ========================
		
		JButton button;
		
		// Connect button:
		button = new JButton();
		button.setText("Connect");
		button.setBounds(LINE_START_X, LINE_START_Y + (LINE_HEIGHT * 5), BUTTON_WIDTH, BUTTON_HEIGHT);
		button.addActionListener(this);
		button.setActionCommand(ACTION_CONNECT);
		add(button);
		
		// Close button:
		button = new JButton();
		button.setText("Close");
		button.setBounds(LINE_START_X + PADDING + BUTTON_WIDTH, LINE_START_Y + (LINE_HEIGHT * 5), BUTTON_WIDTH, BUTTON_HEIGHT);
		button.addActionListener(this);
		button.setActionCommand(ACTION_CANCEL);
		add(button);
		
		setVisible(true);
	}
	
	public void actionPerformed(ActionEvent e) {
		String s = e.getActionCommand();
		
		// Initiate connection to server.
		if(s.equals(ACTION_CONNECT)) {
			try {
				// A bit of a kludge here. Go ahead and check the port field
				// first and throw an exception.
				int port;
				try { port = Integer.parseInt(portField.getText()); }
				catch(NumberFormatException n) { throw new BadConfigurationException("Invalid characters in port field."); }
				Configuration conf = Configuration.getInstance();
				conf.setServerAddress(addressField.getText());
				conf.setServerPort(port);
				conf.setUsername(usernameField.getText());
				conf.setPassword(passwordField.getText());
			}
			catch(BadConfigurationException b) {
				JOptionPane.showMessageDialog(this, b.getMessage(), "Configuration Error", JOptionPane.ERROR_MESSAGE);
			}
			
			try {
				DummyServerConnectionProxy server = new DummyServerConnectionProxy();
				int id = server.authenticate(Configuration.getInstance().getUsername(),
						Configuration.getInstance().getPassword());
				ServerListWindow serverListWindow = new ServerListWindow(id);
				dispose();
			}
			catch(AuthenticationFailedException a) {
				JOptionPane.showMessageDialog(this, a.getMessage(), "Authentication Error", JOptionPane.ERROR_MESSAGE);
			}
			catch(ConnectionFailedException c) {
				JOptionPane.showMessageDialog(this, c.getMessage(), "Connection Error", JOptionPane.ERROR_MESSAGE);
			}
			
		}
		
		// Close window.
		else if(s.equals(ACTION_CANCEL)) {
			setVisible(false);
			dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
		}
	}
}
