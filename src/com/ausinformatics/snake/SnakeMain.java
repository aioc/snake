package com.ausinformatics.snake;

import com.ausinformatics.phais.core.Config;
import com.ausinformatics.phais.core.Director;

public class SnakeMain {

	public static void main(String[] args) {
		Config config = new Config();
		config.parseArgs(args);

		GameFactory f = new GameFactory();
		new Director(new PlayerFactory(), f).run(config);
	}
}
