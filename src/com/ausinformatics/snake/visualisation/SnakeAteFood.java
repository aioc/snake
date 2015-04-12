package com.ausinformatics.snake.visualisation;

import com.ausinformatics.phais.core.visualisation.VisualGameEvent;
import com.ausinformatics.snake.Position;

public class SnakeAteFood extends VisualGameEvent {
	public int player;
	
	public SnakeAteFood(int player) {
		this.player = player;
	}
}
