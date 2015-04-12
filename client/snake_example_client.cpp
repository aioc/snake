#include <cstdio>
#include <cstdlib>
#include "snake.h"

#define BLANK			0
#define FOOD			-1

int dr[] = {-1, 0, 1, 0};
int dc[] = {0, 1, 0, -1};


int numP, size, id;

int board[MAX_BOARD_SIZE][MAX_BOARD_SIZE];
struct point myHead;

void clientRegister(void) {
	setName("Dumbo-2015");
	setColour(255, 0, 0);
}

void clientInit(int numPlayers, int boardSize, int pid) {
	numP = numPlayers;
	size = boardSize;
	id = pid;
	int i, j;
	for (i = 0; i < size; i++) {
		for (j = 0; j < size; j++) {
			board[i][j] = BLANK;
		}
	}
}

void clientStartPositions(int pid, int length, struct point* positions) {
	int i;
	for (i = 0; i < length; i++) {
		board[positions[i].r][positions[i].c] = pid + 1;
	}
	if (pid == id) {
		myHead = positions[length - 1];
	}
}

void clientAddHead(int pid, struct point position) {
	board[position.r][position.c] = pid + 1;
	if (pid == id) {
		myHead = position;
	}
}

void clientRemoveTail(int pid, struct point position) {
	board[position.r][position.c] = BLANK;
}

void clientPlayerDied(int pid) {
	int i, j;
	for (i = 0; i < size; i++) {
		for (j = 0; j < size; j++) {
			if (board[i][j] == pid + 1) {
				board[i][j] = 0;
			}
		}
	}	
}

void clientFoodAdded(struct point position) {
	board[position.r][position.c] = FOOD;
}

void clientDoTurn(void) {
	int i;
	int add = rand() % 4;
	for (i = 0; i < 4; i++) {
		int r = myHead.r + dr[(i + add) % 4];
		int c = myHead.c + dc[(i + add) % 4];
		if (r >= 0 && c >= 0 && r < size && c < size) {
			if (board[r][c] <= 0) {
				makeMove((i + add) % 4);
				return;
			}
		}
	}
	makeMove(3);
}
