package com.kmj.empire.server;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.Socket;

import org.junit.Test;

import com.kmj.empire.client.controller.GameServiceProxy;
import com.kmj.empire.common.ConnectionFailedException;
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
			
			
			
		} catch (IOException e) {
			fail("Could not connect to server.");
		} catch (ConnectionFailedException e) {
			fail("Could not connect to server.");
		}
	}

}
