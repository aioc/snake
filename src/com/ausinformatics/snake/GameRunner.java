package com.ausinformatics.snake;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ausinformatics.phais.core.interfaces.PersistentPlayer;
import com.ausinformatics.phais.core.server.ClientConnection;
import com.ausinformatics.phais.core.server.DisconnectedException;
import com.ausinformatics.phais.core.visualisation.EndGameEvent;
import com.ausinformatics.phais.core.visualisation.EventBasedFrameVisualiser;
import com.ausinformatics.phais.core.visualisation.GameHandler;
import com.ausinformatics.snake.visualisation.SnakeWinnerEvent;
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

	private boolean isFinished(int playerIndex) {
		return results.containsKey(players.get(playerIndex));
	}

	private void killPlayers(List<Integer> toKill) {
		for (Integer i : toKill) {
			finalRanks[i] = players.size() - results.size() - toKill.size() + 1;
		}
		for (Integer i : toKill) {
			results.put(players.get(i), getReward(finalRanks[i] - 1));
		}
	}

	private void killPlayer(int toKill) {
		killPlayers(Arrays.asList(toKill));
	}

	@Override
	public void begin() {
		for (int i = 0; i < players.size(); i++) {
			state.sendInitial(players.get(i).getConnection());
		}
		while (results.size() < players.size() - 1 && !state.gameOver()) {
			for (int i = 0; i < players.size(); i++) {
				PersistentPlayer p = players.get(i);
				if (!p.getConnection().isConnected() && !isFinished(i)) {
					state.killPlayer(i);
					killPlayer(i);
					continue;
				}
				ClientConnection connection = p.getConnection();
				p.getConnection().sendInfo("YOURMOVE");
				boolean playerDied = false;
				try {
					Action a = Action.getAction(connection);
					state.setPlayerAction(a, i);
				} catch (DisconnectedException ex) {
					playerDied = true;
				} catch (BadProtocolException ex) {
					connection.sendInfo("BADPROT Invalid action. " + ex.getExtraInfo());
					playerDied = true;
				}
				if (playerDied) {
					state.killPlayer(i);
					killPlayer(i);
				}
			}
			MoveReporter reporter = new MoveReporter(players.size());
			state.implementMoves(reporter);
			for (int i = 0; i < players.size(); i++) {
				reporter.sendToPlayer(players.get(i).getConnection());
			}
			for (int i = 0; i < players.size(); i++) {
				if (reporter.died(i)) {
					killPlayer(i);
				}
			}
		}
		while (results.size() < players.size()) {
			int minV = -1;
			List<Integer> curMin = null;
			for (int i = 0; i < players.size(); i++) {
				if (!isFinished(i) && (minV == -1 || (state.getLength(i) < state.getLength(minV)))) {
					minV = i;
					curMin = new ArrayList<Integer>();
					curMin.add(i);
				} else if (!isFinished(i) && (minV == -1 || (state.getLength(i) == state.getLength(minV)))) {
					curMin.add(i);
				}
			}
			killPlayers(curMin);
		}
		int amoWinners = 0;
		String name = "";
		for (int i = 0; i < players.size(); i++) {
			players.get(i).getConnection().sendInfo("GAMEOVER " + finalRanks[i]);
			if (finalRanks[i] == 1) {
				amoWinners++;
				name = players.get(i).getName();
			}
		}
		if (amoWinners > 1) {
			name = "";
		}
		vis.giveEvent(new SnakeWinnerEvent(100000, name));
		vis.giveEvent(new EndGameEvent());
		int round = 0;
		while (!vis.finishedVisualising() && vis.isVisualising() && round < 100) {
			try {
				round++;
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (vis.isVisualising()) {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	@Override
	public Map<PersistentPlayer, Integer> getResults() {
		return results;
	}

	public int getReward(int pos) {
		return 1 + (players.size() - pos - 1) * 10000;
	}

}
