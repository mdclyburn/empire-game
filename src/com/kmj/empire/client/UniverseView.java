package com.kmj.empire.client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;

import sun.reflect.annotation.TypeAnnotation.LocationInfo.Location;

import com.kmj.empire.common.Base;
import com.kmj.empire.common.Game;
import com.kmj.empire.common.Sector;
import com.kmj.empire.common.Ship;

public class UniverseView extends JPanel implements MouseListener {
	
	protected Game game;
	protected Sector selectedSector;
	protected SectorView sectorView;
	
	protected static final int PADDING = 5;

	public UniverseView() {
		super();
		selectedSector = game.getSector(1, 1);
		addMouseListener(this);
	}
	
	public UniverseView(Game game) {
		super();
		this.game = game;
		selectedSector = game.getSector(1, 1);
		addMouseListener(this);
	}
	
	public void setSectorView(SectorView sectorView) {
		this.sectorView = sectorView;
		sectorView.setSector(game.getSector(1, 1));
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
		Dimension topLeft = new Dimension();
		Dimension topRight = new Dimension();
		Dimension bottomLeft = new Dimension();
		Dimension bottomRight = new Dimension();
		
		topLeft.width = (selectedSector.getX() - 1) * getWidth() / 8;
		topLeft.height = (selectedSector.getY() - 1) * getHeight() / 8;
		
		topRight.width = selectedSector.getX() * getWidth() / 8;
		topRight.height = topLeft.height;
		
		bottomLeft.width = topLeft.width;
		bottomLeft.height = selectedSector.getY() * getHeight() / 8;
		
		bottomRight.width = topRight.width;
		bottomRight.height = bottomLeft.height;
		
		g.drawLine(topLeft.width, topLeft.height, topRight.width, topRight.height);
		g.drawLine(topLeft.width, topLeft.height, bottomLeft.width, bottomLeft.height);
		g.drawLine(bottomLeft.width, bottomLeft.height, bottomRight.width, bottomRight.height);
		g.drawLine(bottomRight.width, bottomRight.height, topRight.width, topRight.height);
		
		// Display information.
		g.setColor(Color.WHITE);
		for(int y = 0; y < 8; y++) {
			for(int x = 0; x < 8; x++) {
				// Calculate figures.
				int planets = game.getSector(x + 1, y + 1).getPlanets().size();
				
				int enemies = 0;
				for(Ship s : game.getSector(x + 1, y + 1).getShips())
					if(!s.getEmpire().getName().equals(game.getPlayerShip(Configuration.getInstance().getUsername()).getEmpire().getName()))
							enemies++;
				
				int friendlies = 0;
				for(Base b : game.getSector(x + 1, y + 1).getBases()) {
					if(b.getEmpire().getName().equals(game.getPlayerShip(Configuration.getInstance().getUsername()).getEmpire().getName()))
						friendlies++;
					else
						enemies++;
				}

				// Draw information to screen.
				g.drawString(Integer.toString(planets), (x * getWidth() / 8) + PADDING, (y * getHeight() / 8) + (3 * PADDING));
				g.drawString(Integer.toString(enemies), (x * getWidth() / 8) + PADDING, (y * getHeight() / 8) + (6 * PADDING));
				g.drawString(Integer.toString(friendlies), (x * getWidth() / 8) + PADDING, (y * getHeight() / 8) + (9 * PADDING));
			}
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		selectedSector.setX((e.getX() / (getWidth() / 8)) + 1);
		selectedSector.setY((e.getY() / (getHeight() / 8)) + 1);
		repaint();
		
		// Notify the sector view of the new sector selection.
		sectorView.setSector(game.getSector(selectedSector.getX(), selectedSector.getY()));
		
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
