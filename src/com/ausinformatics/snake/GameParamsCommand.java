package com.ausinformatics.snake;

import java.io.PrintStream;

import com.ausinformatics.phais.core.Director;
import com.ausinformatics.phais.core.commander.commands.Command;

public class GameParamsCommand implements Command {

	private GameFactory f;
	public GameParamsCommand(GameFactory f) {
		this.f = f;
	}
	
	@Override
	public void execute(Director reportTo, PrintStream out, String[] args) {
		boolean badArgs = false;
		int boardSize = 0;
		int maxTurns = 0;
		int maxFood = 0;
		if (args.length != 3) {
			badArgs = true;
		} else {
			try {
				boardSize = Integer.parseInt(args[0]);
				maxTurns = Integer.parseInt(args[1]);
				maxFood = Integer.parseInt(args[2]);
			} catch (NumberFormatException e) {
				badArgs = true;
			}
		}
		
		if (badArgs) {
			out.println("Usage: PARAMS boardSize, maxTurns, maxFood");
		} else {
			f.boardSize = boardSize;
			f.maxTurns = maxTurns;
			f.maxFood = maxFood;
		}
	}

	@Override
	public String shortHelpString() {
		return "Change the params of the games.\nIn order of boardSize, numProd, numCons, numTypes, startingMoney";
	}

	@Override
	public String detailedHelpString() {
		return null;
	}

}
