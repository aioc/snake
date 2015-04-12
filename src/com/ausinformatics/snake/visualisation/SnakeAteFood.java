package com.ausinformatics.snake.visualisation;

import com.ausinformatics.snake.Position;

public class SnakeAteFood extends TurnEvent {
	public int player;
	public Position p;

	public SnakeAteFood(int turn, int player, Position p) {
		super(turn);
		this.player = player;
		this.p = p;
	}
}
