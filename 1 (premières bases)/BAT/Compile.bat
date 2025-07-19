@echo off

cls&&cd ..\src && javac Affichage.java -d "../class" && javac Bateau.java -d "../class" && javac Grille.java -d "../class" && javac Jeu.java -d "../class" && javac Main.java -d "../class" && javac Menu.java -d "../class" && javac -d "../class" Utilitaire.java && cd ../BAT
cd ../BAT