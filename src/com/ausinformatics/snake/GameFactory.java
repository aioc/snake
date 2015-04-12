package com.ausinformatics.snake;

import java.util.Collections;
import java.util.List;

import com.ausinformatics.phais.core.interfaces.GameBuilder;
import com.ausinformatics.phais.core.interfaces.GameInstance;
import com.ausinformatics.phais.core.interfaces.PersistentPlayer;
import com.ausinformatics.phais.core.server.DisconnectedException;
import com.ausinformatics.phais.core.visualisation.EventBasedFrameVisualiser;
import com.ausinformatics.snake.visualisation.FrameVisualiser;
import com.ausinformatics.snake.visualisation.VisualGameState;

public class GameFactory implements GameBuilder {

	public int boardSize = 10;

	@Override
	public GameInstance createGameInstance(List<PersistentPlayer> players) {
		Collections.shuffle(players);
		for (int i = 0; i < players.size(); i++) {
			PersistentPlayer p = players.get(i);
			String toSend = "NEWGAME " + players.size() + " " + boardSize + " " + i;
			p.getConnection().sendInfo(toSend);

			try {
				String inputString = p.getConnection().getStrInput();
				String[] tokens = inputString.split("\\s");
				if (tokens.length != 1) {
					p.getConnection().disconnect();
					continue;
				} else if (!tokens[0].equals("READY")) {
					p.getConnection().disconnect();
					continue;
				}
			} catch (DisconnectedException de) {
				p.getConnection().disconnect();
			}
		}
		GameRunner gr = new GameRunner(players, boardSize);
		FrameVisualiser fv = new FrameVisualiser();
		EventBasedFrameVisualiser<VisualGameState> vis = new EventBasedFrameVisualiser<VisualGameState>(gr, fv,
				new VisualGameState(boardSize, players.size(), players));

		// Ok, we need to get the visualGameState set up, give the EBFV to the
		// gameState to report to, and to the gameRunner to know when to finish
		// Use this method
		gr.setEventVisualiser(vis);
		return vis;
	}

}
