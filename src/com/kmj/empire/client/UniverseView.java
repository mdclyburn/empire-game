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
		
		// Draw grid.
		g.setColor(Color.WHITE);
		for(int i = 1; i < 8; i++) {
			g.drawLine(i * getWidth() / 8, 0, i * getWidth() / 8, getHeight());
			g.drawLine(0, i * getHeight() / 8, getWidth(), i * getHeight() / 8);
		}
	}
}
