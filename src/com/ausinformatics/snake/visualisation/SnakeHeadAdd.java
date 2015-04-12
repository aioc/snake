package com.ausinformatics.snake.visualisation;

import com.ausinformatics.snake.Position;

public class SnakeHeadAdd extends TurnEvent {
	public int player;
	public Position p;

	public SnakeHeadAdd(int turn, int player, Position p) {
		super(turn);
		this.player = player;
		this.p = p;
	}
}
