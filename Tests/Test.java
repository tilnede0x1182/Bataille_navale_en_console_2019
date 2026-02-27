// ==============================================================================
// Classe Test
// ==============================================================================

import java.util.Arrays;

/**
	Classe de test pour verifier le fonctionnement de l'IA et des utilitaires.
*/
class Test {

// ==============================================================================
// Donnees
// ==============================================================================

	IA ia;

// ==============================================================================
// Constructeur
// ==============================================================================

	/**
		Constructeur qui execute les tests.
	*/
	public Test() {
		ia = new IA();
		testerTriTableau();
	}

// ==============================================================================
// Fonctions de test
// ==============================================================================

	/**
		Teste le tri d'un tableau d'entiers.
	*/
	private void testerTriTableau() {
		int[] tableau_test = {2, 4, 1};
		Arrays.sort(tableau_test);
		afficherTableau(tableau_test);
	}

	/**
		Affiche le contenu d'un tableau d'entiers.

		@param tableau Tableau a afficher
	*/
	private void afficherTableau(int[] tableau) {
		for (int index = 0; index < tableau.length; index++) {
			aff("tableau_test[" + index + "] = " + tableau[index]);
		}
	}

// ==============================================================================
// Main
// ==============================================================================

	/**
		Point d'entree du programme de test.

		@param args Arguments de la ligne de commande
	*/
	public static void main(String[] args) {
		Test test = new Test();
	}

// ==============================================================================
// Fonctions utilitaires
// ==============================================================================

	/**
		Affiche une chaine avec saut de ligne.

		@param message Message a afficher
	*/
	public void aff(String message) {
		System.out.println(message);
	}

	/**
		Affiche une chaine sans saut de ligne.

		@param message Message a afficher
	*/
	public void affnn(String message) {
		System.out.print(message);
	}
}
