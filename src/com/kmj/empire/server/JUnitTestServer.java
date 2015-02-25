package com.kmj.empire.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.Socket;

import org.junit.Test;

import com.kmj.empire.client.GameServiceProxy;
import com.kmj.empire.common.Game;
import com.kmj.empire.common.GameService;
import com.kmj.empire.common.GameState;
import com.kmj.empire.common.exceptions.ConnectionFailedException;
import com.kmj.empire.common.exceptions.InvalidGameFileException;


public class JUnitTestServer {

	public static final String ADDRESS = "localhost";
	public static final int PORT = 8080;
	
	@Test
	public void restoreGameTest() {
		try {
			Socket socket = new Socket(ADDRESS, PORT);
			GameService gameService = new GameServiceProxy(socket);
			
			//convert file to string
			File gameFile = new File("resources/TrekUniverse.dat");
			BufferedReader br = new BufferedReader(new FileReader(gameFile));
			String gameData = "";
			String line = br.readLine();
			while (line != null) {
				gameData += line + "\n";
				line = br.readLine();
			}
			br.close();
			
			int gameId = -100;
			try {
				gameId = gameService.restoreGame(gameData);
			} catch (InvalidGameFileException e) {
				fail("Vaild game file threw invalid game file exception");
			}
			assertFalse("GameId is not -100;", gameId == -100);
			assertFalse("GameId did not return error", gameId == -1);
			
			//get game state to check if restore successful
			GameState gameState = gameService.getGameState(gameId);
			Game game = new Game();
			gameState.toGame(game);
			
			//check header
			assertEquals("Check game id", game.getId(), gameId);
			assertEquals("Check title", game.getName(), "Star Trek Forever");
			assertEquals("Check stardate", game.getStardate(), 2236);
			
			//check empires
			assertEquals("Check empires exists", game.getUniverse().getEmpireList().size(), 4);
			assertTrue("Check empire BAJ", game.getUniverse().getEmpire("Bajoran").getId().equals("BAJ"));
			assertTrue("Check empire BAJ", game.getUniverse().getEmpire("Bajoran").isExploration());
			
			//check weapons
			assertEquals("Check weapons exists", game.getUniverse().getWeaponTypes().size(), 5);
			assertTrue("Check weapon PHAS", game.getUniverse().getWeapon("Phaser").getId().equals("PHAS"));
			assertTrue("Check weapon ATOR", game.getUniverse().getWeapon("ATOR").getMaxYield() == 10000);
			assertTrue("Check weapon PTOR", game.getUniverse().getWeapon("PTOR").isMissleWeapon());
			
			//check shiptypes
			assertEquals("Check shiptype", game.getUniverse().getEmpire("FED").getShipTypes().size(), 2);
			assertEquals("Check ship BOP", game.getUniverse().getEmpire("Klingon").getShip("BOP").getMaxShield(), 600);
			assertTrue("Check ship Cruiser", game.getUniverse().getEmpire("CAR").getShip("Cruiser").getEnergyWeapon().isEnergyWeapon());

			//check bases
			assertEquals("Check bases", game.getBases().size(), 3);
			
			//check ships
			assertEquals("Check ships", game.getShips().size(), 4);
			assertTrue("Check ships", game.getIdShip(2).getType().getId().equals("STM"));
			
			//check players
			assertEquals("Check players", game.getActivePlayers().size(), 2);
			assertTrue("Check player 0", game.getActivePlayers().get(0).getEmpire().getId().equals("FED"));
			assertEquals("Check player 0", game.getActivePlayers().get(1).getShip(), game.getIdShip(1));
			
			
		} catch (IOException e) {
			fail("Could not connect to server.");
		} catch (ConnectionFailedException e) {
			fail("Could not connect to server.");
		}
	}

}
