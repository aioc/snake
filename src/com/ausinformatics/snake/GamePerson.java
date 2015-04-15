package com.ausinformatics.snake;

import java.util.ArrayDeque;
import java.util.Deque;

import com.ausinformatics.phais.core.server.ClientConnection;

public class GamePerson {

	// Front of the deque is the tail. So we push the heads on, pop the tails
	// off.
	private Deque<Position> blocks;
	private boolean amAlive;

	// Creates a person from head to tail. If they are not in a line, then will
	// go horizontal first.
	public GamePerson(Position head, Position tail) {
		blocks = new ArrayDeque<>();
		Position cur = head.clone();
		blocks.addLast(cur);
		while (!cur.equals(tail)) {
			if (cur.c < tail.c) {
				cur = cur.move(Position.RIGHT);
			} else if (cur.c > tail.c) {
				cur = cur.move(Position.LEFT);
			} else if (cur.r < tail.r) {
				cur = cur.move(Position.DOWN);
			} else if (cur.r > tail.r) {
				cur = cur.move(Position.UP);
			}
			blocks.addLast(cur);
		}
		amAlive = true;
	}

	public Position getHead() {
		return blocks.getFirst();
	}
	
	public Position getTail() {
		return blocks.getLast();
	}
	
	public Position addHead(int dir) {
		Position p = getHead().move(dir);
		blocks.addFirst(p);
		return p;
	}
	
	public Position removeTail() {
		return blocks.removeLast();
	}
	
	public int getLength() {
		return blocks.size();
	}
	
	public void markArray(int arr[][], int num) {
		for (Position p : blocks) {
			arr[p.r][p.c] = num;
		}
	}
	
	public void cloneToDeque(Deque<Position> d) {
		for (Position p : blocks) {
			d.addLast(p);
		}
	}
	
	public void sendToPlayer(ClientConnection c) {
		for (Position p : blocks) {
			c.sendInfo("STARTBLOCK " + p.r + " " + p.c);
		}
	}
	
	public void removeFromArray(int arr[][]) {
		for (Position p : blocks) {
			if (p.r >= 0 && p.c >= 0 && p.r < arr.length && p.c < arr[0].length) {
				arr[p.r][p.c] = 0;
			}
		}
	}
	
	public boolean isAlive() {
		return amAlive;
	}
	
	public void kill() {
		amAlive = false;
	}
}
