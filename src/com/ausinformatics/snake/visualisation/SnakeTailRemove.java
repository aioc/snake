package com.ausinformatics.snake.visualisation;

import com.ausinformatics.snake.Position;

public class SnakeTailRemove extends TurnEvent {
	public int player;
	public Position p;

	public SnakeTailRemove(int turn, int player, Position p) {
		super(turn);
		this.player = player;
		this.p = p;
	}
}
