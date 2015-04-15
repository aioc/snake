#include <cstdio>
#include <cstdlib>
#include "armada.h"

#define BLANK			0
#define FOOD			-1

int dr[] = {-1, 0, 1, 0};
int dc[] = {0, 1, 0, -1};


int numP, size, id;
int curTurn;

int player[MAX_BOARD_SIZE][MAX_BOARD_SIZE];
int board[MAX_BOARD_SIZE][MAX_BOARD_SIZE];
struct point myHead;
int curLength[MAX_NUM_PLAYERS];

void clientRegister(void) {
	setName("A simple farmer");
	setColour(0, 255, 0);
}

void clientInit(int numPlayers, int boardSize, int pid) {
	numP = numPlayers;
	size = boardSize;
	id = pid;
	int i, j;
	for (i = 0; i < size; i++) {
		for (j = 0; j < size; j++) {
			board[i][j] = BLANK;
			player[i][j] = BLANK;
		}
	}
}

void clientStartPositions(int pid, int length, struct point* positions) {
	int i;
	for (i = 0; i < length; i++) {
		board[positions[i].r][positions[i].c] = length - i;
		player[positions[i].r][positions[i].c] = pid + 1;
	}
	curLength[pid] = length;
	curTurn = length;
	if (pid == id) {
		myHead = positions[0];
	}
}

void clientAddHead(int pid, struct point position) {
	board[position.r][position.c] = curTurn;
	player[position.r][position.c] = pid + 1;
	if (pid == id) {
		myHead = position;
	}
	curLength[pid]++;
}

void clientRemoveTail(int pid, struct point position) {
	if (board[position.r][position.c] != curTurn) {
		board[position.r][position.c] = 0;
		player[position.r][position.c] = 0;
	}
	curLength[pid]--;
}

void clientPlayerDied(int pid) {
	int i, j;
	for (i = 0; i < size; i++) {
		for (j = 0; j < size; j++) {
			if (player[i][j] == pid + 1) {
				board[i][j] = 0;
				player[i][j] = 0;
			}
		}
	}	
}

void clientFoodAdded(struct point position) {
	player[position.r][position.c] = FOOD;
}

int randMove() {
	printf ("At rand :(\n");
	return rand() % 4;
}

int areas[MAX_BOARD_SIZE][MAX_BOARD_SIZE];
int lookup[MAX_BOARD_SIZE * MAX_BOARD_SIZE];

int randGoodMove() {
	printf ("At rand good\n");
	int i;
	int larg = 0;
	int lind = -1;
	for (i = 0; i < 4; i++) {
		int r = myHead.r + dr[i % 4];
		int c = myHead.c + dc[i % 4];
		if (r >= 0 && c >= 0 && r < size && c < size) {
			if (board[r][c] <= 0) {
				if (lookup[areas[r][c]] > larg) {
					larg = lookup[areas[r][c]];
					lind = i;
				}
			}
		}
	}
	if (lind == -1) {
		return randMove();
	} else {
		return lind;
	}
}



int seenC;
int seen[MAX_BOARD_SIZE][MAX_BOARD_SIZE];
int pre[MAX_BOARD_SIZE][MAX_BOARD_SIZE];
int qS, qE;
struct point queue[MAX_BOARD_SIZE * MAX_BOARD_SIZE * MAX_BOARD_SIZE];

int amoR;
int route[MAX_BOARD_SIZE * MAX_BOARD_SIZE * MAX_BOARD_SIZE];

int val(struct point p) {
	if (p.r < 0 || p.c < 0 || p.r >= size || p.c >= size) return FALSE;
	if (seen[p.r][p.c] == seenC) return FALSE;
	if (board[p.r][p.c] != 0) return FALSE;
	return TRUE;
}

struct point move(struct point p, int d) {
	struct point p2;
	p2.r = p.r + dr[d];
	p2.c = p.c + dc[d];
	return p2;
}

int eq(struct point p1, struct point p2) {
	return p1.c == p2.c && p1.r == p2.r;
}

int findArea(struct point p, int a) {
	if (areas[p.r][p.c] == a) {
		return 0;
	}
	areas[p.r][p.c] = a;
	int i;
	int ar = 1;
	for (i = 0; i < 4; i++) {
		struct point c = move(p, i);
		if (!val(c)) continue;
		ar += findArea(c, a);
	}
	return ar;
}

void calcAreas() {
	int i, j;
	for (i = 0; i < size; i++) {
		for (j = 0; j < size; j++) {
			areas[i][j] = -1;
		}
	}
	int amo = 0;
	for (i = 0; i < size; i++) {
		for (j = 0; j < size; j++) {
			struct point p;
			p.r = i;
			p.c = j;
			if (areas[p.r][p.c] == -1 && val(p)) {
				int n = amo++;
				lookup[n] = findArea(p, n);
			}
		}
	}
}

int findMove() {
	seenC++;
	calcAreas();
	qS = qE = 0;
	queue[qE++] = myHead;
	struct point c;
	while (qS < qE) {
		c = queue[qS++];
		if (player[c.r][c.c] == FOOD) break;
		int i;
		for (i = 0; i < 4; i++) {
			struct point n = move(c, i);
			if (!val(n)) continue;
			if (lookup[areas[n.r][n.c]] < curLength[id]) continue;
			seen[n.r][n.c] = seenC;
			pre[n.r][n.c] = (i + 2) % 4;
			queue[qE++] = n;
		}
	}
	if (player[c.r][c.c] == FOOD) {
		int m = 0;
		while (!eq(c, myHead)) {
			m = pre[c.r][c.c];
			c = move(c, m);
		}
		return (m + 2) % 4;
	}
	return randGoodMove();
}

void clientDoTurn(void) {
	curTurn++;
	makeMove(findMove());
}
