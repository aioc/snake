package com.ausinformatics.snake.visualisation;

import com.ausinformatics.snake.Position;

public class SnakeFoodAdd extends TurnEvent {

	public Position p;
	
	public SnakeFoodAdd(int turn, Position p) {
		super(turn);
		this.p = p;
	}

}
