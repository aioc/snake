CC=gcc
CXXFLAGS=-Wall -O2 
CPP=g++


snake_library: snake_library.c snake.h
	$(CC) $(CXXFLAGS) -c -o snake_library.o snake_library.c

snake_example_client: snake_library snake_example_client.cpp
	$(CPP) $(CXXFLAGS) -o snake_example_client.exe snake_example_client.cpp snake_library.o

snake_simple: snake_library snake_simple.cpp
	$(CPP) $(CXXFLAGS) -o snake_simple.exe snake_simple.cpp snake_library.o

snake_follower: snake_library snake_follower.cpp
	$(CPP) $(CXXFLAGS) -o snake_follower.exe snake_follower.cpp snake_library.o

snake_luke: snake_library snake_luke.cpp
	$(CPP) $(CXXFLAGS) -o snake_luke.exe snake_luke.cpp snake_library.o

clean: 
	rm *.o *.exe

all: snake_library snake_example_client snake_simple snake_follower snake_luke

