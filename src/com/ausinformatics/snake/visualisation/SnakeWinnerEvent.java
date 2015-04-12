package com.ausinformatics.snake.visualisation;

import com.ausinformatics.phais.core.visualisation.VisualGameEvent;

public class SnakeWinnerEvent extends VisualGameEvent {
	public String playerName;

	public SnakeWinnerEvent(String name) {
		playerName = name;
	}
}
