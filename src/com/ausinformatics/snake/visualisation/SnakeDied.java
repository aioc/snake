package com.ausinformatics.snake.visualisation;

import com.ausinformatics.phais.core.visualisation.VisualGameEvent;

public class SnakeDied extends VisualGameEvent {
	public int player;
	
	public SnakeDied(int player) {
		this.player = player;
	}
}
