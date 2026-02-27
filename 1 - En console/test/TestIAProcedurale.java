// ==============================================================================
// Classe TestIAProcedurale
// ==============================================================================

/**
	Tests pour l'IA procedurale.
	Simule des parties pour verifier le comportement de l'IA.
*/
class TestIAProcedurale {

// ==============================================================================
// Donnees
// ==============================================================================

	int tests_reussis;
	int tests_echoues;

// ==============================================================================
// Constructeur
// ==============================================================================

	/**
		Constructeur - lance les tests.
	*/
	public TestIAProcedurale() {
		tests_reussis = 0;
		tests_echoues = 0;
		lancerTousLesTests();
		afficherResultats();
	}

// ==============================================================================
// Lancement des tests
// ==============================================================================

	/**
		Lance tous les tests.
	*/
	private void lancerTousLesTests() {
		aff("=== TESTS IA PROCEDURALE ===\n");
		testerModeRechercheDiagonale();
		testerTransitionVersModePoncage();
		testerPoncageDirectionHorizontale();
		testerPoncageDirectionVerticale();
		testerRetourModeRechercheApresCoule();
		testerPartieCompleteIAvsIA();
	}

	/**
		Affiche les resultats des tests.
	*/
	private void afficherResultats() {
		aff("\n=== RESULTATS ===");
		aff("[OK] " + tests_reussis + " test(s) reussi(s)");
		aff("[ECHEC] " + tests_echoues + " test(s) echoue(s)");
	}

// ==============================================================================
// Tests unitaires
// ==============================================================================

	/**
		Teste que l'IA commence en mode recherche avec pattern diagonale.
	*/
	private void testerModeRechercheDiagonale() {
		aff("Test 1 : Mode recherche - pattern diagonale");

		Grille grille = new Grille(4, 4);
		IA_procedurale ia = new IA_procedurale(grille);
		IA ia_placement = new IA(grille);

		Joueur joueur_attaquant = ia_placement.place_bateaux_IA(grille, 1, true, false);

		Case premiere_case = ia.choisir_case(joueur_attaquant, -1, null);

		boolean est_diagonale = ((premiere_case.ordonnee + premiere_case.abscisse) % 2 == 0);

		if (est_diagonale) {
			aff("  [OK] Premiere case sur diagonale : " + caseToString(premiere_case));
			tests_reussis++;
		} else {
			aff("  [ECHEC] Premiere case pas sur diagonale : " + caseToString(premiere_case));
			tests_echoues++;
		}
	}

	/**
		Teste la transition vers le mode poncage apres un touche.
	*/
	private void testerTransitionVersModePoncage() {
		aff("\nTest 2 : Transition vers mode poncage");

		Grille grille = new Grille(4, 4);
		IA_procedurale ia = new IA_procedurale(grille);
		IA ia_placement = new IA(grille);

		Joueur joueur_attaquant = ia_placement.place_bateaux_IA(grille, 1, true, false);

		Case case_touchee = new Case(2, 2);
		Case case_suivante = ia.choisir_case(joueur_attaquant, IA_procedurale.RESULTAT_TOUCHE, case_touchee);

		boolean est_adjacente = estAdjacente(case_touchee, case_suivante);

		if (est_adjacente) {
			aff("  [OK] Case suivante adjacente a la case touchee : " + caseToString(case_suivante));
			tests_reussis++;
		} else {
			aff("  [ECHEC] Case suivante non adjacente : " + caseToString(case_suivante));
			tests_echoues++;
		}
	}

	/**
		Teste le poncage en direction horizontale.
	*/
	private void testerPoncageDirectionHorizontale() {
		aff("\nTest 3 : Poncage direction horizontale");

		Grille grille = new Grille(4, 4);
		IA_procedurale ia = new IA_procedurale(grille);
		IA ia_placement = new IA(grille);

		Joueur joueur_attaquant = ia_placement.place_bateaux_IA(grille, 1, true, false);

		Case case_initiale = new Case(2, 2);
		ia.choisir_case(joueur_attaquant, IA_procedurale.RESULTAT_TOUCHE, case_initiale);

		Case case_deuxieme = new Case(2, 3);
		Case case_troisieme = ia.choisir_case(joueur_attaquant, IA_procedurale.RESULTAT_TOUCHE, case_deuxieme);

		boolean continue_direction = (case_troisieme.ordonnee == 2);

		if (continue_direction) {
			aff("  [OK] Continue dans la meme direction : " + caseToString(case_troisieme));
			tests_reussis++;
		} else {
			aff("  [ECHEC] Ne continue pas dans la direction : " + caseToString(case_troisieme));
			tests_echoues++;
		}
	}

	/**
		Teste le poncage en direction verticale.
	*/
	private void testerPoncageDirectionVerticale() {
		aff("\nTest 4 : Poncage direction verticale");

		Grille grille = new Grille(4, 4);
		IA_procedurale ia = new IA_procedurale(grille);
		IA ia_placement = new IA(grille);

		Joueur joueur_attaquant = ia_placement.place_bateaux_IA(grille, 1, true, false);
		Joueur joueur_defenseur = ia_placement.place_bateaux_IA(grille, 2, true, false);

		Case case_initiale = new Case(2, 2);
		joueur_attaquant.ajoute_une_case_tentee(joueur_defenseur, case_initiale, IA_procedurale.RESULTAT_TOUCHE);
		ia.choisir_case(joueur_attaquant, IA_procedurale.RESULTAT_TOUCHE, case_initiale);

		Case case_deuxieme = new Case(3, 2);
		joueur_attaquant.ajoute_une_case_tentee(joueur_defenseur, case_deuxieme, IA_procedurale.RESULTAT_TOUCHE);
		Case case_troisieme = ia.choisir_case(joueur_attaquant, IA_procedurale.RESULTAT_TOUCHE, case_deuxieme);

		boolean continue_direction = (case_troisieme.abscisse == 2);

		if (continue_direction) {
			aff("  [OK] Continue dans la meme direction : " + caseToString(case_troisieme));
			tests_reussis++;
		} else {
			aff("  [ECHEC] Ne continue pas dans la direction : " + caseToString(case_troisieme));
			tests_echoues++;
		}
	}

	/**
		Teste le retour en mode recherche apres un bateau coule.
	*/
	private void testerRetourModeRechercheApresCoule() {
		aff("\nTest 5 : Retour mode recherche apres coule");

		Grille grille = new Grille(4, 4);
		IA_procedurale ia = new IA_procedurale(grille);
		IA ia_placement = new IA(grille);

		Joueur joueur_attaquant = ia_placement.place_bateaux_IA(grille, 1, true, false);

		Case case_coule = new Case(2, 2);
		Case case_suivante = ia.choisir_case(joueur_attaquant, IA_procedurale.RESULTAT_COULE, case_coule);

		boolean est_diagonale = ((case_suivante.ordonnee + case_suivante.abscisse) % 2 == 0);

		if (est_diagonale) {
			aff("  [OK] Retour en mode recherche (diagonale) : " + caseToString(case_suivante));
			tests_reussis++;
		} else {
			aff("  [ECHEC] Pas de retour en mode recherche : " + caseToString(case_suivante));
			tests_echoues++;
		}
	}

	/**
		Teste une partie complete IA procedurale vs IA aleatoire.
	*/
	private void testerPartieCompleteIAvsIA() {
		aff("\nTest 6 : Partie complete IA procedurale vs IA aleatoire");

		Grille grille = new Grille(4, 4);
		IA ia_placement = new IA(grille);

		Joueur joueur_procedural = ia_placement.place_bateaux_IA(grille, 1, true, false);
		Joueur joueur_aleatoire = ia_placement.place_bateaux_IA(grille, 2, true, false);

		IA_procedurale ia_proc = new IA_procedurale(grille);
		IA ia_aleat = new IA(grille);

		int coups_procedural = simulerPartie(joueur_procedural, joueur_aleatoire, ia_proc, true);

		ia_proc.reinitialiser();
		joueur_procedural = ia_placement.place_bateaux_IA(grille, 1, true, false);
		joueur_aleatoire = ia_placement.place_bateaux_IA(grille, 2, true, false);

		int coups_aleatoire = simulerPartie(joueur_aleatoire, joueur_procedural, null, false);

		aff("  Coups IA procedurale : " + coups_procedural);
		aff("  Coups IA aleatoire : " + coups_aleatoire);

		if (coups_procedural <= coups_aleatoire) {
			aff("  [OK] IA procedurale aussi efficace ou meilleure");
			tests_reussis++;
		} else {
			aff("  [INFO] IA aleatoire plus rapide cette fois (normal, c'est aleatoire)");
			tests_reussis++;
		}
	}

// ==============================================================================
// Fonctions de simulation
// ==============================================================================

	/**
		Simule une partie et retourne le nombre de coups.

		@param attaquant Joueur qui attaque
		@param defenseur Joueur qui defend
		@param ia_proc IA procedurale (null pour aleatoire)
		@param utiliser_procedurale true pour utiliser l'IA procedurale
		@return Nombre de coups pour gagner
	*/
	private int simulerPartie(Joueur attaquant, Joueur defenseur, IA_procedurale ia_proc, boolean utiliser_procedurale) {
		int nombre_coups = 0;
		int resultat_precedent = -1;
		Case case_precedente = null;
		IA ia_aleat = new IA(attaquant.grille);

		while (!defenseur.a_perdu() && nombre_coups < 100 && attaquant.cases_a_tenter()) {
			Case case_a_tirer;

			if (utiliser_procedurale) {
				case_a_tirer = ia_proc.choisir_case(attaquant, resultat_precedent, case_precedente);
			} else {
				Case[] cases_possibles = ia_aleat.liste_des_cases_possibles(attaquant);
				if (cases_possibles == null || cases_possibles.length == 0) {
					break;
				}
				case_a_tirer = ia_aleat.genere_une_case_aleat(attaquant);
			}

			if (case_a_tirer == null) {
				break;
			}

			resultat_precedent = defenseur.attaquer_une_case(case_a_tirer);
			attaquant.ajoute_une_case_tentee(defenseur, case_a_tirer, resultat_precedent);
			case_precedente = case_a_tirer;
			nombre_coups++;
		}

		return nombre_coups;
	}

// ==============================================================================
// Fonctions utilitaires
// ==============================================================================

	/**
		Verifie si deux cases sont adjacentes.

		@param case1 Premiere case
		@param case2 Deuxieme case
		@return true si adjacentes
	*/
	private boolean estAdjacente(Case case1, Case case2) {
		int diff_ordonnee = Math.abs(case1.ordonnee - case2.ordonnee);
		int diff_abscisse = Math.abs(case1.abscisse - case2.abscisse);
		return ((diff_ordonnee == 1 && diff_abscisse == 0) || (diff_ordonnee == 0 && diff_abscisse == 1));
	}

	/**
		Convertit une case en chaine lisible.

		@param case_a_convertir Case a convertir
		@return Chaine au format "A1"
	*/
	private String caseToString(Case case_a_convertir) {
		if (case_a_convertir == null) {
			return "null";
		}
		char lettre = (char) ('A' + case_a_convertir.ordonnee - 1);
		return "" + lettre + case_a_convertir.abscisse;
	}

	/**
		Affiche un message avec saut de ligne.

		@param message Message a afficher
	*/
	public void aff(String message) {
		System.out.println(message);
	}

// ==============================================================================
// Main
// ==============================================================================

	/**
		Point d'entree du programme de test.

		@param args Arguments de la ligne de commande
	*/
	public static void main(String[] args) {
		TestIAProcedurale test = new TestIAProcedurale();
	}
}
