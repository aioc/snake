#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/socket.h>
#include <sys/types.h>
#include <arpa/inet.h>
#include <netinet/tcp.h>
#include <signal.h>
#include <string.h>
#include <stdarg.h>
#include <time.h>

#include "armada.h"

#define VERSION					"v0.2"

#define STATE_INTERNAL			0
#define STATE_REGISTER			1
#define STATE_MAKEMOVE			2

static int echo_mode;
static int sock;
static int state;


///////////////////////////////////////////////
//
//  HELPER FUNCTIONS
//

static int sendPrintf(const char *fmt, ...) {
	char buf[1000];
	va_list ap;
	va_start(ap, fmt);
	vsnprintf(buf, 999, fmt, ap);
	va_end(ap);

	int buflen = strlen(buf);
	if (write(sock, buf, buflen) != buflen) {
		return -1;
	}
	if (echo_mode) {
		buf[buflen - 1] = '\0';
		fprintf(stderr, "\x1b[1;35m> \"");
		fprintf(stderr, "%s", buf);
		fprintf(stderr, "\"\x1b[0m\n");
	}
	return 0;
}

static void ensureState(int s, const char* fn) {
	if (s != state) {
		fprintf(stderr, "Cannot call function %s at this time (%d != %d)\n", fn, s, state);
		raise(SIGUSR1);
	}
}

static int validChr(char c) {
	if ('0' <= c && c <= '9') return TRUE;
	if ('A' <= c && c <= 'Z') return TRUE;
	if ('a' <= c && c <= 'z') return TRUE;
	if (c == '_' || c == '-' || c == '.') return TRUE;
	return FALSE;
}

static int clamp(int l, int x, int h) {
	if (x < l) return l;
	if (x > h) return h;
	return x;
}

static void internalError(char *fmt, ...) {
	fprintf(stderr, "CLIENT LIBRARY INTERNAL ERROR:\n");
	va_list ap;
	va_start(ap, fmt);
	vfprintf(stderr, fmt, ap);
	va_end(ap);
	fprintf(stderr, "Please alert the organisers\n");
	exit(EXIT_FAILURE);
}


///////////////////////////////////////////////
//
//  FUNCTIONS THE CLIENT CALLS
//

static int colour_r, colour_g, colour_b;
static char name[17];

static int move_made;
static int move_d;

void setName(const char *n) {
	ensureState(STATE_REGISTER, "setName");
	int i;
	for (i = 0; n[i] && i < MAX_NAME_LENGTH; i++) {
		if (!validChr(n[i])) {
			name[i] = '_';
		} else {
			name[i] = n[i];
		}
	}
	name[i] = 0;
}

void setColour(int r, int g, int b) {
	ensureState(STATE_REGISTER, "setColour");
	colour_r = clamp(0, r, 255);
	colour_g = clamp(0, g, 255);
	colour_b = clamp(0, b, 255);
}

void makeMove(int d) {
	ensureState(STATE_MAKEMOVE, "makeMove");
	move_d = d;
	move_made = TRUE;
}


///////////////////////////////////////////////
//
//  COMMAND FUNCTIONS
//

static int handleName(char *args) {
	colour_r = rand() % 256;
	colour_g = rand() % 256;
	colour_b = rand() % 256;
	name[0] = '\0';

	state = STATE_REGISTER;
	alarm(1);
	clientRegister();
	alarm(0);
	state = STATE_INTERNAL;

	if (name[0] == '\0') {
		strcpy(name, "forget-a-lot");
	}
	if (sendPrintf("NAME %s %d %d %d\n", name, colour_r, colour_g, colour_b)) {
		return -1;
	}
	return 0;
}

static int handleError(char *args) {
	fprintf(stderr, "Error: %s\n", args);
	exit(EXIT_FAILURE);
	return 0;
}

static int handleNewGame(char *args) {
	int num_players, board_size;
	int pid;
	// Big sscanf
	if (sscanf(args, "%d %d %d", &num_players, &board_size, &pid) != 3) {
		internalError("Bad arguments on NEWGAME: %s\n", args);
	}
	alarm(1);
	clientInit(num_players, board_size, pid);
	alarm(0);
	if (sendPrintf("READY\n")) {
		return -1;
	}
	return 0;
}

static int start_block_size;
static int start_block_pid;
static int start_block_seen;
static struct point start_blocks[MAX_BOARD_SIZE * MAX_BOARD_SIZE];

static int handleStart(char* args) {
	if (sscanf(args, "%d %d", &start_block_pid, &start_block_size) != 2) {
		internalError("Bad arguments on START: %s\n", args);
	}
	start_block_seen = 0;
	return 0;
}

static int handleStartBlock(char* args) {
	int r, c;
	if (sscanf(args, "%d %d", &r, &c) != 2) {
		internalError("Bad arguments on STARTBLOCK: %s\n", args);
	}
	start_blocks[start_block_seen].r = r;
	start_blocks[start_block_seen].c = c;
	start_block_seen++;
	if (start_block_seen == start_block_size) {
		alarm(1);
		clientStartPositions(start_block_pid, start_block_size, start_blocks);
		alarm(0);
	}
	return 0;
}

static int handleMoved(char* args) {
	int pid;
	struct point h, t;
	int readTail = TRUE;
	if (sscanf(args, "%d %d %d %d %d", &pid, &h.r, &h.c, &t.r, &t.c) != 5) {
		if (sscanf(args, "%d %d %d", &pid, &h.r, &h.c) != 3) {
			internalError("Bad arguments on MOVED: %s\n", args);
		}
		readTail = FALSE;
	}
	alarm(1);
	clientAddHead(pid, h);
	alarm(0);
	if (readTail) {
		alarm(1);
		clientRemoveTail(pid, t);
		alarm(0);
	}
	return 0;
}

static int handleDied(char* args) {
	int pid;
	if (sscanf(args, "%d ", &pid) != 1) {
		internalError("Bad arguments on DIED: %s\n", args);
	}
	alarm(1);
	clientPlayerDied(pid);
	alarm(0);
	return 0;
}

static int handleFood(char* args) {
	struct point p;
	if (sscanf(args, "%d %d", &p.r, &p.c) != 2) {
		internalError("Bad arguments on FOOD: %s\n", args);
	}
	alarm(1);
	clientFoodAdded(p);
	alarm(0);
	return 0;
}

static int handleYourMove(char *args) {
	move_made = FALSE;
	state = STATE_MAKEMOVE;
	alarm(1);
	clientDoTurn();
	alarm(0);
	state = STATE_INTERNAL;

	if (!move_made) {
		fprintf(stderr, "No	move was made\n");
		raise(SIGUSR1);
	}
	if (sendPrintf("MOVE %d\n", move_d)) {
		return -1;
	}
	return 0;
}

static int handleGameOver(char *args) {
	fprintf(stderr, "Game over! %s\n", args);
	return 0;
}


///////////////////////////////////////////////
//
//  MAIN COMMAND LOOP FUNCTIONS
//

// Takes in arguments, returns 0 on success.
typedef int (*command_func)(char *);

struct command {
	char *name;
	command_func func;
};

static struct command commands[] = {
	{"NAME", handleName},
	{"ERROR", handleError},
	{"BADPROT", handleError},
	{"NEWGAME", handleNewGame},
	// Snake server commands
	{"START", handleStart},
	{"STARTBLOCK", handleStartBlock},
	{"MOVED", handleMoved},
	{"DIED", handleDied},
	{"FOOD", handleFood},
	{"YOURMOVE", handleYourMove},
	{"GAMEOVER", handleGameOver},
};


/**********************************************
 *                                            *
 *         IF THIS IS DONE CORRECTLY,         *
 *        YOU WILL NOT HAVE TO CHANGE         *
 *          STUFF BELOW THIS COMMENT          *
 *                                            *
 **********************************************/

// Will read from input until either max_read - 1 characters are read,
// or new line encountered. The last character will always be \0, and
// no new line.
// Returns 0 on success, else -1 for failure (usually connection closed).
static int getLine(char *buf, int max_read) {
	int i;
	for (i = 0; i < max_read - 1; i++) {
		if (read(sock, &buf[i], 1) <= 0) {
			return -1;
		}
		if (buf[i] == '\n') break;
	}
	buf[i] = '\0';
	if (echo_mode) {
		fprintf(stderr, "\x1b[1;32m< \"");
		fprintf(stderr, "%s", buf);
		fprintf(stderr, "\"\x1b[0m\n");
	}
	return 0;
}

static void mainLoop(void) {
	char buf[1000];
	int finished = FALSE;
	state = STATE_INTERNAL;
	while (!finished) {
		if (getLine(buf, sizeof(buf))) break; // Couldn't get line. Server probably died.
		char *args = strchr(buf, ' ');
		if (args != NULL) {
			// Make that space a \0
			*args = '\0';
			args++;
		}
		// So now buf == first word, and *args is either NULL
		// or points to the character after the space
		int i;
		int found = FALSE;
		for (i = 0; i < sizeof(commands) / sizeof(struct command); i++) {
			if (strcmp(buf, commands[i].name) == 0) {
				if (commands[i].func(args) == 0) {
					// No error
					found = TRUE;
				} else {
					finished = TRUE;
				}
				break;
			}
		}
		if (!found) {
			fprintf(stderr, "Error: server sent unknown command \"%s\"\n"
					"(as part of \"%s %s\"\n", buf, buf, args);
			finished = TRUE;
		}
	}
}

static void connectServer(char *server, int port) {
	// Create socket
	if ((sock = socket(PF_INET, SOCK_STREAM, IPPROTO_TCP)) < 0) {
		fprintf(stderr, "Error: could not create socket. This is bad\n");
		exit(EXIT_FAILURE);
	}

	int yes = TRUE;
	if (-1 == setsockopt (sock, IPPROTO_TCP, TCP_NODELAY, &yes, sizeof (yes))) {
		fprintf(stderr, "Error: could not turn nodelay on. This is bad\n");
		exit(EXIT_FAILURE);
	}
	if (-1 == setsockopt (sock, IPPROTO_TCP, TCP_QUICKACK, &yes, sizeof (yes))) {
		fprintf(stderr, "Error: could not turn quickack on. This is bad\n");
		exit(EXIT_FAILURE);
	}

	// Initialise remote address
	struct sockaddr_in serv_addr;

	memset(&serv_addr, 0, sizeof(serv_addr));
	serv_addr.sin_family = AF_INET;
	serv_addr.sin_port = htons(port);
	if (inet_pton(AF_INET, server, &serv_addr.sin_addr) <= 0) {
		fprintf(stderr, "Error: could not set up server address. This is bad\n");
		exit(EXIT_FAILURE);
	}
	if (connect(sock, (const struct sockaddr *)&serv_addr, sizeof(struct sockaddr_in)) < 0) {
		close(sock);
		sock = -1;
		// We failed to connect :(
	}
}

///////////////////////////////////////////////
//
//  SEGFAULT CATCHING
//

static int app_argc;
static char **app_argv;

static void segfaultHandler(int v) {
	fprintf(stderr, "Segmentation fault");
	fprintf(stderr, " -- restarting client\n\n\n");
	sleep(1);
	execvp(app_argv[0], app_argv);
	exit(-1);
}

static void siguserHandler(int v) {
	fprintf(stderr, "Invalid client operation");
	fprintf(stderr, " -- restarting client\n\n\n");
	sleep(1);
	execvp(app_argv[0], app_argv);
	exit(-1);
}

static void sigalrmHandler(int v) {
	fprintf(stderr, "TIME LIMIT EXCEEDED");
	fprintf(stderr, " -- restarting client\n\n\n");
	sleep(1);
	execvp(app_argv[0], app_argv);
	exit(-1);
}

static void prepareSignalHandler(int signum, void(*handler)(int)) {
	struct sigaction param;
	memset(&param, 0, sizeof(param));
	param.sa_handler = handler;
	sigemptyset(&(param.sa_mask));
	param.sa_flags = SA_NODEFER | SA_RESTART;
	sigaction(signum, &param, NULL);
}


///////////////////////////////////////////////
//
//  PRINTING FUNCTIONS
//

static void printVersion(void) {
	fprintf(stderr, "\n"
			"*******************************\n"
			"*          Welcome to         *\n"
			"*  PHAIS client library %-6s*\n"
			"*******************************\n", VERSION);
}

static void printHelp(char *name) {
	fprintf(stderr, "Usage: %s [-hvse] server-ip [port]\n"
			"Options:\n"
			"\th: Print out this help\n"
			"\tv: Print out the version message\n"
			"\ts: Disable segfault handling\n"
			"\te: Echo all network traffic\n", name);
}

int main(int argc, char *argv[]) {
	printVersion();
	// Make sure that crashes will
	char c;
	char *server;
	int port = 12317;
	int seg_handle = TRUE;
	int res;
	while ((c = getopt (argc, argv, "hvse")) != -1) {
		switch (c) {
			case 'h':
				printHelp(argv[0]);
				break;
			case 'v':
				printVersion();
				break;
			case 's':
				seg_handle = FALSE;
				break;
			case 'e':
				echo_mode = TRUE;
				break;
		}
	}
	if (optind == argc) {
		fprintf(stderr, "Must provide a server address\n");
		return EXIT_FAILURE;
	}
	if (optind <= argc - 2) {
		// Includes port
		res = sscanf(argv[optind + 1], "%d", &port);
		if (res != 1) {
			fprintf(stderr, "Invalid port option: %s\n", argv[optind + 1]);
			return EXIT_FAILURE;
		}
	}
	server = argv[optind];
	fprintf(stderr, "Configured!\n");
	fprintf(stderr, "Trying server of %s, port %d\n", server, port);
	if (!seg_handle) {
		fprintf(stderr, "SEGFAULTs will *not* be handled\n");
	}

	// Prepare handlers
	app_argc = argc;
	app_argv = argv;
	if (seg_handle) {
		prepareSignalHandler(SIGSEGV, &segfaultHandler);
	}
	prepareSignalHandler(SIGUSR1, &siguserHandler);
	prepareSignalHandler(SIGALRM, &sigalrmHandler);

	// Might as well initialise random
	srand(time(0));
	// Always connect
	while(TRUE) {
		// Attempt to connect here
		while(TRUE) {
			fprintf(stderr, "Attempting to connect...");
			connectServer(server, port);
			if (-1 != sock) {
				break;
			}
			fprintf(stderr, "   failed to connect. Will retry...\n");
			sleep(1);
		}
		fprintf(stderr, "   connected!\n");
		mainLoop();
		fprintf(stderr, "Was disconnected\n");
		shutdown(sock, SHUT_RDWR);
		close(sock);
		sleep(2);
	}
	return EXIT_SUCCESS;
}
