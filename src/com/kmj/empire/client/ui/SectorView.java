package com.kmj.empire.client.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.kmj.empire.client.controller.Configuration;
import com.kmj.empire.client.controller.Session;
import com.kmj.empire.client.controller.SessionObserver;
import com.kmj.empire.client.ui.model.ShipAttributeTableModel;
import com.kmj.empire.common.Base;
import com.kmj.empire.common.Game;
import com.kmj.empire.common.Planet;
import com.kmj.empire.common.Sector;
import com.kmj.empire.common.Ship;
import com.kmj.empire.common.exceptions.ActionException;
import com.kmj.empire.common.exceptions.BadDestinationException;
import com.kmj.empire.common.exceptions.ConnectionFailedException;

/*
 * The view presented to the user that shows the user
 * the currently selected sector (one they have chosen).
 * It's initial sector is the sector the user is currently
 * in.
 */
public class SectorView extends JPanel implements SessionObserver, MouseListener {
	
	private static final long serialVersionUID = 5870003383765058290L;

	protected Sector sector;

	protected GameWindow parent;
	protected JLabel status;
	
	protected ShipAttributeTableModel model;
	
	// The mode the sector view is currently in. This decides
	// what actions are to be taken when the player selects
	// something inside the view.
	protected int mode;
	
	/*
	 * SECTOR VIEW MODES
	 * =================
	 * MODE_SCANNER		Enables the player to get information about enties in a sector.
	 * MODE_NAVIGATE	Enables the player to move around the sector.
	 * MODE_MISSILE		Enables the player to shoot a missile at a target.
	 */
	public static final int MODE_SCANNER = 0;
	public static final int MODE_NAVIGATE = 1;
	public static final int MODE_MISSILE = 2;
	
	public SectorView() {
		super();
		addMouseListener(this);
		Session.getInstance().addObserver(this);
	}
	
	public SectorView(GameWindow parent) {
		this();
		this.parent = parent;
	}
	
	// Setter Functions
	
	// Sets the sector for displaying. Afterwards, the view will
	// repaint itself to show the new view.
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
	
	// Custom painting method to show the user the sector.
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
		Game game = Session.getInstance().getGame();
		sector = game.getSector(sector.getX(), sector.getY());
		if(sector == null) return;
		else {
			// Draw planets.
			for(Planet p : sector.getPlanets()) {
				int x = (p.getX() - 1) * (getWidth() / 8) + (getWidth() / 8 / 2) - (getWidth() / 8 / 3 / 2);
				int y = (p.getY() - 1) * (getHeight() / 8) + (getHeight() / 8 / 2) - (getHeight() / 8 / 3 / 2);
				g.fillOval(x, y, getWidth() / 8 / 3, getHeight() / 8 / 3);
			}
			
			// Bail if game is over.
			if(Session.getInstance().isGameOver()) return;
			
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
	public void onIdChanged(int newId) {
		
	}
	
	// When the Game changes, the sector needs to be able
	// to repaint itself on cue.
	@Override
	public void onGameChanged(Game newGame) {
		repaint();
	}

	// Function called when the user clicks inside the
	// sector view. The action performed depends on the
	// 'mode' the sector view is in.
	@Override
	public void mouseClicked(MouseEvent e) {
		int x = (e.getX() / (getWidth() / 8)) + 1;
		int y = (e.getY() / (getHeight() / 8)) + 1;
		
		Game game = Session.getInstance().getGame();
		
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
				Session.getInstance().navigate(x, y);
			} catch (BadDestinationException b) {
				JOptionPane.showMessageDialog(this, b.getMessage(), "Navigation Error", JOptionPane.ERROR_MESSAGE);
			} catch (ConnectionFailedException c) {
				JOptionPane.showMessageDialog(this, c.getMessage(), "Connection Error", JOptionPane.ERROR_MESSAGE);
			} catch (ActionException a) {
				JOptionPane.showMessageDialog(this, a.getMessage(), "Action Error", JOptionPane.ERROR_MESSAGE);
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
				Session.getInstance().fireTorpedo(playerShip.getSector(), x, y);
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

	// Unused Functions =======================
	@Override
	public void mousePressed(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}
}
