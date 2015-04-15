#ifndef __PHAIS_H
#define __PHAIS_H

#ifdef __cplusplus
extern "C" {
#endif
	/////////////////////////////////////////////////////////////////////

	// Constants used
#define TRUE                    1
#define FALSE                   0

	// Limits of certain restrictions
#define MAX_NUM_PLAYERS         4
#define MAX_BOARD_SIZE          40
#define MAX_NAME_LENGTH			16

	// Directions
#define UP                      0
#define RIGHT                   1
#define DOWN                    2
#define LEFT                    3
#define NUM_DIRECTIONS			4
	/**
	 *   Use:
	 *   int dr[] = {-1, 0, 1, 0};
	 *   int dc[] = {0, 1, 0, -1};
	 *   to deal with directions.
	 *   These directions are defined in such a way, that if you are at square
	 *   (r,c), the square in direction d from it has the coordinates
	 *   (r + dr[d], c + dc[d]).
	 */

	struct point {
		int r;
		int c;
	};

	/////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////

	// The following must be implemented by the client:

	/**
	 *   This is called when the client first logs on to the server. You should
	 *   call setName(const char* name) to define the name of your AI and
	 *   setColour(int r, int g, int b) to elect a preferred colour. Names can
	 *   be up to 16 characters long and colour components are between 0 and
	 *   255 inclusive.
	 */
	void clientRegister(void);
	
	// ******** These two functions are only called at the start of the game ********
	
	/**
	 * This is called at the start of a game. The argument numPlayers
	 * specifies the number of players in this game. The argument boardSize
	 * specifies the height/width of the game board, which is always square.
	 * The argument playerID specifies your player ID throughout the game. Any
	 * persistent data structures that you use throughout the course of a game
	 * are best initialised here.
	 */
	void clientInit(int numPlayers, int boardSize, int playerID);
	
	/**
	 * This is called at the start of a game, after clientInit is called. It
	 * will be called once for each player in the game, informing the client of
	 * the positions of each of the ships in the player's armada. The argument
	 * playerID specifies the player whose fleet the function call refers to.
	 * The argument positions will be an array of points with length elements
	 * (length will always be 5). struct point is a type which contains two
	 * variables r and c indicating the row and column of the ship,
	 * respectively.
	 */
	void clientStartPositions(int playerID, int length, struct point* positions);
	
	// ******** These four functions will be called *BEFORE* players take their turns for the round ********
	
	/**
	 * This is called before the client's turn. It will be called once for each
	 * player in the game, informing the client of the new position of the head
	 * of each player's fleet. The player is indicated by playerID and the new
	 * head is indicated by position.
	 */
	void clientAddHead(int playerID, struct point position);

	/**
	 * This is called before the client's turn. It will be called at most once
	 * for each player in the game, informing the client of the movement of the
	 * tail of each player's fleet. The player is indicated by playerID and the
	 * previous position of tail is indicated by position
	 */
	void clientRemoveTail(int playerID, struct point position);
	
	/**
	 * This is called before the client's turn. It is called for each player
	 * who was eliminated in the previous turn. The argument playerID indicates
	 * the player id of the eliminated fleet.
	 */
	void clientPlayerDied(int playerID);

	/**
	 * This is called when a new abandoned ship appears on a square. The
	 * argument position describes where the new abandoned ship has been
	 * sighted.
	 */
	void clientFoodAdded(struct point position);
	
	// ******** This function will be called *WHEN* it is your turn ********

	/**
	 * This is called when it's your turn to make a move. Do so by calling
	 * makeMove(int direction). Argument direction should be used to specify
	 * the direction that you wish your fleet's head to move in. (Note that
	 * directions are defined in armada.h as UP, RIGHT, DOWN and LEFT) If you
	 * call attempt to call makeMove() multiple times during your turn, only
	 * the last value will be used. If you do not call makeMove() in
	 * clientDoTurn(), your client will be dropped from the game and will be
	 * recognised as having lost the game.
	 */
	void clientDoTurn(void);


	/////////////////////////////////////////////////////////////////////
	// The following are available to the client:

	/**
	 * This will send to the server what you want your name to be. It must only
	 * contain A-Z, a-z, 1-9 and _. The length of the name should be at most 16
	 * characters (not including the null terminating byte).
	 *
	 * This can only be called in clientRegister.
	 */
	void setName(const char* name);

	/**
	 * This will send to the server what colour you want to be. The values must
	 * be 0-255.
	 *
	 * This can only be called in clientRegister.
	 */
	void setColour(int r, int g, int b);
	
	/**
	 * This will send to the server the move you want to make for this turn,
	 * defined as the direction you want the head to move in.
	 *
	 * This can only be called in clientDoTurn.
	 */
	void makeMove(int direction);

	/////////////////////////////////////////////////////////////////////

#ifdef __cplusplus
}
#endif

#endif
