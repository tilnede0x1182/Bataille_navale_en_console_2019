// ==============================================================================
// Classe Grille
// ==============================================================================

/**
 *	Représente la grille de jeu de la bataille navale.
 *	Gère le plateau de jeu et les attaques sur les cases.
 */
class Grille {

	// ==========================================================================
	// Données
	// ==========================================================================

	public int hauteur;
	public int largeur;
	public Bateau[][] grille;
	Utilitaire utilitaire;

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
		this.utilitaire = new Utilitaire();
		this.hauteur = hauteurGrille;
		this.largeur = largeurGrille;
		this.grille = new Bateau[hauteur][largeur];
	}

	// ==========================================================================
	// Fonctions principales - Initialisation
	// ==========================================================================

	/**
	 *	Initialise toutes les cases de la grille à null.
	 */
	public void initialisationDeLaGrille() {
		for (int ligne = 0; ligne < hauteur; ligne++) {
			for (int colonne = 0; colonne < largeur; colonne++) {
				grille[ligne][colonne] = null;
			}
		}
	}

	// ==========================================================================
	// Fonctions principales - Combat
	// ==========================================================================

	/**
	 *	Attaque une case de la grille.
	 *
	 *	@param ordonnee Coordonnée Y de la case (ligne)
	 *	@param abscisse Coordonnée X de la case (colonne)
	 *	@return true si touché, false si dans l'eau
	 */
	public boolean attaquerUneCase(int ordonnee, int abscisse) {
		if (grille[ordonnee][abscisse] == null) {
			return false;
		}
		grille[ordonnee][abscisse].attaque();
		return true;
	}

	// ==========================================================================
	// Fonctions principales - Placement
	// ==========================================================================

	/**
	 *	Ajoute un bateau d'une case à la position spécifiée.
	 *
	 *	@param ordonnee Coordonnée Y de la case
	 *	@param abscisse Coordonnée X de la case
	 *	@param nombreCases Nombre de cases du bateau
	 *	@param numeroJoueur Numéro du joueur propriétaire
	 */
	public void ajouteUnBateauUneCase(int ordonnee, int abscisse, int nombreCases, int numeroJoueur) {
		grille[ordonnee][abscisse] = new Bateau(nombreCases, numeroJoueur);
	}

	// ==========================================================================
	// Fonctions utilitaires
	// ==========================================================================

	/**
	 *	Affiche un message avec saut de ligne.
	 *
	 *	@param message Message à afficher
	 */
	public void aff(String message) {
		System.out.println(message);
	}

	/**
	 *	Affiche un message sans saut de ligne.
	 *
	 *	@param message Message à afficher
	 */
	public void affnn(String message) {
		System.out.print(message);
	}
}
