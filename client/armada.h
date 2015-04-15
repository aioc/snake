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
#define MAX_BOARD_SIZE          20
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
	 *   This is called when your client connects to the server. You need to
	 *   provide a name using setName and a colour with setColour.
	 */
	void clientRegister(void);
	
	// ******** These two functions are only called at the start of the game ********
	
	/**
	 *   TODO:Describe
	 */
	void clientInit(int numPlayers, int boardSize, int pid);
	
	/**
	 *   TODO:Describe
	 */
	void clientStartPositions(int pid, int length, struct point* positions);
	
	// ******** These four functions will be called *BEFORE* players take their turns for the round ********
	
	/**
	 *   TODO:Describe
	 */
	void clientAddHead(int pid, struct point position);

	/**
	 *   TODO:Describe
	 */
	void clientRemoveTail(int pid, struct point position);
	
	/**
	 *   TODO:Describe
	 */
	void clientPlayerDied(int pid);

	/**
	 *   TODO:Describe
	 */
	void clientFoodAdded(struct point position);
	
	// ******** This function will be called *WHEN* it is your turn ********

	/**
	 *   TODO: Describe
	 */
	void clientDoTurn(void);


	/////////////////////////////////////////////////////////////////////
	// The following are available to the client:

	/**
	 *   This will send to the server what you want your name to be. It must
	 *   only contain A-Z, a-z, 1-9 and _. The length of the name should be at
	 *   most 16 characters (not including the null terminating byte).
	 *
	 *   This can only be called in clientRegister.
	 */
	void setName(const char* name);

	/**
	 *   This will send to the server what colour you want to be. The values
	 *   must be 0-255.
	 *
	 *   This can only be called in clientRegister.
	 */
	void setColour(int r, int g, int b);
	
	/**  
	 *   TODO: Describe
	 *
	 *   This can only be called in clientDoTurn.
	 */
	void makeMove(int d);

	/////////////////////////////////////////////////////////////////////

#ifdef __cplusplus
}
#endif

#endif
