all: Edge.class Obstacle.class Vertex.class PathPlanning.class

%.class: %.java
	javac -d . -classpath . $<

run: all
	java PathPlanning

clean:
	rm -f *.class 
