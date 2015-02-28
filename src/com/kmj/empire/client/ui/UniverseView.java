package com.kmj.empire.client.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.kmj.empire.client.controller.Configuration;
import com.kmj.empire.client.controller.Session;
import com.kmj.empire.client.controller.SessionObserver;
import com.kmj.empire.common.Base;
import com.kmj.empire.common.Game;
import com.kmj.empire.common.Ship;
import com.kmj.empire.common.exceptions.ActionException;
import com.kmj.empire.common.exceptions.BadDestinationException;
import com.kmj.empire.common.exceptions.ConnectionFailedException;

/*
 * The view that is presented to the user that allows them to
 * get a summary of the contents of each sector.
 */
public class UniverseView extends JPanel implements SessionObserver, MouseListener {
	
	private static final long serialVersionUID = -757496235041650510L;
	
	protected int selectedSectorX;
	protected int selectedSectorY;

	protected JLabel status;
	protected GameWindow parent;
	protected SectorView sectorView;
	
	protected static final int PADDING = 5;
	
	// The mode of the UniverseView decides what actions the view
	// will take when an element is selected.
	protected int mode;
	
	/*
	 * UNIVERSE VIEW MODES
	 * ===================
	 * MODE_SCANNER		Enables the player to view the contents of another sector.
	 * MODE_WARP		Enables the player to move to another sector.
	 */
	protected static final int MODE_SCANNER = 0;
	protected static final int MODE_WARP = 1;

	public UniverseView() {
		super();
		selectedSectorX = selectedSectorY = 1;
		mode = MODE_SCANNER;
		addMouseListener(this);
		Session.getInstance().addObserver(this);
	}
	
	public UniverseView(GameWindow parent) {
		super();
		this.parent = parent;
		selectedSectorX = selectedSectorY = 1;
		mode = MODE_SCANNER;
		addMouseListener(this);
		Session.getInstance().addObserver(this);
		
		// Focus on sector player is in.
		Ship ship = Session.getInstance().getGame().getPlayerShip(Configuration.getInstance().getUsername());
		selectedSectorX = ship.getSector().getX();
		selectedSectorY = ship.getSector().getY();
	}
	
	public void setSectorView(SectorView sectorView) {
		this.sectorView = sectorView;
		sectorView.setSector(Session.getInstance().getGame().getSector(selectedSectorX, selectedSectorY));
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
		
		// Quit at this point if the player is dead.
		if(Session.getInstance().isGameOver()) return;
		
		// Display information.
		g.setColor(Color.WHITE);
		for(int y = 0; y < 8; y++) {
			for(int x = 0; x < 8; x++) {
				Game game = Session.getInstance().getGame();
				
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
	public void onIdChanged(int newId) {
		
	}
	
	@Override
	public void onGameChanged(Game newGame) {
		repaint();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		Game game = Session.getInstance().getGame();
		selectedSectorX = (e.getX() / (getWidth() / 8)) + 1;
		selectedSectorY = (e.getY() / (getHeight() / 8)) + 1;
		
		// Notify the sector view of the new sector selection.
		if(mode == MODE_SCANNER) {
			sectorView.setSector(game.getSector(selectedSectorX, selectedSectorY));
			repaint();
		}
		
		// Move to a new sector.
		if(mode == MODE_WARP) {
			try {
				Session.getInstance().warp(game.getSector(selectedSectorX, selectedSectorY));
			} catch (BadDestinationException b) {
				JOptionPane.showMessageDialog(this, b.getMessage(), "Warp Error", JOptionPane.ERROR_MESSAGE);
			} catch (ConnectionFailedException c) {
				JOptionPane.showMessageDialog(this, c.getMessage(), "Connection Error", JOptionPane.ERROR_MESSAGE);
			} catch (ActionException a) {
				JOptionPane.showMessageDialog(this, a.getMessage(), "Action Error", JOptionPane.ERROR_MESSAGE);
			}
			sectorView.setSector(game.getSector(selectedSectorX, selectedSectorY));
			mode = MODE_SCANNER;
			status.setText("Idling");
		}
		
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
