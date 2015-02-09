package com.kmj.empire.client;

import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JDialog;


public class ConnectToServerDialog extends JDialog{

	private static final long serialVersionUID = 1L;
	private JTextField username;
	private JTextField password;
	private JTextField serverAddress;
	private JTextField serverPort;
	private Configuration config;
	private JButton btnSave;
	private JButton btnCancel;
	
	public ConnectToServerDialog(Configuration previous){
		config = previous;
		setTitle("Configuration");
		setSize(600,300);
		setResizable(false);
		getContentPane().setLayout(null);
		
		username = new JTextField();
		username.setBounds(273, 73, 202, 20);
		username.setText(config.getUsername());
		getContentPane().add(username);
		
		password = new JTextField();
		password.setBounds(273, 11, 202, 20);
		password.setText(config.getPassword());
		getContentPane().add(password);
		
		serverAddress = new JTextField();
		serverAddress.setBounds(273, 42, 202, 20);
		serverAddress.setText(config.getServerAddress());
		getContentPane().add(serverAddress);
		
		serverPort = new JTextField();
		serverPort.setBounds(273, 42, 202, 20);
		serverPort.setText(Integer.toString(config.getServerPort()));
		getContentPane().add(serverPort);
		
		btnSave = new JButton("Continue");
		btnSave.setBounds(216, 166, 89, 23);
		btnSave.setActionCommand("1");
		getContentPane().add(btnSave);
		
		btnCancel = new JButton("Cancel");
		btnCancel.setBounds(315, 166, 89, 23);
		btnCancel.setActionCommand("2");
		getContentPane().add(btnCancel);
		
		
	}
	
	public void actionPerformed(ActionEvent e) {
		int event = Integer.parseInt(e.getActionCommand());
		
		switch(event) {
		
			case 1:
							
				break;
			case 2:
				
				break;
		}
	}
}