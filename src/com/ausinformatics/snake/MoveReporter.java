package com.ausinformatics.snake;

import java.util.ArrayList;
import java.util.List;

import com.ausinformatics.phais.core.server.ClientConnection;

public class MoveReporter {

	private int numPlayers;
	private String[] updateMoves;
	private boolean[] died;
	private List<String> foodAdds;

	public MoveReporter(int numPlayers) {
		updateMoves = new String[numPlayers];
		died = new boolean[numPlayers];
		foodAdds = new ArrayList<>();
		this.numPlayers = numPlayers;
	}

	public void setHead(int player, Position p) {
		updateMoves[player] = "MOVED " + player + " " + p.r + " " + p.c;
	}

	public void setTail(int player, Position p) {
		updateMoves[player] += " " + p.r + " " + p.c;
	}

	public void killPlayer(int player) {
		updateMoves[player] = "DIED " + player;
	}

	public void foodAdd(Position p) {
		foodAdds.add("FOOD " + p.r + " " + p.c);
	}

	public void sendToPlayer(ClientConnection c) {
		for (int i = 0; i < numPlayers; i++) {
			if (updateMoves[i] != null) {
				c.sendInfo(updateMoves[i]);
			}
		}
		for (String s : foodAdds) {
			c.sendInfo(s);
		}
	}
	
	public boolean died(int player) {
		return died[player];
	}
}
