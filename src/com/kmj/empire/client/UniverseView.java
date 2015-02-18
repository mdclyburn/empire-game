package com.kmj.empire.client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.kmj.empire.common.BadDestinationException;
import com.kmj.empire.common.Base;
import com.kmj.empire.common.ConnectionFailedException;
import com.kmj.empire.common.Game;
import com.kmj.empire.common.GameService;
import com.kmj.empire.common.Ship;

public class UniverseView extends JPanel implements MouseListener {
	
	protected Game game;
	protected GameService server;
	protected GameWindow parent;
	protected int sessionId;
	protected int selectedSectorX;
	protected int selectedSectorY;
	protected SectorView sectorView;
	protected JLabel status;
	
	protected int mode;
	
	protected static final int PADDING = 5;
	
	protected static final int MODE_SCANNER = 0;
	protected static final int MODE_WARP = 1;

	public UniverseView() {
		super();
		selectedSectorX = selectedSectorY = 1;
		addMouseListener(this);
		mode = MODE_SCANNER;
	}
	
	public UniverseView(GameWindow parent, Game game, GameService server, int sessionId) {
		super();
		this.parent = parent;
		this.game = game;
		this.server = server;
		this.sessionId = sessionId;
		selectedSectorX = selectedSectorY = 1;
		addMouseListener(this);
		
		// Focus on sector player is in.
		Ship ship = game.getPlayerShip(Configuration.getInstance().getUsername());
		selectedSectorX = ship.getSector().getX();
		selectedSectorY = ship.getSector().getY();
	}
	
	public void setSectorView(SectorView sectorView) {
		this.sectorView = sectorView;
		sectorView.setSector(game.getSector(selectedSectorX, selectedSectorY));
	}
	
	public void setMode(int mode) {
		this.mode = mode;
	}
	
	public void setStatus(JLabel status) {
		this.status = status;
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
		
		topLeft.width = (selectedSectorX - 1) * getWidth() / 8;
		topLeft.height = (selectedSectorY - 1) * getHeight() / 8;
		
		topRight.width = selectedSectorX * getWidth() / 8;
		topRight.height = topLeft.height;
		
		bottomLeft.width = topLeft.width;
		bottomLeft.height = selectedSectorY * getHeight() / 8;
		
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
					if(!s.getType().getEmpire().getName().equals(game.getPlayerShip(Configuration.getInstance().getUsername()).getType().getEmpire().getName()))
							enemies++;
				
				int friendlies = 0;
				for(Base b : game.getSector(x + 1, y + 1).getBases()) {
					if(b.getEmpire().getName().equals(game.getPlayerShip(Configuration.getInstance().getUsername()).getType().getEmpire().getName()))
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
		selectedSectorX = (e.getX() / (getWidth() / 8)) + 1;
		selectedSectorY = (e.getY() / (getHeight() / 8)) + 1;
		
		// Notify the sector view of the new sector selection.
		if(mode == MODE_SCANNER) {
			sectorView.setSector(game.getSector(selectedSectorX, selectedSectorY));
		}
		
		// Move to a new sector.
		if(mode == MODE_WARP) {
			try {
				server.warp(sessionId, game.getSector(selectedSectorX, selectedSectorY));
			} catch (BadDestinationException b) {
				JOptionPane.showMessageDialog(this, b.getMessage(), "Warp Error", JOptionPane.ERROR_MESSAGE);
			} catch (ConnectionFailedException c) {
				JOptionPane.showMessageDialog(this, c.getMessage(), "Connection Error", JOptionPane.ERROR_MESSAGE);
			}
			sectorView.setSector(game.getSector(selectedSectorX, selectedSectorY));
			mode = MODE_SCANNER;
			status.setText("Idling");
		}
		parent.refresh();
		
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
