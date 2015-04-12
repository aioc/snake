package com.ausinformatics.snake;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.ausinformatics.phais.core.visualisation.EventBasedFrameVisualiser;
import com.ausinformatics.phais.core.visualisation.VisualGameEvent;
import com.ausinformatics.snake.visualisation.SnakeAteFood;
import com.ausinformatics.snake.visualisation.SnakeDied;
import com.ausinformatics.snake.visualisation.SnakeHeadAdd;
import com.ausinformatics.snake.visualisation.SnakeTailRemove;
import com.ausinformatics.snake.visualisation.VisualGameState;

public class GameState {

	private static int INITIAL_SIZE = 5;
	private static int TOTAL_FOOD = 3;

	public static int BLANK = 0;
	public static int FOOD = -1;

	private EventBasedFrameVisualiser<VisualGameState> vis;
	private int numPlayers;
	private int boardSize;
	private Action[] actions;
	private GamePerson[] players;
	private int board[][];
	private int curFood;
	private int tick;

	public GameState(int numPlayers, int boardSize) {
		this.numPlayers = numPlayers;
		this.boardSize = boardSize;
		actions = new Action[numPlayers];
		players = new GamePerson[numPlayers];

		Position p1 = new Position(0, 0);
		Position p2 = new Position(boardSize - 1, 0);
		Position p3 = new Position(boardSize - 1, boardSize - 1);
		Position p4 = new Position(0, boardSize - 1);
		if (numPlayers == 2) {
			players[0] = new GamePerson(p1.moveN(Position.DOWN, INITIAL_SIZE - 1), p1);
			players[1] = new GamePerson(p3.moveN(Position.UP, INITIAL_SIZE - 1), p3);
		} else if (numPlayers == 4) {
			players[0] = new GamePerson(p1.moveN(Position.DOWN, INITIAL_SIZE - 1), p1);
			players[1] = new GamePerson(p2.moveN(Position.RIGHT, INITIAL_SIZE - 1), p2);
			players[2] = new GamePerson(p3.moveN(Position.UP, INITIAL_SIZE - 1), p3);
			players[3] = new GamePerson(p4.moveN(Position.LEFT, INITIAL_SIZE - 1), p4);
		} else {
			// TODO: Handle this somehow?
		}
		board = new int[boardSize][boardSize];
		for (int i = 0; i < numPlayers; i++) {
			players[i].markArray(board, i + 1);
		}
		tick = 0;
		curFood = 0;
		repopulateFood();
	}

	public void setUpForVisualisation(EventBasedFrameVisualiser<VisualGameState> vis) {
		this.vis = vis;
		for (int i = 0; i < boardSize; i++) {
			for (int j = 0; j < boardSize; j++) {
				vis.getCurState().board[i][j] = board[i][j];
			}
		}
		for (int i = 0; i < numPlayers; i++) {
			vis.getCurState().heads[i] = players[i].getHead();
			vis.getCurState().tails[i] = players[i].getTail();
		}
	}

	public void setPlayerAction(Action a, int id) {
		actions[id] = a;
	}

	public void killPlayer(int id) {
		players[id].kill();
	}

	public void implementMoves(MoveReporter reporter) {
		Position[] addedPositions = new Position[numPlayers];
		boolean[] ateFood = new boolean[numPlayers];
		boolean[] died = new boolean[numPlayers];
		List<List<VisualGameEvent>> allEvents = new ArrayList<>();
		// Idea, go through all players, move their heads.
		for (int i = 0; i < numPlayers; i++) {
			List<VisualGameEvent> events = new ArrayList<>();
			allEvents.add(events);
			if (!players[i].isAlive())
				continue;
			Position head = players[i].addHead(actions[i].dir);
			addedPositions[i] = head;
			reporter.setHead(i, head);
			events.add(new SnakeHeadAdd(i, head));
			if (validP(head) && board[head.r][head.c] == FOOD) {
				curFood--;
				board[head.r][head.c] = BLANK;
				ateFood[i] = true;
				events.add(new SnakeAteFood(i));
			} else {
				Position tail = players[i].removeTail();
				board[tail.r][tail.c] = BLANK;
				reporter.setTail(i, tail);
				events.add(new SnakeTailRemove(i, tail));
			}
		}
		// Now go through checking for collisions.
		// If two players have the same new position, then kill both.
		for (int i = 0; i < numPlayers; i++) {
			for (int j = 0; j < numPlayers; j++) {
				if (players[i].isAlive() && players[j].isAlive()) {
					if (addedPositions[i].equals(addedPositions[j])) {
						died[i] = true;
						died[j] = true;
					}
				}
			}
		}
		for (int i = 0; i < numPlayers; i++) {
			if (!players[i].isAlive() || died[i])
				continue;
			if (!validP(addedPositions[i])) {
				died[i] = true;
			} else {
				if (board[addedPositions[i].r][addedPositions[i].c] != BLANK) {
					died[i] = true;
				} else {
					board[addedPositions[i].r][addedPositions[i].c] = i + 1;
				}
			}
		}
		for (int i = 0; i < numPlayers; i++) {
			if (died[i]) {
				players[i].kill();
				players[i].removeFromArray(board);
				reporter.killPlayer(i);
				allEvents.get(i).clear();
				allEvents.get(i).add(new SnakeDied(i));
			}
		}
		repopulateFood();
		tick++;
		List<VisualGameEvent> finalEvents = new ArrayList<>();
		for (List<VisualGameEvent> l : allEvents) {
			finalEvents.addAll(l);
		}
		vis.giveEvents(finalEvents);
	}

	public boolean gameOver() {
		int alive = 0;
		for (int i = 0; i < numPlayers; i++) {
			if (players[i].isAlive()) {
				alive++;
			}
		}
		if (alive <= 1) {
			return true;
		}
		if (tick >= 200) {
			return true;
		}
		return false;
	}

	public int getLength(int player) {
		return players[player].getLength();
	}

	private void repopulateFood() {
		while (curFood < TOTAL_FOOD) {
			Random rand = new Random();
			int fR = rand.nextInt(boardSize);
			int fC = rand.nextInt(boardSize);
			while (board[fR][fC] != BLANK) {
				fR = rand.nextInt(boardSize);
				fC = rand.nextInt(boardSize);
			}
			board[fR][fC] = FOOD;
			curFood++;
		}
	}

	private boolean validP(Position p) {
		return (p.r >= 0 && p.c >= 0 && p.r < boardSize && p.c < boardSize);
	}

}
