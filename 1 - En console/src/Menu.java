// ==============================================================================
// Classe Menu
// ==============================================================================

import java.util.Scanner;

/**
 *	Gère les menus et les saisies utilisateur du jeu.
 */
class Menu {

	// ==========================================================================
	// Données
	// ==========================================================================

	private static final Scanner INPUT = new Scanner(System.in);
	Utilitaire utilitaire;
	Case caseValidation;

	// ==========================================================================
	// Constructeur
	// ==========================================================================

	/**
	 *	Constructeur du menu.
	 */
	public Menu() {
		this.caseValidation = new Case();
		this.utilitaire = new Utilitaire();
	}

	// ==========================================================================
	// Fonctions principales - Menus
	// ==========================================================================

	/**
	 *	Affiche et gère le menu principal.
	 *
	 *	@return Choix de l'utilisateur (1, 2 ou 3)
	 */
	public int menu_principal() {
		int resultat = -1;
		aff("\n   ******* Menu principal ******* \n");
		while (resultat != 1 && resultat != 2 && resultat != 3) {
			aff("\t1 : Jouer");
			aff("\t2 : Jouer à plus de deux joueurs");
			aff("\t3 : Quitter");
			affnn("\n   ");
			resultat = entrer_entier("entre 1 et 3");
		}
		return resultat;
	}

	/**
	 *	Menu de positionnement des bateaux.
	 */
	public int menu_positionnement_bateaux_1(int numeroJoueur) {
		int resultat = -1;
		aff("\n   ******* Menu joueur 2 ******* \n");
		aff("Le joueur " + numeroJoueur + " doit :");
		while (resultat != 1 && resultat != 2) {
			aff("\t1 : Placer ses bateau soi-même");
			aff("\t2 : Placer ses bateau aléatoirement");
			affnn("\n   ");
			resultat = entrer_entier("entre 1 et 2");
		}
		return resultat;
	}

	/**
	 *	Menu pour choisir IA ou humain.
	 */
	public int menu_IA_humain(int numeroJoueur) {
		int resultat = -1;
		aff("\n   ******* Menu joueur 1 ******* \n");
		aff("Le joueur " + numeroJoueur + " est un :");
		while (resultat != 1 && resultat != 2) {
			aff("\t1 : Joueur humain");
			aff("\t2 : joueur IA");
			affnn("\n   ");
			resultat = entrer_entier("entre 1 et 2");
		}
		return resultat;
	}

	/**
	 *	Retourne true si IA, false si humain.
	 */
	public boolean IA_humain(int numeroJoueur) {
		int choix = menu_IA_humain(numeroJoueur);
		return (choix == 2);
	}

	// ==========================================================================
	// Fonctions principales - Saisie des paramètres
	// ==========================================================================

	/**
	 *	Demande le nombre de joueurs.
	 */
	public int entre_nombre_de_joueurs() {
		int resultat = -1;
		aff("\nEntrer le nombre de joueurs :");
		while (resultat < 2) {
			resultat = entrer_entier(" supérieur ou égal à 2");
		}
		return resultat;
	}

	/**
	 *	Demande le nombre de bateaux.
	 */
	public int entre_nombre_de_bateaux(int nombreCasesRestantes) {
		int resultat = -1;
		while (resultat < 1 || resultat > nombreCasesRestantes) {
			aff("Veuillez entrer le nombre de bateaux :");
			resultat = entrer_entier_phrase("inférieur ou égal à " + nombreCasesRestantes + " et supérieur à 0");
		}
		return resultat;
	}

	// --------------------------------------------------------------------------
	// Saisie des tailles de bateaux
	// --------------------------------------------------------------------------

	/**
	 *	Demande le nombre de cases d'un bateau.
	 */
	public int entre_nombre_de_case_bateau(Grille grille, int numeroBateau, int nombreCasesRestantes, int nombreBateauxRestants) {
		int resultat = -1;
		Bateau bateauTemp = new Bateau();
		int maxAutorise = bateauTemp.taille_max_bateau(grille, nombreCasesRestantes - (nombreBateauxRestants - 1));
		while (resultat < 1 || resultat > maxAutorise) {
			aff("Entrer le nombre de cases du bateau " + numeroBateau + " : ");
			resultat = entrer_entier_phrase("inférieur ou égal à " + maxAutorise + " et supérieur à 0");
		}
		return resultat;
	}

	/**
	 *	Demande les tailles de tous les bateaux.
	 */
	public int[] entre_nombre_de_case_bateaux(Grille grille, int nombreCasesRestantes) {
		int nombreCasesTemp = nombreCasesRestantes;
		aff("Il vous reste " + nombreCasesRestantes + " cases à occuper avec vos bateaux.");
		int nombreBateaux = entre_nombre_de_bateaux(nombreCasesRestantes);
		int nombreBateauxRestants = nombreBateaux;

		int[] resultat = new int[nombreBateaux];
		for (int index = 0; index < nombreBateaux; index++) {
			int taille = entre_nombre_de_case_bateau(grille, (index + 1), nombreCasesTemp, nombreBateauxRestants);
			resultat[index] = taille;
			nombreCasesTemp -= taille;
			nombreBateauxRestants--;
		}
		return resultat;
	}

	// ==========================================================================
	// Fonctions principales - Saisie des cases
	// ==========================================================================

	/**
	 *	Demande les cases de début et fin d'un bateau.
	 */
	public int[] donne_cases_bateau(Grille grille, int numeroBateau, int nombreCasesBateau) {
		String message = "Entrer la première case du bateau numéro " + numeroBateau + " à " + nombreCasesBateau + " case(s) : ";
		int[] case1 = entre_case(grille, message);
		message = "Entrer la dernière case du bateau numéro " + numeroBateau + " à " + nombreCasesBateau + " case(s) : ";
		int[] caseFin = entre_case(grille, message);

		int[] resultat = new int[4];
		resultat[0] = case1[0];
		resultat[1] = case1[1];
		resultat[2] = caseFin[0];
		resultat[3] = caseFin[1];
		return resultat;
	}

	/**
	 *	Demande une case au joueur avec validation.
	 */
	public int[] entre_case(Grille grille, String message) {
		int tentative = 0;
		String reponse = "";
		affnn(message + "\n? = ");
		while (!caseValidation.verifie_case_existe(grille, reponse)) {
			if (tentative > 0) {
				affnn("Format de case incorrect\n? = ");
			}
			reponse = INPUT.nextLine();
			tentative++;
		}
		return caseValidation.convertit_case_en_coordonnee(grille, reponse);
	}

	// ==========================================================================
	// Fonctions utilitaires - Saisie d'entiers
	// ==========================================================================

	/**
	 *	Demande un entier à l'utilisateur.
	 */
	public int entrer_entier(String precision) {
		String saisie = "";
		affnn("? = ");
		saisie = INPUT.nextLine();
		while (!is_integer(saisie)) {
			aff("Veuillez entrer un entier " + precision + " : ");
			affnn("? = ");
			saisie = INPUT.nextLine();
		}
		return Integer.parseInt(saisie);
	}

	/**
	 *	Demande un entier avec phrase explicative.
	 */
	public int entrer_entier_phrase(String precision) {
		String saisie = "";
		aff("Veuillez entrer un entier " + precision + " : ");
		affnn("? = ");
		saisie = INPUT.nextLine();
		while (!is_integer(saisie)) {
			aff("Veuillez entrer un entier " + precision + " : ");
			affnn("? = ");
			saisie = INPUT.nextLine();
		}
		return Integer.parseInt(saisie);
	}

	/**
	 *	Vérifie si une chaîne représente un entier.
	 */
	public boolean is_integer(String chaine) {
		try {
			Integer.parseInt(chaine);
			return true;
		} catch (Exception erreur) {
			return false;
		}
	}

	// ==========================================================================
	// Fonctions utilitaires - Affichage
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
