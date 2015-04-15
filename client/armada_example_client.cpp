#include <cstdio>
#include <cstdlib>
#include "armada.h"

#define BLANK			-1
#define FOOD			-2

int dr[] = {-1, 0, 1, 0};
int dc[] = {0, 1, 0, -1};


int numP, size, id;

int board[MAX_BOARD_SIZE][MAX_BOARD_SIZE];
struct point myHead;

void clientRegister(void) {
	setName("Dumbo-2015");
	setColour(255, 0, 0);
}

void clientInit(int numPlayers, int boardSize, int playerID) {
	numP = numPlayers;
	size = boardSize;
	id = playerID;
	int i, j;
	for (i = 0; i < size; i++) {
		for (j = 0; j < size; j++) {
			board[i][j] = BLANK;
		}
	}
}

void clientStartPositions(int playerID, int length, struct point* positions) {
	int i;
	for (i = 0; i < length; i++) {
		board[positions[i].r][positions[i].c] = playerID;
	}
	if (playerID == id) {
		myHead = positions[0];
	}
}

void clientAddHead(int playerID, struct point position) {
	board[position.r][position.c] = playerID;
	if (playerID == id) {
		myHead = position;
	}
}

void clientRemoveTail(int playerID, struct point position) {
	board[position.r][position.c] = BLANK;
}

void clientPlayerDied(int playerID) {
	int i, j;
	for (i = 0; i < size; i++) {
		for (j = 0; j < size; j++) {
			if (board[i][j] == playerID) {
				board[i][j] = BLANK;
			}
		}
	}	
}

void clientFoodAdded(struct point position) {
	board[position.r][position.c] = FOOD;
}

void clientDoTurn(void) {
	makeMove(DOWN); // Quick! Run away! As far down as we can go!
	// Note that this will either cause the head of the fleet to run
	// into itself if the head begins upwards relative to the next
	// ship in line, or it will eventually hit the bottom of the map if
	// it doesn't hit another ship first.
}
