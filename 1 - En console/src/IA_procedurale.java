// ==============================================================================
// Classe IA_procedurale
// ==============================================================================

/**
	IA procedurale pour le jeu de bataille navale.
	Deux modes : RECHERCHE (tir en diagonale) et PONCAGE (exploitation des touches).
*/
class IA_procedurale {

// ==============================================================================
// Constantes
// ==============================================================================

	public static final int MODE_RECHERCHE = 0;
	public static final int MODE_PONCAGE = 1;

	public static final int DIRECTION_INCONNUE = 0;
	public static final int DIRECTION_HAUT = 1;
	public static final int DIRECTION_BAS = 2;
	public static final int DIRECTION_GAUCHE = 3;
	public static final int DIRECTION_DROITE = 4;

	public static final int RESULTAT_EAU = 0;
	public static final int RESULTAT_TOUCHE = 1;
	public static final int RESULTAT_COULE = 2;

// ==============================================================================
// Donnees
// ==============================================================================

	Grille grille;
	int mode_actuel;
	Case case_touchee_initiale;
	int direction_actuelle;
	Case[] cases_touchees_bateau_en_cours;
	int nombre_cases_touchees;
	boolean[] directions_essayees;

// ==============================================================================
// Constructeur
// ==============================================================================

	/**
		Constructeur de l'IA procedurale.

		@param grille_jeu Grille de jeu
	*/
	public IA_procedurale(Grille grille_jeu) {
		this.grille = grille_jeu;
		reinitialiser();
	}

	/**
		Reinitialise l'etat de l'IA (retour en mode recherche).
	*/
	public void reinitialiser() {
		mode_actuel = MODE_RECHERCHE;
		case_touchee_initiale = null;
		direction_actuelle = DIRECTION_INCONNUE;
		cases_touchees_bateau_en_cours = new Case[grille.hauteur * grille.largeur];
		nombre_cases_touchees = 0;
		directions_essayees = new boolean[5];
	}

// ==============================================================================
// Fonction principale - Choix de case
// ==============================================================================

	/**
		Choisit la prochaine case a tirer.

		@param joueur_attaquant Joueur qui attaque
		@param resultat_dernier_tir Resultat du tir precedent (-1 si premier tir)
		@param derniere_case_tiree Case du dernier tir (null si premier tir)
		@return Case a tirer
	*/
	public Case choisir_case(Joueur joueur_attaquant, int resultat_dernier_tir, Case derniere_case_tiree) {
		mettreAJourEtat(resultat_dernier_tir, derniere_case_tiree);

		if (mode_actuel == MODE_RECHERCHE) {
			return choisirCaseRecherche(joueur_attaquant);
		} else {
			return choisirCasePoncage(joueur_attaquant);
		}
	}

// ==============================================================================
// Gestion des transitions d'etat
// ==============================================================================

	/**
		Met a jour l'etat de l'IA apres un tir.

		@param resultat Resultat du tir
		@param case_tiree Case qui a ete tiree
	*/
	private void mettreAJourEtat(int resultat, Case case_tiree) {
		if (resultat == -1) {
			return;
		}

		if (resultat == RESULTAT_COULE) {
			reinitialiser();
			return;
		}

		if (resultat == RESULTAT_TOUCHE) {
			gererTouche(case_tiree);
			return;
		}

		if (resultat == RESULTAT_EAU && mode_actuel == MODE_PONCAGE) {
			gererEauEnPoncage();
		}
	}

	/**
		Gere le cas ou on touche un bateau.

		@param case_tiree Case touchee
	*/
	private void gererTouche(Case case_tiree) {
		if (mode_actuel == MODE_RECHERCHE) {
			mode_actuel = MODE_PONCAGE;
			case_touchee_initiale = case_tiree;
			direction_actuelle = DIRECTION_INCONNUE;
			nombre_cases_touchees = 0;
			directions_essayees = new boolean[5];
		} else {
			detecterDirection(case_tiree);
		}
		ajouterCaseTouchee(case_tiree);
	}

	/**
		Detecte la direction du bateau a partir de deux cases touchees.
		Corrige la direction si elle etait incorrecte.

		@param nouvelle_case Nouvelle case touchee
	*/
	private void detecterDirection(Case nouvelle_case) {
		if (case_touchee_initiale == null) {
			return;
		}

		int diff_ordonnee = nouvelle_case.ordonnee - case_touchee_initiale.ordonnee;
		int diff_abscisse = nouvelle_case.abscisse - case_touchee_initiale.abscisse;

		int nouvelle_direction = DIRECTION_INCONNUE;
		if (diff_ordonnee < 0) {
			nouvelle_direction = DIRECTION_HAUT;
		} else if (diff_ordonnee > 0) {
			nouvelle_direction = DIRECTION_BAS;
		} else if (diff_abscisse < 0) {
			nouvelle_direction = DIRECTION_GAUCHE;
		} else if (diff_abscisse > 0) {
			nouvelle_direction = DIRECTION_DROITE;
		}

		if (nouvelle_direction != DIRECTION_INCONNUE) {
			direction_actuelle = nouvelle_direction;
		}
	}

	/**
		Ajoute une case a la liste des cases touchees du bateau en cours.

		@param case_touchee Case touchee
	*/
	private void ajouterCaseTouchee(Case case_touchee) {
		if (nombre_cases_touchees < cases_touchees_bateau_en_cours.length) {
			cases_touchees_bateau_en_cours[nombre_cases_touchees] = case_touchee;
			nombre_cases_touchees++;
		}
	}

	/**
		Gere le cas ou on tombe dans l'eau en mode poncage.
	*/
	private void gererEauEnPoncage() {
		directions_essayees[direction_actuelle] = true;
		direction_actuelle = DIRECTION_INCONNUE;
	}

// ==============================================================================
// Mode RECHERCHE - Tir en diagonale
// ==============================================================================

	/**
		Choisit une case en mode recherche (pattern diagonale).

		@param joueur_attaquant Joueur qui attaque
		@return Case a tirer
	*/
	private Case choisirCaseRecherche(Joueur joueur_attaquant) {
		int[][] plateau_tentees = joueur_attaquant.genere_plateau_cases_tentees(joueur_attaquant);

		Case case_diagonale = trouverCaseDiagonale(plateau_tentees);
		if (case_diagonale != null) {
			return case_diagonale;
		}

		return trouverPremiereCaseLibre(plateau_tentees);
	}

	/**
		Trouve une case libre sur le pattern diagonale.

		@param plateau_tentees Plateau des cases tentees
		@return Case sur la diagonale ou null
	*/
	private Case trouverCaseDiagonale(int[][] plateau_tentees) {
		for (int ligne = 0; ligne < grille.hauteur; ligne++) {
			for (int colonne = 0; colonne < grille.largeur; colonne++) {
				if (estCaseDiagonale(ligne, colonne) && estCaseLibre(plateau_tentees, ligne, colonne)) {
					return new Case(ligne + 1, colonne + 1);
				}
			}
		}
		return null;
	}

	/**
		Verifie si une case est sur le pattern diagonale.

		@param ligne Ligne (0-indexed)
		@param colonne Colonne (0-indexed)
		@return true si sur la diagonale
	*/
	private boolean estCaseDiagonale(int ligne, int colonne) {
		return ((ligne + colonne) % 2 == 0);
	}

	/**
		Verifie si une case est libre (non tentee).

		@param plateau_tentees Plateau des cases tentees
		@param ligne Ligne (0-indexed)
		@param colonne Colonne (0-indexed)
		@return true si case libre
	*/
	private boolean estCaseLibre(int[][] plateau_tentees, int ligne, int colonne) {
		return (plateau_tentees[ligne][colonne] == -1);
	}

	/**
		Trouve la premiere case libre sur le plateau.

		@param plateau_tentees Plateau des cases tentees
		@return Premiere case libre
	*/
	private Case trouverPremiereCaseLibre(int[][] plateau_tentees) {
		for (int ligne = 0; ligne < grille.hauteur; ligne++) {
			for (int colonne = 0; colonne < grille.largeur; colonne++) {
				if (estCaseLibre(plateau_tentees, ligne, colonne)) {
					return new Case(ligne + 1, colonne + 1);
				}
			}
		}
		return null;
	}

// ==============================================================================
// Mode PONCAGE - Exploitation des touches
// ==============================================================================

	/**
		Choisit une case en mode poncage.

		@param joueur_attaquant Joueur qui attaque
		@return Case a tirer
	*/
	private Case choisirCasePoncage(Joueur joueur_attaquant) {
		int[][] plateau_tentees = joueur_attaquant.genere_plateau_cases_tentees(joueur_attaquant);

		if (direction_actuelle == DIRECTION_INCONNUE) {
			return choisirDirectionInitiale(plateau_tentees);
		} else {
			return continuerDansDirection(plateau_tentees);
		}
	}

	/**
		Choisit une direction initiale apres un premier touche.

		@param plateau_tentees Plateau des cases tentees
		@return Case adjacente a tirer
	*/
	private Case choisirDirectionInitiale(int[][] plateau_tentees) {
		int[] directions = {DIRECTION_DROITE, DIRECTION_BAS, DIRECTION_GAUCHE, DIRECTION_HAUT};

		for (int index = 0; index < directions.length; index++) {
			int direction = directions[index];
			if (!directions_essayees[direction]) {
				Case case_adjacente = calculerCaseAdjacente(case_touchee_initiale, direction);
				if (estCaseValideEtLibre(case_adjacente, plateau_tentees)) {
					direction_actuelle = direction;
					return case_adjacente;
				}
				directions_essayees[direction] = true;
			}
		}

		reinitialiser();
		return trouverPremiereCaseLibreDepuisPlateau(plateau_tentees);
	}

	/**
		Trouve la premiere case libre depuis un plateau deja calcule.

		@param plateau_tentees Plateau des cases tentees
		@return Premiere case libre sur diagonale ou ailleurs
	*/
	private Case trouverPremiereCaseLibreDepuisPlateau(int[][] plateau_tentees) {
		Case case_diagonale = trouverCaseDiagonale(plateau_tentees);
		if (case_diagonale != null) {
			return case_diagonale;
		}
		return trouverPremiereCaseLibre(plateau_tentees);
	}

	/**
		Continue dans la direction actuelle ou inverse.

		@param plateau_tentees Plateau des cases tentees
		@return Case suivante a tirer
	*/
	private Case continuerDansDirection(int[][] plateau_tentees) {
		Case derniere_touchee = obtenirDerniereCaseTouchee();
		Case case_suivante = calculerCaseAdjacente(derniere_touchee, direction_actuelle);

		if (estCaseValideEtLibre(case_suivante, plateau_tentees)) {
			return case_suivante;
		}

		int direction_inverse = inverserDirection(direction_actuelle);
		directions_essayees[direction_actuelle] = true;

		if (!directions_essayees[direction_inverse]) {
			direction_actuelle = direction_inverse;
			Case case_inverse = calculerCaseAdjacente(case_touchee_initiale, direction_inverse);
			if (estCaseValideEtLibre(case_inverse, plateau_tentees)) {
				return case_inverse;
			}
		}

		return choisirDirectionInitiale(plateau_tentees);
	}

	/**
		Retourne la derniere case touchee du bateau en cours.

		@return Derniere case touchee
	*/
	private Case obtenirDerniereCaseTouchee() {
		if (nombre_cases_touchees > 0) {
			return cases_touchees_bateau_en_cours[nombre_cases_touchees - 1];
		}
		return case_touchee_initiale;
	}

// ==============================================================================
// Fonctions utilitaires - Calcul de cases
// ==============================================================================

	/**
		Calcule la case adjacente dans une direction.

		@param case_origine Case de depart
		@param direction Direction (HAUT, BAS, GAUCHE, DROITE)
		@return Case adjacente
	*/
	private Case calculerCaseAdjacente(Case case_origine, int direction) {
		int nouvelle_ordonnee = case_origine.ordonnee;
		int nouvelle_abscisse = case_origine.abscisse;

		if (direction == DIRECTION_HAUT) {
			nouvelle_ordonnee = case_origine.ordonnee - 1;
		} else if (direction == DIRECTION_BAS) {
			nouvelle_ordonnee = case_origine.ordonnee + 1;
		} else if (direction == DIRECTION_GAUCHE) {
			nouvelle_abscisse = case_origine.abscisse - 1;
		} else if (direction == DIRECTION_DROITE) {
			nouvelle_abscisse = case_origine.abscisse + 1;
		}

		return new Case(nouvelle_ordonnee, nouvelle_abscisse);
	}

	/**
		Verifie si une case est valide (dans la grille) et libre.

		@param case_a_verifier Case a verifier
		@param plateau_tentees Plateau des cases tentees
		@return true si valide et libre
	*/
	private boolean estCaseValideEtLibre(Case case_a_verifier, int[][] plateau_tentees) {
		if (!estCaseValide(case_a_verifier)) {
			return false;
		}
		int ligne = case_a_verifier.ordonnee - 1;
		int colonne = case_a_verifier.abscisse - 1;
		return estCaseLibre(plateau_tentees, ligne, colonne);
	}

	/**
		Verifie si une case est dans les limites de la grille.

		@param case_a_verifier Case a verifier
		@return true si dans la grille
	*/
	private boolean estCaseValide(Case case_a_verifier) {
		return (case_a_verifier.ordonnee >= 1 && case_a_verifier.ordonnee <= grille.hauteur
			&& case_a_verifier.abscisse >= 1 && case_a_verifier.abscisse <= grille.largeur);
	}

	/**
		Inverse une direction.

		@param direction Direction a inverser
		@return Direction inverse
	*/
	private int inverserDirection(int direction) {
		if (direction == DIRECTION_HAUT) return DIRECTION_BAS;
		if (direction == DIRECTION_BAS) return DIRECTION_HAUT;
		if (direction == DIRECTION_GAUCHE) return DIRECTION_DROITE;
		if (direction == DIRECTION_DROITE) return DIRECTION_GAUCHE;
		return DIRECTION_INCONNUE;
	}

// ==============================================================================
// Fonctions utilitaires
// ==============================================================================

	/**
		Affiche un message avec saut de ligne.

		@param message Message a afficher
	*/
	public void aff(String message) {
		System.out.println(message);
	}

	/**
		Affiche un message sans saut de ligne.

		@param message Message a afficher
	*/
	public void affnn(String message) {
		System.out.print(message);
	}
}
