run:
	java -cp class Jeu

compile:
	javac -encoding UTF-8 -d class src/*.java

compile_run: compile run

compile_test:
	javac -encoding UTF-8 -d class src/*.java test/*.java

run_test:
	java -cp class TestIAProcedurale

test: compile_test run_test
