// ==============================================================================
// Classe Jeu
// ==============================================================================

/**
 *	Classe principale du jeu de bataille navale.
 *	Gère le déroulement de la partie et le menu principal.
 */
class Jeu {

	// ==========================================================================
	// Données
	// ==========================================================================

	Grille grille;
	int nombreDeJoueurs;
	Utilitaire utilitaire;
	Affichage affichage;
	Menu menu;

	// ==========================================================================
	// Constructeur
	// ==========================================================================

	/**
	 *	Constructeur du jeu. Initialise la grille et lance le menu.
	 *
	 *	@param hauteur Hauteur de la grille
	 *	@param largeur Largeur de la grille
	 *	@param nombreJoueurs Nombre de joueurs
	 */
	public Jeu(int hauteur, int largeur, int nombreJoueurs) {
		this.utilitaire = new Utilitaire();
		this.menu = new Menu();
		this.nombreDeJoueurs = nombreJoueurs;
		this.grille = new Grille(hauteur, largeur);
		this.affichage = new Affichage(grille);

		affichage.affiche();
		lancerMenuPrincipal();
	}

	// ==========================================================================
	// Fonctions principales
	// ==========================================================================

	/**
	 *	Lance le menu principal en boucle jusqu'à ce que le joueur quitte.
	 */
	private void lancerMenuPrincipal() {
		int choix;
		while (true) {
			choix = menu.menu_principal();
			if (choix == 3) {
				System.exit(0);
			}
		}
	}

	/**
	 *	Initialise le jeu (à compléter).
	 */
	public void initialisationDuJeu() {
		// À implémenter
	}

	/**
	 *	Lance une partie (à compléter).
	 */
	public void partie() {
		// À implémenter
	}

	// --------------------------------------------------------------------------
	// Placement des bateaux
	// --------------------------------------------------------------------------

	/**
	 *	Place un bateau sur la grille (à compléter).
	 *
	 *	@param caseDebut Case de début du bateau
	 *	@param caseFin Case de fin du bateau
	 *	@param nombreCases Nombre de cases du bateau
	 */
	public void placerUnBateau(int caseDebut, int caseFin, int nombreCases) {
		// À implémenter
	}

	// ==========================================================================
	// Main
	// ==========================================================================

	/**
	 *	Point d'entrée du programme.
	 *
	 *	@param args Arguments de la ligne de commande
	 */
	public static void main(String[] args) {
		Jeu jeu = new Jeu(5, 5, 1);
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
