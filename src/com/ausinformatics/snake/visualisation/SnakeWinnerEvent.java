package com.ausinformatics.snake.visualisation;

public class SnakeWinnerEvent extends TurnEvent {
	public String playerName;

	public SnakeWinnerEvent(int turn, String name) {
		super(turn);
		playerName = name;
	}
}
