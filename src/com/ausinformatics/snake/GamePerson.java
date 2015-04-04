package com.ausinformatics.snake;

import java.util.ArrayDeque;
import java.util.Deque;

public class GamePerson {

	// Front of the deque is the tail. So we push the heads on, pop the tails
	// off.
	private Deque<Position> blocks;
	private boolean isAlive;

	// Creates a person from head to tail. If they are not in a line, then will
	// go horizontal first.
	public GamePerson(Position head, Position tail) {
		blocks = new ArrayDeque<>();
		Position cur = tail.clone();
		blocks.push(cur);
		while (cur.equals(head)) {
			if (cur.c < head.c) {
				cur = cur.move(Position.RIGHT);
			} else if (cur.c > head.c) {
				cur = cur.move(Position.LEFT);
			} else if (cur.r < head.r) {
				cur = cur.move(Position.DOWN);
			} else if (cur.r > head.r) {
				cur = cur.move(Position.UP);
			}
			blocks.push(cur);
		}
		isAlive = true;
	}

	public Position getHead() {
		return blocks.getLast();
	}
	
	public Position getTail() {
		return blocks.getFirst();
	}
	
	public Position addHead(int dir) {
		Position p = getHead().move(dir);
		blocks.push(p);
		return p;
	}
	
	public Position removeTail() {
		return blocks.pop();
	}
	
	public void markArray(int arr[][], int num) {
		for (Position p : blocks) {
			arr[p.r][p.c] = num;
		}
	}
	
	public void removeFromArray(int arr[][]) {
		for (Position p : blocks) {
			arr[p.r][p.c] = 0;
		}
	}
	
	public boolean getAlive() {
		return isAlive;
	}
	
	public void kill() {
		isAlive = false;
	}
}
