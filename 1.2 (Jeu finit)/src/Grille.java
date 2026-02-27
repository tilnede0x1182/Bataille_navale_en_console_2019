// ==============================================================================
// Classe Grille
// ==============================================================================

/**
 *	Représente la grille de jeu (dimensions uniquement).
 */
class Grille {

	// ==========================================================================
	// Données
	// ==========================================================================

	public int hauteur;
	public int largeur;

	// ==========================================================================
	// Constructeur
	// ==========================================================================

	/**
	 *	Constructeur de la grille.
	 *
	 *	@param hauteurGrille Hauteur de la grille
	 *	@param largeurGrille Largeur de la grille
	 */
	public Grille(int hauteurGrille, int largeurGrille) {
		this.hauteur = hauteurGrille;
		this.largeur = largeurGrille;
	}

	// ==========================================================================
	// Fonctions utilitaires
	// ==========================================================================

	/**
	 *	Représentation textuelle de la grille.
	 */
	public String toString() {
		return "Hauteur = " + hauteur + "\nLargeur = " + largeur + "\n";
	}

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
