package com.kmj.empire.server;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.Socket;

import org.junit.Test;

import com.kmj.empire.client.GameServiceProxy;
import com.kmj.empire.common.ConnectionFailedException;
import com.kmj.empire.common.Game;
import com.kmj.empire.common.GameService;
import com.kmj.empire.common.InvalidGameFileException;


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
			
			fail();
			//get game state to check if restore successful
			Game game = gameService.getGameState(gameId);
			
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
			assertTrue("Check weapon PHAS", game.getUniverse().getWeapon("ATOR").getMaxYield() == 10000);
			assertTrue("Check weapon PHAS", game.getUniverse().getWeapon("PTOR").isMissleWeapon());
			
			
			
		} catch (IOException e) {
			fail("Could not connect to server.");
		} catch (ConnectionFailedException e) {
			fail("Could not connect to server.");
		}
	}

}
