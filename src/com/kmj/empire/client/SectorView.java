package com.kmj.empire.client;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;

import com.kmj.empire.common.Planet;
import com.kmj.empire.common.Sector;

public class SectorView extends JPanel implements MouseListener {
	
	protected Sector sector;
	
	public SectorView() {
		super();
		addMouseListener(this);
	}
	
	public void setSector(Sector sector) {
		System.out.println("Setting sector view.");
		this.sector = sector;
		repaint();
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
			g.drawLine(0,  i * getHeight() / 8,  getWidth(), i * getHeight() / 8);
		}
		
		// Draw sector contents.
		if(sector == null) return;
		else {
			// Draw planets.
			for(Planet p : sector.getPlanets()) {
				int x = (p.getLocation().x - 1) * (getWidth() / 8) + (getWidth() / 8 / 2) - (getWidth() / 8 / 3 / 2);
				int y = (p.getLocation().y - 1) * (getHeight() / 8) + (getHeight() / 8 / 2) - (getHeight() / 8 / 3 / 2);
				g.fillOval(x, y, getWidth() / 8 / 3, getHeight() / 8 / 3);
			}
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}
}
