package com.ausinformatics.snake.visualisation;

import com.ausinformatics.phais.core.visualisation.VisualGameEvent;
import com.ausinformatics.snake.Position;

public class SnakeDied extends VisualGameEvent {
	public int player;
	public Position p;
	
	public SnakeDied(int player, Position p) {
		this.player = player;
		this.p = p;
	}
}
