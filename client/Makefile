CC=gcc
CXXFLAGS=-Wall -O2 
CPP=g++


snake_library: snake_library.c snake.h
	$(CC) $(CXXFLAGS) -c -o snake_library.o snake_library.c

snake_example_client: snake_library snake_example_client.cpp
	$(CPP) $(CXXFLAGS) -o snake_example_client.exe snake_example_client.cpp snake_library.o

snake_luke: snake_library snake_luke.cpp
	$(CPP) $(CXXFLAGS) -o snake_luke.exe snake_luke.cpp snake_library.o

clean: 
	rm *.o *.exe

all: snake_library snake_example_client snake_luke

