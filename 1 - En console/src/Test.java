// ==============================================================================
// Classe Test
// ==============================================================================

import java.util.Arrays;

/**
 *	Classe de tests pour le jeu de bataille navale.
 */
class Test {

	// ==========================================================================
	// Données
	// ==========================================================================

	IA ia;

	// ==========================================================================
	// Constructeur
	// ==========================================================================

	/**
	 *	Constructeur de la classe de test.
	 */
	public Test() {
		// Tests commentés
	}

	// ==========================================================================
	// Main
	// ==========================================================================

	/**
	 *	Point d'entrée du programme de test.
	 */
	public static void main(String[] args) {
		Test test = new Test();
	}

	// ==========================================================================
	// Fonctions utilitaires
	// ==========================================================================

	/**
	 *	Affiche un message avec saut de ligne.
	 */
	public void aff(String message) {
		System.out.println(message);
	}

	/**
	 *	Affiche un message sans saut de ligne.
	 */
	public void affnn(String message) {
		System.out.print(message);
	}
}
