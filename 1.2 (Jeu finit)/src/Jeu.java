// ==============================================================================
// Classe Jeu
// ==============================================================================

/**
 *	Classe principale du jeu de bataille navale.
 *	Gère le déroulement complet d'une partie.
 */
class Jeu {

	// ==========================================================================
	// Données
	// ==========================================================================

	Menu menu;
	Affichage affichage;
	Utilitaire utilitaire;
	Grille grille;
	Joueur[] joueurs;
	IA ia;

	// ==========================================================================
	// Constructeur
	// ==========================================================================

	/**
	 *	Constructeur du jeu.
	 *
	 *	@param hauteur Hauteur de la grille
	 *	@param largeur Largeur de la grille
	 *	@param nombreJoueurs Nombre de joueurs
	 */
	public Jeu(int hauteur, int largeur, int nombreJoueurs) {
		initialiserComposants(hauteur, largeur);
		int choix = menu.menu_principal();
		if (choix == 2) {
			nombreJoueurs = menu.entre_nombre_de_joueurs();
		}
		initialisation_du_jeu(nombreJoueurs);
		jeu(nombreJoueurs);
		if (choix == 3) {
			System.exit(0);
		}
	}

	/**
	 *	Initialise les composants du jeu.
	 */
	private void initialiserComposants(int hauteur, int largeur) {
		menu = new Menu();
		affichage = new Affichage();
		utilitaire = new Utilitaire();
		grille = new Grille(hauteur, largeur);
		ia = new IA(grille);
	}

	// ==========================================================================
	// Fonctions principales - Initialisation
	// ==========================================================================

	/**
	 *	Initialise le jeu avec tous les joueurs.
	 *
	 *	@param nombreJoueurs Nombre de joueurs
	 */
	public void initialisation_du_jeu(int nombreJoueurs) {
		joueurs = new Joueur[nombreJoueurs];
		for (int index = 0; index < nombreJoueurs; index++) {
			joueurs[index] = initialiserJoueur(index + 1);
			affichage.afficherCarteJoueur(grille, joueurs[index]);
		}
	}

	/**
	 *	Initialise un joueur.
	 */
	private Joueur initialiserJoueur(int numeroJoueur) {
		boolean estIA = menu.IA_humain(numeroJoueur);
		boolean carteAMontrer = !estIA;
		boolean placementAuto = true;
		return placement_bateaux(numeroJoueur, placementAuto, estIA, carteAMontrer);
	}

	/**
	 *	Place les bateaux d'un joueur.
	 */
	public Joueur placement_bateaux(int numeroJoueur, boolean placementAuto, boolean estIA, boolean carteAMontrer) {
		if (!placementAuto) {
			return new Joueur(numeroJoueur, grille, estIA, carteAMontrer);
		}
		return ia.place_bateaux_IA(grille, numeroJoueur, estIA, carteAMontrer);
	}

	// ==========================================================================
	// Fonctions principales - Boucle de jeu
	// ==========================================================================

	/**
	 *	Lance la boucle principale du jeu.
	 */
	public void jeu(int nombreJoueurs) {
		int nombreJoueursAyantPerdu = 0;
		boolean[] joueurElimine = new boolean[nombreJoueurs];
		boolean finDuJeu = false;

		while (!finDuJeu) {
			finDuJeu = executerTour(nombreJoueurs, joueurElimine);
		}
		afficherResultatsFinaux(nombreJoueurs);
	}

	/**
	 *	Exécute un tour de jeu complet.
	 */
	private boolean executerTour(int nombreJoueurs, boolean[] joueurElimine) {
		for (int indexAttaquant = 0; indexAttaquant < nombreJoueurs; indexAttaquant++) {
			for (int indexCible = 0; indexCible < nombreJoueurs; indexCible++) {
				if (indexAttaquant != indexCible) {
					boolean finPartie = executerAttaque(indexAttaquant, indexCible, joueurElimine, nombreJoueurs);
					if (finPartie) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 *	Exécute une attaque d'un joueur vers un autre.
	 */
	private boolean executerAttaque(int indexAttaquant, int indexCible, boolean[] joueurElimine, int nombreJoueurs) {
		boolean impossible = effectuerTir(indexAttaquant, indexCible);
		utilitaire.afficher_evenement_coup(joueurs[indexCible].derniere_case_tentee);
		affichage.afficherCarteJoueur(grille, joueurs[indexCible]);

		gererEliminations(indexAttaquant, indexCible, joueurElimine, impossible);
		return verifierFinJeu(joueurElimine, nombreJoueurs, impossible);
	}

	/**
	 *	Effectue le tir selon le type de joueur.
	 */
	private boolean effectuerTir(int indexAttaquant, int indexCible) {
		if (joueurs[indexAttaquant].IA_humain) {
			return !joueurs[indexAttaquant].tenter_une_case_IA(joueurs[indexCible]);
		}
		return !joueurs[indexAttaquant].tenter_une_case(joueurs[indexCible]);
	}

	// --------------------------------------------------------------------------
	// Gestion des éliminations
	// --------------------------------------------------------------------------

	/**
	 *	Gère les éliminations de joueurs.
	 */
	private void gererEliminations(int indexAttaquant, int indexCible, boolean[] joueurElimine, boolean impossible) {
		if (impossible) {
			joueurs[indexAttaquant].perd();
			if (!joueurElimine[indexAttaquant]) {
				joueurElimine[indexAttaquant] = true;
			}
		}
		if (joueurs[indexCible].a_perdu() && !joueurElimine[indexCible]) {
			joueurElimine[indexCible] = true;
		}
	}

	/**
	 *	Vérifie si le jeu est terminé.
	 */
	private boolean verifierFinJeu(boolean[] joueurElimine, int nombreJoueurs, boolean impossible) {
		int nombreElimines = compterElimines(joueurElimine);
		if (nombreElimines == nombreJoueurs - 1 || impossible) {
			afficherMessageFin();
			return true;
		}
		return false;
	}

	/**
	 *	Compte les joueurs éliminés.
	 */
	private int compterElimines(boolean[] joueurElimine) {
		int compte = 0;
		for (int index = 0; index < joueurElimine.length; index++) {
			if (joueurElimine[index]) {
				compte++;
			}
		}
		return compte;
	}

	/**
	 *	Affiche le message de fin de partie.
	 */
	private void afficherMessageFin() {
		aff("\nFin du jeu");
		int gagnant = utilitaire.recherche_gagnant(this.joueurs) + 1;
		if (gagnant > 0) {
			aff("\nLe gagnant est le joueur " + gagnant + ".\n");
		} else {
			aff("\nIl n'y a aucun gagnant.\n");
		}
	}

	// --------------------------------------------------------------------------
	// Affichage des résultats
	// --------------------------------------------------------------------------

	/**
	 *	Affiche les résultats finaux.
	 */
	private void afficherResultatsFinaux(int nombreJoueurs) {
		for (int index = 0; index < nombreJoueurs; index++) {
			joueurs[index].affiche_plateau_attaquant(joueurs[index]);
		}
		for (int index = 0; index < nombreJoueurs; index++) {
			joueurs[index].carte_a_montrer = true;
			affichage.afficherCarteJoueur(grille, joueurs[index]);
		}
	}

	// ==========================================================================
	// Main
	// ==========================================================================

	/**
	 *	Point d'entrée du programme.
	 */
	public static void main(String[] args) {
		Jeu jeu = new Jeu(4, 4, 2);
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
