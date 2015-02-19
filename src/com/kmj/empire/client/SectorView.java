package com.kmj.empire.client;

import java.awt.Color;
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
import com.kmj.empire.common.Planet;
import com.kmj.empire.common.Sector;
import com.kmj.empire.common.Ship;

public class SectorView extends JPanel implements MouseListener {
	
	protected Sector sector;

	protected int sessionId;
	protected Game game;
	protected GameService server;
	protected GameWindow parent;
	protected JLabel status;
	
	protected ShipAttributeTableModel model;
	
	protected int mode;
	
	public static final int MODE_SCANNER = 0;
	public static final int MODE_NAVIGATE = 1;
	public static final int MODE_MISSILE = 2;
	
	public SectorView() {
		super();
		addMouseListener(this);
	}
	
	public SectorView(GameWindow parent, Game game, GameService server, int sessionId) {
		this();
		this.parent = parent;
		this.game = game;
		this.server = server;
		this.sessionId = sessionId;
		this.status = status;
	}
	
	public void setSector(Sector sector) {
		this.sector = sector;
		repaint();
	}
	
	public void setTableModel(ShipAttributeTableModel model) {
		this.model = model;
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
			g.drawLine(0,  i * getHeight() / 8,  getWidth(), i * getHeight() / 8);
		}
		
		// Draw sector contents.
		if(sector == null) return;
		else {
			// Draw planets.
			for(Planet p : sector.getPlanets()) {
				int x = (p.getX() - 1) * (getWidth() / 8) + (getWidth() / 8 / 2) - (getWidth() / 8 / 3 / 2);
				int y = (p.getY() - 1) * (getHeight() / 8) + (getHeight() / 8 / 2) - (getHeight() / 8 / 3 / 2);
				g.fillOval(x, y, getWidth() / 8 / 3, getHeight() / 8 / 3);
			}
			
			// Draw ships.
			String username = Configuration.getInstance().getUsername();
			// Make sure player's ship exists.
			if(game.getPlayerShip(username) == null) return;
			String playerAlliance = game.getPlayerShip(username).getType().getEmpire().getName();
			for(Ship s : sector.getShips()) {
				int x = (s.getX() - 1) * (getWidth() / 8) + (getWidth() / 8 / 2) - (getWidth() / 8 / 6 / 2);
				int y = (s.getY() - 1) * (getHeight() / 8) + (getHeight() / 8 / 2) - (getWidth() / 8 / 6 / 2);
				
				// A null owner means that the ship is AI-controlled.
				if(game.getOwner(s) != null && game.getOwner(s).equals(Configuration.getInstance().getUsername()))
					g.setColor(Color.YELLOW);
				else if(s.getType().getEmpire().getName().equals(playerAlliance))
					g.setColor(Color.GREEN);
				else
					g.setColor(Color.RED);
				g.fillRect(x, y, getWidth() / 8 / 6, getHeight() / 8 / 6);
			}
			
			// Draw bases.
			for(Base b : sector.getBases()) {
				int x = (b.getX() - 1) * (getWidth() / 8) + (getWidth() / 8 / 2) - (getWidth() / 8 / 3 / 2);
				int y = (b.getY() - 1) * (getHeight() / 8) + (getHeight() / 8 / 2) - (getHeight() / 8 / 3 / 2);
				
				if(b.getEmpire().getName().equals(playerAlliance))
					g.setColor(Color.GREEN);
				else
					g.setColor(Color.RED);
				g.fillRect(x, y, getWidth() / 8 / 3, getHeight() / 8 / 3);
			}
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		int x = (e.getX() / (getWidth() / 8)) + 1;
		int y = (e.getY() / (getHeight() / 8)) + 1;
		
		// Scanner Mode
		if(mode == MODE_SCANNER) {
			// Look for a ship in that sector.
			for(Ship s : sector.getShips()) {
				if(s.getX() == x && s.getY() == y) {
					model.setTableSource(s);
				}
			}
		}
		// Navigate Mode
		else if(mode == MODE_NAVIGATE) {
			// Send message to server.
			try {
				server.navigate(sessionId, x, y);
				parent.refresh();
			} catch (BadDestinationException b) {
				JOptionPane.showMessageDialog(this, b.getMessage(), "Navigation Error", JOptionPane.ERROR_MESSAGE);
			} catch (ConnectionFailedException c) {
				JOptionPane.showMessageDialog(this, c.getMessage(), "Connection Error", JOptionPane.ERROR_MESSAGE);
			}
			// Set the mode back to scanner mode.
			mode = MODE_SCANNER;
			status.setText("Idling");
		}
		// Missile Mode
		else if(mode == MODE_MISSILE) {
			// Sent message to server.
			try {
				Ship playerShip = game.getPlayerShip(Configuration.getInstance().getUsername());
				server.fireTorpedo(sessionId, playerShip.getSector(), x, y);
				parent.refresh();
			}
			catch(ActionException a) {
				JOptionPane.showMessageDialog(this, a.getMessage(), "Action Error", JOptionPane.ERROR_MESSAGE);
			}
			catch(ConnectionFailedException c) {
				JOptionPane.showMessageDialog(this, c.getMessage(), "Connection Error", JOptionPane.ERROR_MESSAGE);
			}
			mode = MODE_SCANNER;
			status.setText("Idling");
		}
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
