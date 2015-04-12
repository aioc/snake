package com.ausinformatics.snake.visualisation;

import com.ausinformatics.phais.core.visualisation.VisualGameEvent;
import com.ausinformatics.snake.Position;

public class SnakeTailRemove extends VisualGameEvent {
	public int player;
	public Position p;
	
	public SnakeTailRemove(int player, Position p) {
		this.player = player;
		this.p = p;
	}
}
