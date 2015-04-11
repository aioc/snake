package com.ausinformatics.snake;

import com.ausinformatics.phais.core.server.ClientConnection;
import com.ausinformatics.phais.core.server.DisconnectedException;

public class Action {

	public int dir;

	private Action(int dir) {
		this.dir = dir;
	}

	public static Action getAction(ClientConnection c) throws BadProtocolException, DisconnectedException {
		String inputString;
		inputString = c.getStrInput();
		String[] tokens = inputString.split("\\s");
		Action finalA;
		if (tokens.length < 2) {
			throw new BadProtocolException("Getting action: Not enough arguments (got " + inputString + ")");
		} else if (tokens.length > 2) {
			throw new BadProtocolException("Getting action: Too many arguments (got " + inputString + ")");
		} else if (!tokens[0].equals("MOVE")) {
			throw new BadProtocolException("Getting action: Invalid identifier (got " + inputString + ")");
		} else {
			int dir;
			try {
				dir = Integer.parseInt(tokens[1]);
			} catch (NumberFormatException e) {
				throw new BadProtocolException("Getting action: Bad numbers (got " + inputString + ")");
			}
			if (dir < 0 || dir >= 4) {
				throw new BadProtocolException("Getting action: Invalid move character (got " + inputString + ")");
			}
			finalA = new Action(dir);
		}
		return finalA;
	}
}
