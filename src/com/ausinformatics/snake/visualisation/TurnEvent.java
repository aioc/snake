package com.ausinformatics.snake.visualisation;

import com.ausinformatics.phais.core.visualisation.VisualGameEvent;

public class TurnEvent extends VisualGameEvent {
	public int turn;
	
	public TurnEvent(int turn) {
		this.turn = turn;
	}
}
