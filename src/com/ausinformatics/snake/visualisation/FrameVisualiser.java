package com.ausinformatics.snake.visualisation;

import java.awt.Graphics2D;
import java.util.List;

import com.ausinformatics.phais.core.visualisation.FrameVisualisationHandler;
import com.ausinformatics.phais.core.visualisation.VisualGameEvent;
import com.ausinformatics.snake.GameState;
import com.ausinformatics.snake.Position;

public class FrameVisualiser implements FrameVisualisationHandler<VisualGameState> {

	@Override
	public void generateBackground(VisualGameState state, int sWidth, int sHeight, Graphics2D g) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void generateState(VisualGameState state, int sWidth, int sHeight, Graphics2D g) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void eventCreated(VisualGameEvent e) {
		if (e instanceof SnakeHeadAdd || e instanceof SnakeTailRemove || e instanceof SnakeDied) {
			e.totalFrames = 10;
		}
		if (e instanceof SnakeAteFood) {
			e.totalFrames = 30;
		}
		if (e instanceof SnakeWinnerEvent) {
			e.totalFrames = 60;
		}
	}

	@Override
	public void animateEvents(VisualGameState state, List<VisualGameEvent> events, int sWidth, int sHeight, Graphics2D g) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void eventEnded(VisualGameEvent e, VisualGameState state) {
		if (e instanceof SnakeHeadAdd) {
			int player = ((SnakeTailRemove) e).player;
			Position p = ((SnakeTailRemove) e).p;
			state.board[p.r][p.c] = player + 1;
		}
		if (e instanceof SnakeTailRemove ) {
			int player = ((SnakeTailRemove) e).player;
			Position p = ((SnakeTailRemove) e).p;
			if (state.board[p.r][p.c] == player + 1) {
				state.board[p.r][p.c] = GameState.BLANK;
			}
		}
		if (e instanceof SnakeDied) {
			int player = ((SnakeDied) e).player;
			for (int i = 0; i < state.boardSize; i++) {
				for (int j = 0; j < state.boardSize; j++) {
					if (state.board[i][j] == player + 1) {
						state.board[i][j] = GameState.BLANK;
					}
				}
			}
		}
		if (e instanceof SnakeAteFood) {
			state.foodEaten[((SnakeAteFood) e).player]++;
		}
		if (e instanceof SnakeWinnerEvent) {
			state.winner = ((SnakeWinnerEvent) e).playerName;
		}
	}

}
