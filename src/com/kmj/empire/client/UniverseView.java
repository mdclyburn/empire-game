package com.kmj.empire.client;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

import com.kmj.empire.common.Game;

public class UniverseView extends JPanel {
	
	protected Game game;
	
	protected static final int PADDING = 5;

	public UniverseView() {
		super();
	}
	
	public UniverseView(Game game) {
		super();
		this.game = game;
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
		
		// Display information.
		for(int y = 0; y < 8; y++) {
			for(int x = 0; x < 8; x++) {
				g.drawString(Integer.toString(game.getSector(x, y).getPlanets()), (x * getWidth() / 8) + PADDING, (y * getHeight() / 8) + (3 * PADDING));
				g.drawString(Integer.toString(game.getSector(x,  y).getEnemyEntities()), (x * getWidth() / 8) + PADDING, (y * getHeight() / 8) + (6 * PADDING));
				g.drawString(Integer.toString(game.getSector(x, y).getFriendlyEntities()), (x * getWidth() / 8) + PADDING, (y * getHeight() / 8) + (9 * PADDING));
			}
		}
	}
}
