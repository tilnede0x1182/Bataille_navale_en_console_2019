// ==============================================================================
// Classe Affichage
// ==============================================================================

/**
 *	Gère l'affichage de la grille de jeu en console.
 */
class Affichage {

	// ==========================================================================
	// Données
	// ==========================================================================

	Grille grille;
	int nombreEspaces;

	// ==========================================================================
	// Constructeur
	// ==========================================================================

	/**
	 *	Constructeur de la classe Affichage.
	 *
	 *	@param grilleJeu La grille de jeu à afficher
	 */
	public Affichage(Grille grilleJeu) {
		this.grille = grilleJeu;
		this.nombreEspaces = 3;
	}

	// ==========================================================================
	// Fonctions principales
	// ==========================================================================

	/**
	 *	Affiche la grille de jeu complète avec les coordonnées.
	 */
	public void affiche() {
		int hauteur = grille.hauteur;
		int largeur = grille.largeur;

		afficherEnTeteColonnes(largeur);
		afficherLignesGrille(hauteur, largeur);
	}

	// --------------------------------------------------------------------------
	// Affichage de la grille
	// --------------------------------------------------------------------------

	/**
	 *	Affiche l'en-tête des colonnes (numéros 1, 2, 3...).
	 *
	 *	@param largeur Nombre de colonnes de la grille
	 */
	private void afficherEnTeteColonnes(int largeur) {
		afficheEspaces(nombreEspaces + 1);
		for (int colonne = 0; colonne < largeur; colonne++) {
			affnn("" + (colonne + 1));
			int espaces = (colonne > 8) ? nombreEspaces - 1 : nombreEspaces;
			afficheEspaces(espaces);
		}
		afficheSautsDeLigne(1);
	}

	/**
	 *	Affiche toutes les lignes de la grille.
	 *
	 *	@param hauteur Nombre de lignes de la grille
	 *	@param largeur Nombre de colonnes de la grille
	 */
	private void afficherLignesGrille(int hauteur, int largeur) {
		char lettreCoordonnee = 'A';

		afficheSautsDeLigne(1);
		for (int ligne = 0; ligne < hauteur; ligne++) {
			affnn("" + (lettreCoordonnee++));
			afficheEspaces(nombreEspaces);
			afficherContenuLigne(ligne, largeur);
			afficheSautsDeLigne(2);
		}
	}

	/**
	 *	Affiche le contenu d'une ligne de la grille.
	 *
	 *	@param ligne Index de la ligne à afficher
	 *	@param largeur Nombre de colonnes de la grille
	 */
	private void afficherContenuLigne(int ligne, int largeur) {
		for (int colonne = 0; colonne < largeur; colonne++) {
			if (grille.grille[ligne][colonne] != null) {
				affnn(grille.grille[ligne][colonne].toString());
				afficheEspaces(1);
			} else {
				affnn("0");
				afficheEspaces(nombreEspaces);
			}
		}
	}

	// ==========================================================================
	// Fonctions utilitaires d'affichage
	// ==========================================================================

	/**
	 *	Affiche un nombre donné d'espaces.
	 *
	 *	@param nombreEspacesAAfficher Nombre d'espaces à afficher
	 */
	public void afficheEspaces(int nombreEspacesAAfficher) {
		for (int index = 0; index < nombreEspacesAAfficher; index++) {
			affnn(" ");
		}
	}

	/**
	 *	Affiche un nombre donné de sauts de ligne.
	 *
	 *	@param nombreLignes Nombre de sauts de ligne à afficher
	 */
	public void afficheSautsDeLigne(int nombreLignes) {
		for (int index = 0; index < nombreLignes; index++) {
			affnn("\n");
		}
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
