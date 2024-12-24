# Makefile for Emergency Supply Network Project

# Compiler and Executable
JAVAC = javac
JAVA = java

# JSON Library
CLASSPATH = .:java-json.jar

# Source Files
SRC = EmergencySupplyNetwork.java ResourceRedistribution.java DynamicResourceSharing.java NetworkApp.java InputParser.java JsonUtils.java

# Targets
.PHONY: all clean run

# Default target
all: compile

# Compile all Java files
compile:
	$(JAVAC) -cp $(CLASSPATH) $(SRC)

# Run the program with optional arguments

run_tests: compile
	$(JAVA) NetworkApp TestCase1.txt
	$(JAVA) NetworkApp TestCase2.txt

run: compile
	$(JAVA) NetworkApp $(ARGS)

# Clean compiled files
clean:
	rm -f *.class