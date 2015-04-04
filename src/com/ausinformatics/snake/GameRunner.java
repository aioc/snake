package com.ausinformatics.snake;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ausinformatics.phais.core.interfaces.PersistentPlayer;
import com.ausinformatics.phais.core.visualisation.EventBasedFrameVisualiser;
import com.ausinformatics.phais.core.visualisation.GameHandler;
import com.ausinformatics.snake.visualisation.VisualGameState;

public class GameRunner implements GameHandler {
	
	private GameState state;
	private List<PersistentPlayer> players;
	private Map<PersistentPlayer, Integer> results;
	private EventBasedFrameVisualiser<VisualGameState> vis;

	private int[] finalRanks;
	
	public GameRunner(List<PersistentPlayer> players, int boardSize) {
		this.players = players;
		results = new HashMap<PersistentPlayer, Integer>();
		finalRanks = new int[players.size()];
		state = new GameState(players.size(), boardSize);
	}
	
	public void setEventVisualiser(EventBasedFrameVisualiser<VisualGameState> vis) {
		this.vis = vis;
		state.setUpForVisualisation(vis);
	}

	@Override
	public void begin() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Map<PersistentPlayer, Integer> getResults() {
		// TODO Auto-generated method stub
		return null;
	}

}
