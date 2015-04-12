package com.ausinformatics.snake.visualisation;

public class SnakeAteFood extends TurnEvent {
	public int player;

	public SnakeAteFood(int turn, int player) {
		super(turn);
		this.player = player;
	}
}
