package com.ausinformatics.snake.visualisation;

import com.ausinformatics.phais.core.visualisation.VisualGameEvent;

public class SnakeAteFood extends VisualGameEvent {
	public int player;
	
	public SnakeAteFood(int player) {
		this.player = player;
	}
}
