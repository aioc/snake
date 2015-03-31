package com.ausinformatics.snake;

import java.util.List;

import com.ausinformatics.phais.core.interfaces.PersistentPlayer;
import com.ausinformatics.phais.core.visualisation.EventBasedFrameVisualiser;
import com.ausinformatics.snake.visualisation.VisualGameState;

public class GameState {

	private EventBasedFrameVisualiser<VisualGameState> vis;
	
	public GameState(int numPlayers, int boardSize, List<PersistentPlayer> players) {
		
	}

	public void setUpForVisualisation(EventBasedFrameVisualiser<VisualGameState> vis) {
		this.vis = vis;
	}
	
}
