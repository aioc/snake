CC=gcc
CXXFLAGS=-Wall -O2 
CPP=g++


armada_library: armada_library.c armada.h
	$(CC) $(CXXFLAGS) -c -o armada_library.o armada_library.c

armada_example_client: armada_library armada_example_client.cpp
	$(CPP) $(CXXFLAGS) -o armada_example_client.exe armada_example_client.cpp armada_library.o

armada_simple: armada_library armada_simple.cpp
	$(CPP) $(CXXFLAGS) -o armada_simple.exe armada_simple.cpp armada_library.o

armada_follower: armada_library armada_follower.cpp
	$(CPP) $(CXXFLAGS) -o armada_follower.exe armada_follower.cpp armada_library.o

armada_luke: armada_library armada_luke.cpp
	$(CPP) $(CXXFLAGS) -o armada_luke.exe armada_luke.cpp armada_library.o

clean: 
	rm *.o *.exe

all: armada_library armada_example_client armada_simple armada_follower armada_luke

