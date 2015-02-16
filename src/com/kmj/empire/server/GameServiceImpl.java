package com.kmj.empire.server;

import java.util.ArrayList;
import java.util.List;

import com.kmj.empire.common.AuthenticationFailedException;
import com.kmj.empire.common.ConnectionFailedException;
import com.kmj.empire.common.Game;
import com.kmj.empire.common.GameService;

public class GameServiceImpl implements GameService {

	@Override
	public int restoreGame(Game game) {
		
		return 0;
	}

	@Override
	public Game getGameState(int gameId) {
		
		return null;
	}

	@Override
	public int authenticate(String user, String password) {
		
		return 0;
	}

	@Override
	public int createGame() {
		
		return 0;
	}

	@Override
	public void joinGame() {
	}

	@Override
	public ArrayList<Game> getGamesList(int sessionId)
			throws AuthenticationFailedException, ConnectionFailedException {
		return null;
	}

}
