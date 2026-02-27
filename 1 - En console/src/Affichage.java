// ==============================================================================
// Classe Affichage
// ==============================================================================

/**
 *	Gère l'affichage des plateaux de jeu en console.
 */
class Affichage {

	// ==========================================================================
	// Données
	// ==========================================================================

	int nombreEspaces;

	// ==========================================================================
	// Constructeur
	// ==========================================================================

	/**
	 *	Constructeur de la classe Affichage.
	 */
	public Affichage() {
		this.nombreEspaces = 5;
	}

	// ==========================================================================
	// Fonctions principales - Affichage carte d'attaque
	// ==========================================================================

	/**
	 *	Affiche la carte d'attaque d'un joueur.
	 *	Légende : 0=non attaqué, X=eau, 1=touché, 2=coulé.
	 *
	 *	@param plateau Plateau d'attaque du joueur
	 *	@param numeroJoueur Numéro du joueur
	 */
	public void afficher_carte_attaque_joueur(int[][] plateau, int numeroJoueur) {
		afficherCarteAttaqueJoueur(plateau, numeroJoueur);
	}

	public void afficherCarteAttaqueJoueur(int[][] plateau, int numeroJoueur) {
		int hauteur = plateau.length;
		int largeur = plateau[0].length;

		afficherLegendeAttaque(numeroJoueur);
		afficherEnTeteColonnes(largeur);
		afficherLignesCarteAttaque(plateau, hauteur, largeur);
	}

	/**
	 *	Affiche la légende de la carte d'attaque.
	 */
	private void afficherLegendeAttaque(int numeroJoueur) {
		aff("\nLégende : \n\n  0 : case pas encore attaquée\n  X : Dans l'eau\n  1 : Touché\n  2 : Coulé");
		aff("\nTableau d'attaque du joueur " + numeroJoueur + " : \n");
	}

	/**
	 *	Affiche les lignes de la carte d'attaque.
	 */
	private void afficherLignesCarteAttaque(int[][] plateau, int hauteur, int largeur) {
		char lettreCoordonnee = 'A';

		afficheEspaces(nombreEspaces + 1);
		afficheSautsDeLigne(1);

		for (int ligne = 0; ligne < hauteur; ligne++) {
			affnn("" + (lettreCoordonnee++));
			afficheEspaces(nombreEspaces);
			afficherContenuLigneAttaque(plateau, ligne, largeur);
			afficheSautsDeLigne(2);
		}
	}

	/**
	 *	Affiche le contenu d'une ligne de la carte d'attaque.
	 */
	private void afficherContenuLigneAttaque(int[][] plateau, int ligne, int largeur) {
		for (int colonne = 0; colonne < largeur; colonne++) {
			String symbole = convertirEtatEnSymbole(plateau[ligne][colonne]);
			affnn(symbole);
			afficheEspaces(nombreEspaces);
		}
	}

	/**
	 *	Convertit un état numérique en symbole d'affichage.
	 */
	private String convertirEtatEnSymbole(int etat) {
		if (etat == -1) {
			return "0";
		}
		if (etat == 0) {
			return "X";
		}
		return "" + etat;
	}

	// ==========================================================================
	// Fonctions principales - Affichage carte joueur
	// ==========================================================================

	/**
	 *	Affiche la carte d'un joueur avec ses bateaux.
	 *
	 *	@param grille Grille de jeu
	 *	@param joueur Joueur dont on affiche la carte
	 */
	public void afficher_carte_joueur(Grille grille, Joueur joueur) {
		afficherCarteJoueur(grille, joueur);
	}

	public void afficherCarteJoueur(Grille grille, Joueur joueur) {
		if (!joueur.carte_a_montrer) {
			return;
		}
		int numeroJoueur = joueur.get_numero_du_joueur();
		Case[][] plateau = joueur.plateau;
		int hauteur = grille.hauteur;
		int largeur = grille.largeur;

		aff("\nTableau du joueur " + numeroJoueur + " : \n");
		afficherEnTeteColonnes(largeur);
		afficherLignesCarteJoueur(plateau, hauteur, largeur);
	}

	/**
	 *	Affiche les lignes de la carte d'un joueur.
	 */
	private void afficherLignesCarteJoueur(Case[][] plateau, int hauteur, int largeur) {
		char lettreCoordonnee = 'A';

		afficheSautsDeLigne(1);
		for (int ligne = 0; ligne < hauteur; ligne++) {
			affnn("" + (lettreCoordonnee++));
			afficheEspaces(nombreEspaces);
			afficherContenuLigneJoueur(plateau, ligne, largeur);
			afficheSautsDeLigne(2);
		}
	}

	/**
	 *	Affiche le contenu d'une ligne de la carte joueur.
	 */
	private void afficherContenuLigneJoueur(Case[][] plateau, int ligne, int largeur) {
		for (int colonne = 0; colonne < largeur; colonne++) {
			String contenu = "0";
			int tailleContenu = 1;
			if (plateau != null) {
				contenu = plateau[ligne][colonne].toString();
				tailleContenu = contenu.length();
			}
			affnn(contenu);
			afficheEspaces(nombreEspaces - (tailleContenu - 1));
		}
	}

	// ==========================================================================
	// Fonctions utilitaires d'affichage
	// ==========================================================================

	/**
	 *	Affiche l'en-tête des colonnes (numéros).
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
	 *	Affiche un nombre donné d'espaces.
	 *
	 *	@param nombreEspacesAAfficher Nombre d'espaces
	 */
	public void afficheEspaces(int nombreEspacesAAfficher) {
		for (int index = 0; index < nombreEspacesAAfficher; index++) {
			affnn(" ");
		}
	}

	/**
	 *	Affiche un nombre donné de sauts de ligne.
	 *
	 *	@param nombreLignes Nombre de sauts de ligne
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
