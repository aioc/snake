package com.ausinformatics.snake.visualisation;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Random;

import com.ausinformatics.phais.core.interfaces.PersistentPlayer;
import com.ausinformatics.snake.Player;
import com.ausinformatics.snake.Position;

public class VisualGameState {

	public int boardSize;
	public int numPlayers;
	public String[] names;
	public Color[] colours;
	public int[] foodEaten;
	public List<Deque<Position>> blocks;
	public boolean[] isDead;
	public List<Position> food;
	public String winner;

	public VisualGameState(int boardSize, int numPlayers, List<PersistentPlayer> players) {
		names = new String[numPlayers];
		colours = new Color[numPlayers];
		foodEaten = new int[numPlayers];
		isDead = new boolean[numPlayers];
		blocks = new ArrayList<Deque<Position>>();
		food = new ArrayList<>();
		for (int i = 0; i < numPlayers; i++) {
			names[i] = players.get(i).getName();
			colours[i] = new Color(((Player) players.get(i)).getColour());
			for (int j = 0; j < i; j++) {
				Color original = colours[i];
				int tries = 0;
				while (colourDistance(colours[i], colours[j]) < 100 && tries < 10) {
					colours[i] = generateRandomColour(original);
					tries++;
				}
			}
		}
	}

	private double colourDistance(Color c1, Color c2) {
		double rmean = (c1.getRed() + c2.getRed()) / 2;
		int r = c1.getRed() - c2.getRed();
		int g = c1.getGreen() - c2.getGreen();
		int b = c1.getBlue() - c2.getBlue();
		double weightR = 2 + rmean / 256;
		double weightG = 4.0;
		double weightB = 2 + (255 - rmean) / 256;
		return Math.sqrt(weightR * r * r + weightG * g * g + weightB * b * b);
	}

	private Color generateRandomColour(Color mixer) {
		Random random = new Random();
		int red = random.nextInt(256);
		int green = random.nextInt(256);
		int blue = random.nextInt(256);

		// mix the color
		if (mixer != null) {
			red = (red + mixer.getRed()) / 2;
			green = (green + mixer.getGreen()) / 2;
			blue = (blue + mixer.getBlue()) / 2;
		}

		Color color = new Color(red, green, blue);
		return color;
	}
}
