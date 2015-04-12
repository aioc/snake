package com.ausinformatics.snake.visualisation;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import com.ausinformatics.phais.core.visualisation.FrameVisualisationHandler;
import com.ausinformatics.phais.core.visualisation.VisualGameEvent;
import com.ausinformatics.snake.Position;

public class FrameVisualiser implements FrameVisualisationHandler<VisualGameState> {

	private static final int LARGE_BORDER = 10;
	private static final int SMALL_BORDER = 3;
	private static final int TURN_FRAMES = 3;
	private static final int DIED_FRAMES = 30;
	private Box boardBox;
	private Box statsBox;
	private Box[][] boardBoxes;
	private Box titleBox;
	private Box[] playerBoxes;
	// We keep track of whether to render or not, which will occur if the screen
	// is too small.
	private boolean render;

	@Override
	public void generateBackground(VisualGameState state, int sWidth, int sHeight, Graphics2D g) {
		render = false;
		BoxFactory f = new BoxFactory(sWidth, sHeight);
		int width = sWidth - 2 * LARGE_BORDER;
		int height = sHeight - 2 * LARGE_BORDER;
		// Divide into two parts: The board, and the stats.
		// Board takes at most 2/3rds of the width.
		int boardWidth = 2 * (width - LARGE_BORDER) / 3;
		int boardSize = Math.min(height, boardWidth);
		if (boardSize < 5 * state.boardSize) {
			return;
		}
		// Round down to the nearest multiple of state.boardSize;
		boardSize -= (boardSize % state.boardSize);
		boardBox = f.fromDimensions(LARGE_BORDER, LARGE_BORDER, boardSize, boardSize);
		// Take up a lot of space with the stats.
		statsBox = f.fromPoints(boardBox.right + LARGE_BORDER, LARGE_BORDER, LARGE_BORDER, LARGE_BORDER);
		// Adjust these based of whether it is valid or not.
		if (statsBox.width < 70 || statsBox.height < state.numPlayers * 10 + 20 || statsBox.height < 100) {
			return;
		}
		// We *should* have enough space, so lets define all the rest of the
		// boxes.
		int boardN = state.boardSize;
		boardBoxes = new Box[boardN][boardN];
		int squareSize = boardBox.height / boardN;
		// We either have a 1px border, or a 2px border.
		int border = squareSize > 10 ? 2 : 1;
		for (int i = 0; i < boardN; i++) {
			for (int j = 0; j < boardN; j++) {
				boardBoxes[i][j] = f.fromDimensions(boardBox.left + j * squareSize, boardBox.top + i * squareSize,
						squareSize - border, squareSize - border);
			}
		}
		titleBox = f.fromMixedHeight(statsBox.left + SMALL_BORDER, statsBox.top + SMALL_BORDER, statsBox.right
				- SMALL_BORDER, statsBox.height / 10);
		playerBoxes = new Box[state.numPlayers];
		int playerHeight = (statsBox.height - titleBox.height - LARGE_BORDER) / state.numPlayers;
		if (playerHeight < 10) {
			return;
		}
		for (int i = 0; i < state.numPlayers; i++) {
			playerBoxes[i] = f.fromMixedHeight(statsBox.left + SMALL_BORDER, titleBox.bottom + LARGE_BORDER + i
					* playerHeight, statsBox.right - SMALL_BORDER, playerHeight - SMALL_BORDER);
		}
		// Everything is defined. We start rendering.
		render = true;
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, sWidth, sHeight);
		g.setColor(Color.WHITE);
		for (int i = 0; i < boardN; i++) {
			for (int j = 0; j < boardN; j++) {
				boardBoxes[i][j].fill(g);
			}
		}
		g.setColor(Color.LIGHT_GRAY);
		titleBox.fill(g);
		for (int i = 0; i < state.numPlayers; i++) {
			playerBoxes[i].fill(g);
		}
	}

	@Override
	public void generateState(VisualGameState state, int sWidth, int sHeight, Graphics2D g) {
		if (!render)
			return;
		BoxFactory f = new BoxFactory(sWidth, sHeight);
		int boxSize = boardBoxes[0][0].width;
		g.setColor(Color.GREEN);
		for (Position p : state.food) {
			Box b = f.fromDimensions(boardBoxes[p.r][p.c].left + 3, boardBoxes[p.r][p.c].top + 3, boxSize - 3,
					boxSize - 3);
			b.fill(g);
		}
		for (int i = 0; i < state.numPlayers; i++) {
			if (state.isDead[i])
				continue;
			Color c = state.colours[i];
			g.setColor(c);
			drawSnake(f, g, state.blocks.get(i));
		}
		String text = "Snake : " + state.curTurn;
		drawString(g, titleBox, text, Color.BLACK);
	}

	@Override
	public void eventCreated(VisualGameEvent e) {
		e.totalFrames = TURN_FRAMES;
		if (e instanceof SnakeDied) {
			e.totalFrames = DIED_FRAMES;
		}
		if (e instanceof SnakeAteFood) {
			e.totalFrames = 6;
		}
		if (e instanceof SnakeWinnerEvent) {
			e.totalFrames = 60;
		}
	}

	@Override
	public void animateEvents(VisualGameState state, List<VisualGameEvent> events, int sWidth, int sHeight, Graphics2D g) {
		if (!render)
			return;
		BoxFactory f = new BoxFactory(sWidth, sHeight);
		// Make sure to set up events properly.
		int best = -1;
		for (VisualGameEvent e : events) {
			if (e instanceof TurnEvent) {
				TurnEvent te = (TurnEvent) e;
				if (best == -1 || te.turn < best) {
					best = te.turn;
				}
			}
		}
		List<VisualGameEvent> curEvents = new ArrayList<VisualGameEvent>();
		for (VisualGameEvent e : events) {
			if (e instanceof TurnEvent) {
				TurnEvent te = (TurnEvent) e;
				if (te.turn != best) {
					te.curFrame = 0;
				} else {
					curEvents.add(e);
				}
			}
		}
		// Now we render the tails.
		int[] tailFrame = new int[state.numPlayers];
		SnakeHeadAdd[] headEvents = new SnakeHeadAdd[state.numPlayers];
		SnakeDied[] dead = new SnakeDied[state.numPlayers];
		for (VisualGameEvent e : curEvents) {
			if (e instanceof SnakeTailRemove) {
				tailFrame[((SnakeTailRemove) e).player] = e.curFrame;
			}
			if (e instanceof SnakeHeadAdd) {
				headEvents[((SnakeHeadAdd) e).player] = (SnakeHeadAdd) e;
			}
			if (e instanceof SnakeDied) {
				dead[((SnakeDied) e).player] = (SnakeDied) e;
			}
		}

		for (int i = 0; i < state.numPlayers; i++) {
			if (state.isDead[i]) {
				continue;
			}
			Color c = state.colours[i];
			g.setColor(c);
			if (dead[i] != null) {
				if (dead[i].curFrame % 4 <= 1) {
					c = Color.BLACK;
				}
				g.setColor(c);
				drawSnake(f, g, state.blocks.get(i));
			}
			Position tail = state.blocks.get(i).removeLast();
			Position second = state.blocks.get(i).getLast();
			state.blocks.get(i).addLast(tail);
			tweenMovement(f, tail, second, tailFrame[i], g);
			Position curHead = state.blocks.get(i).getFirst();
			if (headEvents[i] != null) {
				tweenMovement(f, headEvents[i].p, curHead, TURN_FRAMES - headEvents[i].curFrame, g);
			}
		}
	}

	@Override
	public void eventEnded(VisualGameEvent e, VisualGameState state) {
		if (e instanceof SnakeHeadAdd) {
			int player = ((SnakeHeadAdd) e).player;
			Position p = ((SnakeHeadAdd) e).p;
			state.blocks.get(player).addFirst(p);
		}
		if (e instanceof SnakeTailRemove) {
			int player = ((SnakeTailRemove) e).player;
			state.blocks.get(player).removeLast();
		}
		if (e instanceof SnakeDied) {
			int player = ((SnakeDied) e).player;
			state.isDead[player] = true;
		}
		if (e instanceof SnakeAteFood) {
			Position p = ((SnakeAteFood) e).p;
			state.foodEaten[((SnakeAteFood) e).player]++;
			for (int i = 0; i < state.food.size(); i++) {
				if (state.food.get(i).equals(p)) {
					state.food.remove(i);
					break;
				}
			}
		}
		if (e instanceof SnakeFoodAdd) {
			Position p = ((SnakeFoodAdd) e).p;
			state.food.add(p);
		}
		if (e instanceof SnakeWinnerEvent) {
			state.winner = ((SnakeWinnerEvent) e).playerName;
		}
		if (e instanceof SnakeTurnEnd) {
			state.curTurn++;
		}
	}

	private void drawString(Graphics2D g, Box b, String text, Color c) {
		Font fo = getLargestFittingFont(g.getFont(), b, g, text, 180);
		g.setStroke(new BasicStroke(1));
		FontMetrics fm = g.getFontMetrics(fo);
		Rectangle2D fR = fm.getStringBounds(text, g);
		g.setFont(fo);
		g.setColor(Color.BLACK);
		g.drawString(text, b.left + (b.width - (int) fR.getWidth()) / 2,
				b.top + (b.height + (int) (0.5 * fR.getHeight())) / 2);
	}

	private Font getLargestFittingFont(Font f, Box b, Graphics2D g, String s, int largestSize) {
		int minSize = 1;
		int maxSize = largestSize;
		while (minSize < maxSize) {
			int midSize = (minSize + maxSize) / 2;
			f = f.deriveFont(Font.PLAIN, midSize);
			FontMetrics fm = g.getFontMetrics(f);
			Rectangle2D fR = fm.getStringBounds(s, g);
			if (fR.getWidth() < b.width && fR.getHeight() < b.height) {
				minSize = midSize + 1;
			} else {
				maxSize = midSize - 1;
			}
		}
		return f.deriveFont(minSize);
	}

	private void drawSnake(BoxFactory f, Graphics2D g, Deque<Position> blocks) {
		int boxSize = boardBoxes[0][0].width;
		int borderIn = boxSize / 4;
		Position pre = null;
		int count = 0;
		for (Position p : blocks) {
			if (count >= blocks.size() - 1)
				continue;
			count++;
			Box b = null;
			if (pre == null) {
				b = f.fromDimensions(boardBoxes[p.r][p.c].left + borderIn, boardBoxes[p.r][p.c].top + borderIn,
						boxSize / 2, boxSize / 2);
			} else {
				Box b1 = boardBoxes[p.r][p.c];
				Box b2 = boardBoxes[pre.r][pre.c];
				Box b3 = f.fromPoints(Math.min(b1.left, b2.left), Math.min(b1.top, b2.top),
						Math.max(b1.right, b2.right), Math.max(b1.bottom, b2.bottom));
				b = f.fromPoints(b3.left + borderIn, b3.top + borderIn, b3.right + borderIn, b3.bottom + borderIn);
			}
			b.fill(g);
			pre = p;
		}
	}

	private void tweenMovement(BoxFactory f, Position from, Position to, int amo, Graphics2D g) {
		int boxSize = boardBoxes[0][0].width;
		int borderIn = boxSize / 4;
		Box b1 = boardBoxes[from.r][from.c];
		Box b2 = boardBoxes[to.r][to.c];
		Box b3 = f.fromPoints(Math.min(b1.left, b2.left), Math.min(b1.top, b2.top), Math.max(b1.right, b2.right),
				Math.max(b1.bottom, b2.bottom));
		Box b = f.fromPoints(b3.left + borderIn, b3.top + borderIn, b3.right + borderIn, b3.bottom + borderIn);
		int amoDiff = (amo * boxSize / TURN_FRAMES);
		if (from.c < to.c) {
			b = f.fromDimensions(b.left + amoDiff, b.top, b.width - amoDiff, b.height);
		} else if (from.c > to.c) {
			b = f.fromDimensions(b.left, b.top, b.width - amoDiff, b.height);
		} else if (from.r < to.r) {
			b = f.fromDimensions(b.left, b.top + amoDiff, b.width, b.height - amoDiff);
		} else {
			b = f.fromDimensions(b.left, b.top, b.width, b.height - amoDiff);
		}
		b.fill(g);
	}

}
