package com.kmj.empire.client;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

public class UniverseView extends JPanel {

	public UniverseView() {
		super();
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		// Black background.
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, getWidth(), getHeight());
	}
}
