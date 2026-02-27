// ==============================================================================
// Classe Bateau
// ==============================================================================

/**
 *	Représente un bateau dans le jeu de bataille navale.
 *	Gère le nombre de cases, l'état du bateau et les attaques.
 */
class Bateau {

	// ==========================================================================
	// Données
	// ==========================================================================

	int nombreDeCases;
	int nombreDeCasesRestantes;
	int joueur;
	Menu menu;

	// ==========================================================================
	// Constructeur
	// ==========================================================================

	/**
	 *	Constructeur du bateau.
	 *
	 *	@param nombreCases Nombre de cases occupées par le bateau
	 *	@param numeroJoueur Numéro du joueur propriétaire du bateau
	 */
	public Bateau(int nombreCases, int numeroJoueur) {
		this.menu = new Menu();
		this.nombreDeCases = nombreCases;
		this.nombreDeCasesRestantes = nombreCases;
		this.joueur = numeroJoueur;
	}

	// ==========================================================================
	// Fonctions principales - Placement des bateaux
	// ==========================================================================

	/**
	 *	Calcule le nombre de places de bateau autorisé.
	 *	Retourne la proposition si elle est inférieure au max autorisé.
	 *
	 *	@param nombreJoueurs Nombre de joueurs dans la partie
	 *	@param proposition Nombre de places proposé
	 *	@param hauteur Hauteur de la grille
	 *	@param largeur Largeur de la grille
	 *	@return Nombre de places autorisé
	 */
	public int nombreDePlaceDeBateau(int nombreJoueurs, int proposition, int hauteur, int largeur) {
		int maxAutorise = nombreDePlaceDeBateauMax(nombreJoueurs, hauteur, largeur);
		return (proposition < maxAutorise) ? proposition : maxAutorise;
	}

	/**
	 *	Calcule le nombre maximum de cases pour les bateaux.
	 *	Formule : (1 / (nombreJoueurs + 1)) * nombreCasesTotal
	 *
	 *	@param nombreJoueurs Nombre de joueurs dans la partie
	 *	@param hauteur Hauteur de la grille
	 *	@param largeur Largeur de la grille
	 *	@return Nombre maximum de cases autorisées
	 */
	public int nombreDePlaceDeBateauMax(int nombreJoueurs, int hauteur, int largeur) {
		int nombreCasesTotal = hauteur * largeur;
		int maxAutorise = (int) ((1.0 / (nombreJoueurs + 1)) * nombreCasesTotal);
		return maxAutorise;
	}

	// --------------------------------------------------------------------------
	// Taille des bateaux
	// --------------------------------------------------------------------------

	/**
	 *	Détermine la taille maximale d'un bateau selon les contraintes.
	 *
	 *	@param nombreCasesRestant Nombre de cases encore disponibles
	 *	@param proposition Taille proposée
	 *	@param hauteur Hauteur de la grille
	 *	@param largeur Largeur de la grille
	 *	@return Taille maximale autorisée
	 */
	public int tailleMaxBateau(int nombreCasesRestant, int proposition, int hauteur, int largeur) {
		int maxHauteurLargeur = Math.min(hauteur, largeur);

		if (proposition < nombreCasesRestant) {
			return (proposition > maxHauteurLargeur) ? maxHauteurLargeur : proposition;
		}
		return nombreCasesRestant;
	}

	/**
	 *	Récupère les bateaux d'un joueur via le menu.
	 *	Chaque élément du tableau contient le nombre de cases d'un bateau.
	 *
	 *	@param nombreJoueurs Nombre de joueurs dans la partie
	 *	@param hauteur Hauteur de la grille
	 *	@param largeur Largeur de la grille
	 *	@return Tableau des tailles de bateaux
	 */
	public int[] donneLesBateaux(int nombreJoueurs, int hauteur, int largeur) {
		int nombrePlaceMax = nombreDePlaceDeBateauMax(nombreJoueurs, hauteur, largeur);
		int[] resultat = menu.entre_nombre_de_case_bateau(nombrePlaceMax);
		return resultat;
	}

	// ==========================================================================
	// Fonctions principales - Combat
	// ==========================================================================

	/**
	 *	Enregistre une attaque sur le bateau.
	 *	Décrémente le nombre de cases restantes.
	 */
	public void attaque() {
		this.nombreDeCasesRestantes = this.nombreDeCasesRestantes - 1;
	}

	// ==========================================================================
	// Fonctions d'affichage
	// ==========================================================================

	/**
	 *	Retourne la représentation textuelle du bateau.
	 *
	 *	@return Chaîne au format "BX" où X est le numéro du joueur
	 */
	public String toString() {
		return "B" + joueur;
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
