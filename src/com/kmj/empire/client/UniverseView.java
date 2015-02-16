package com.kmj.empire.client;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;

import com.kmj.empire.common.Game;
import com.kmj.empire.common.Location;

public class UniverseView extends JPanel implements MouseListener {
	
	protected Game game;
	protected Location selectedSector;
	protected SectorView sectorView;
	
	protected static final int PADDING = 5;

	public UniverseView() {
		super();
		selectedSector = new Location();
		addMouseListener(this);
	}
	
	public UniverseView(Game game) {
		super();
		this.game = game;
		selectedSector = new Location();
		addMouseListener(this);
	}
	
	public void setSectorView(SectorView sectorView) {
		this.sectorView = sectorView;
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
		
		// Draw box around selected sector
		g.setColor(Color.RED);
		Location topLeft = new Location();
		Location topRight = new Location();
		Location bottomLeft = new Location();
		Location bottomRight = new Location();
		
		topLeft.x = (selectedSector.x - 1) * getWidth() / 8;
		topLeft.y = (selectedSector.y - 1) * getHeight() / 8;
		
		topRight.x = selectedSector.x * getWidth() / 8;
		topRight.y = topLeft.y;
		
		bottomLeft.x = topLeft.x;
		bottomLeft.y = selectedSector.y * getHeight() / 8;
		
		bottomRight.x = topRight.x;
		bottomRight.y = bottomLeft.y;
		
		g.drawLine(topLeft.x, topLeft.y, topRight.x, topRight.y);
		g.drawLine(topLeft.x, topLeft.y, bottomLeft.x, bottomLeft.y);
		g.drawLine(bottomLeft.x, bottomLeft.y, bottomRight.x, bottomRight.y);
		g.drawLine(bottomRight.x, bottomRight.y, topRight.x, topRight.y);
		
		// Display information.
		g.setColor(Color.WHITE);
		for(int y = 0; y < 8; y++) {
			for(int x = 0; x < 8; x++) {
				g.drawString(Integer.toString(game.getSector(x, y).getPlanets()), (x * getWidth() / 8) + PADDING, (y * getHeight() / 8) + (3 * PADDING));
				g.drawString(Integer.toString(game.getSector(x,  y).getEnemyEntities()), (x * getWidth() / 8) + PADDING, (y * getHeight() / 8) + (6 * PADDING));
				g.drawString(Integer.toString(game.getSector(x, y).getFriendlyEntities()), (x * getWidth() / 8) + PADDING, (y * getHeight() / 8) + (9 * PADDING));
			}
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		selectedSector.x = (e.getX() / (getWidth() / 8)) + 1;
		selectedSector.y = (e.getY() / (getHeight() / 8)) + 1;
		repaint();
		
		// Notify the sector view of the new sector selection.
		sectorView.setSector(game.getSector(selectedSector.x, selectedSector.y));
		
		return;
	}

	@Override
	public void mousePressed(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}
}
