# Makefile for Emergency Supply Network Project

# Compiler and Executable
JAVAC = javac
JAVA = java

# JSON Library
CLASSPATH = .:java-json.jar

# Source Files
SRC = com/ift2015/tp2/EmergencySupplyNetwork.java com/ift2015/tp2/ResourceRedistribution.java com/ift2015/tp2/DynamicResourceSharing.java com/ift2015/tp2/NetworkApp.java com/ift2015/tp2/InputParser.java

# Targets
.PHONY: all clean run

# Default target
all: compile

# Compile all Java files
compile:
	$(JAVAC) -cp $(CLASSPATH) $(SRC)

# Run the program
run:
	$(JAVA) com.ift2015.tp2.NetworkApp TestCase1.txt

# Clean compiled files
clean:
	rm -f ./com/ift2015/tp2/*.class
