package com.kmj.empire.server;

import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.Socket;

import org.junit.Test;

import com.kmj.empire.client.GameServiceProxy;
import com.kmj.empire.common.GameService;


public class JUnitTestServer {

	public static final String ADDRESS = "localhost";
	public static final int PORT = 8080;
	
	@Test
	public void restoreGameTest() {
		try {
			Socket socket = new Socket(ADDRESS, PORT);
			GameService gameService = new GameServiceProxy(socket);
			
			File gameFile = new File("resources/TrekUniverse.dat");
			BufferedReader br = new BufferedReader(new FileReader(gameFile));
			String line = br.readLine();
			while (line != null) {
				//gameData += 
			}
		} catch (IOException e) {
			fail("Could not connect to server.");
		}
	}

}
