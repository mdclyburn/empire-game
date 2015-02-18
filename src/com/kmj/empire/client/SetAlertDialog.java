package com.kmj.empire.client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JRadioButton;
import javax.swing.WindowConstants;

import com.kmj.empire.common.Ship;

public class SetAlertDialog extends JDialog implements ActionListener {
	
	private static final int DIALOG_WIDTH = 300;
	private static final int DIALOG_HEIGHT = 200;
	private static final int PADDING = 35;
	
	private static final String ACTION_OK = "ok";
	
	private Ship ship;
	private String result;
	
	public SetAlertDialog() {
		
	}
	
	public SetAlertDialog(JFrame parent, String title, Ship ship) {
		super(parent, title, true);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setSize(DIALOG_WIDTH, DIALOG_HEIGHT);
		setLayout(null);
		this.ship = ship;
		result = "";
		
		ButtonGroup group = new ButtonGroup();
		
		JRadioButton radio = new JRadioButton("Green");
		radio.addActionListener(this);
		radio.setBounds(PADDING, PADDING, 200, 30);
		group.add(radio);
		add(radio);
		
		radio = new JRadioButton("Yellow");
		radio.addActionListener(this);
		radio.setBounds(PADDING, PADDING * 2, 200, 30);
		group.add(radio);
		add(radio);
		
		radio = new JRadioButton("Red");
		radio.addActionListener(this);
		radio.setBounds(PADDING, PADDING * 3, 200, 30);
		group.add(radio);
		add(radio);

		JButton button = new JButton("OK");
		button.setActionCommand(ACTION_OK);
		button.setBounds((DIALOG_WIDTH / 2) - 50, 140, 100, 30);
		button.addActionListener(this);
		add(button);
	}
	
	public void actionPerformed(ActionEvent e) {
		String s = e.getActionCommand();
		if(s.equals("Green") || s.equals("Yellow") || s.equals("Red"))
			result = s;
		if(s.equals(ACTION_OK)) {
			dispose();
		}
	}
	
	public String getChoice() { return result; }
	
}
