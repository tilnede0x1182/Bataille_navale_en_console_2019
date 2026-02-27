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

	Utilitaire utilitaire;

	// ==========================================================================
	// Constructeur
	// ==========================================================================

	/**
	 *	Constructeur du menu.
	 */
	public Menu() {
		this.utilitaire = new Utilitaire();
	}

	// ==========================================================================
	// Fonctions principales - Menu
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
			aff("\t1 : Jeu humain");
			aff("\t2 : Jeu IA (ordinateur contre ordinateur)");
			aff("\t3 : Quitter");
			affnn("\n   ");
			resultat = entrerEntier("entre 1 et 3");
		}
		return resultat;
	}

	// ==========================================================================
	// Fonctions principales - Positionnement des bateaux
	// ==========================================================================

	/**
	 *	Demande le nombre de bateaux au joueur.
	 *
	 *	@param nombreCasesRestantes Nombre de cases disponibles
	 *	@return Nombre de bateaux choisi
	 */
	public int entreNombreDeBateaux(int nombreCasesRestantes) {
		int resultat = -1;
		while (resultat < 1 || resultat > nombreCasesRestantes) {
			resultat = entrerEntierPhrase("inférieur ou égal à " + nombreCasesRestantes + " et supérieur à 0");
		}
		return resultat;
	}

	/**
	 *	Demande le nombre de cases d'un bateau au joueur.
	 *
	 *	@param nombreCasesRestantes Nombre de cases disponibles
	 *	@param nombreBateauxRestants Nombre de bateaux restant à placer
	 *	@return Nombre de cases choisi
	 */
	public int entreNombreDeCaseBateau(int nombreCasesRestantes, int nombreBateauxRestants) {
		int resultat = -1;
		int maxAutorise = nombreCasesRestantes - (nombreBateauxRestants - 1);
		while (resultat < 1 || resultat > maxAutorise) {
			resultat = entrerEntierPhrase("inférieur ou égal à " + maxAutorise + " et supérieur à 0");
		}
		return resultat;
	}

	/**
	 *	Demande au joueur de définir tous ses bateaux.
	 *	Chaque élément du tableau contient le nombre de cases d'un bateau.
	 *
	 *	@param nombreCasesRestantes Nombre de cases disponibles
	 *	@return Tableau des tailles de bateaux
	 */
	public int[] entre_nombre_de_case_bateau(int nombreCasesRestantes) {
		int nombreCasesTemp = nombreCasesRestantes;
		int nombreBateauxRestants;

		aff("Il vous reste " + nombreCasesRestantes + " cases à occuper avec vos bateaux.");
		aff("Veuillez entrer le nombre de bateaux : ");
		int nombreBateaux = entreNombreDeBateaux(nombreCasesRestantes);
		nombreBateauxRestants = nombreBateaux;

		int[] resultat = new int[nombreBateaux];
		for (int index = 0; index < nombreBateaux; index++) {
			int tailleBateau = entreNombreDeCaseBateau(nombreCasesTemp, nombreBateauxRestants);
			resultat[index] = tailleBateau;
			nombreCasesTemp -= tailleBateau;
			nombreBateauxRestants--;
		}
		return resultat;
	}

	// --------------------------------------------------------------------------
	// Saisie des cases
	// --------------------------------------------------------------------------

	/**
	 *	Demande les cases de début et fin d'un bateau.
	 *
	 *	@param numeroBateau Numéro du bateau
	 *	@param nombreCasesBateau Nombre de cases du bateau
	 *	@param hauteur Hauteur de la grille
	 *	@return Tableau [ordonnée1, abscisse1, ordonnée2, abscisse2]
	 */
	public int[] donne_cases_bateau(int numeroBateau, int nombreCasesBateau, int hauteur) {
		String message = "Entrer la première case du bateau numéro " + numeroBateau + " à " + nombreCasesBateau + " case(s) : ";
		int[] case1 = entreCase(message, numeroBateau, hauteur);
		message = "Entrer la dernière case du bateau numéro " + numeroBateau + " à " + nombreCasesBateau + " case(s) : ";
		int[] caseFin = entreCase(message, numeroBateau, hauteur);

		int[] resultat = new int[4];
		resultat[0] = case1[0];
		resultat[1] = case1[1];
		resultat[2] = caseFin[0];
		resultat[3] = caseFin[1];
		return resultat;
	}

	/**
	 *	Demande une case au joueur avec validation du format.
	 *
	 *	@param message Message à afficher
	 *	@param numeroBateau Numéro du bateau concerné
	 *	@param hauteur Hauteur de la grille
	 *	@return Coordonnées [ordonnée, abscisse]
	 */
	public int[] entreCase(String message, int numeroBateau, int hauteur) {
		int tentative = 0;
		Scanner scanner = new Scanner(System.in);
		utilitaire = new Utilitaire();
		String reponse = "";

		aff(message);
		while (!utilitaire.verifie_format_case(reponse, hauteur)) {
			if (tentative > 0 && !utilitaire.verifie_format_case(reponse, hauteur)) {
				aff("Format de case incorrect");
			}
			reponse = scanner.nextLine();
			tentative++;
		}
		return utilitaire.convertit_case_en_coordonnee(reponse, hauteur);
	}

	// ==========================================================================
	// Fonctions utilitaires - Saisie
	// ==========================================================================

	/**
	 *	Demande un entier à l'utilisateur.
	 *
	 *	@param precision Message de précision sur la valeur attendue
	 *	@return Entier saisi
	 */
	public int entrerEntier(String precision) {
		Scanner scanner = new Scanner(System.in);
		utilitaire = new Utilitaire();
		String saisie = "";

		affnn("? = ");
		saisie = scanner.nextLine();
		while (!utilitaire.is_integer(saisie)) {
			aff("Veuillez entrer un entier " + precision + " : ");
			affnn("? = ");
			saisie = scanner.nextLine();
		}
		return Integer.parseInt(saisie);
	}

	/**
	 *	Demande un entier à l'utilisateur avec phrase explicative.
	 *
	 *	@param precision Message de précision
	 *	@return Entier saisi
	 */
	public int entrerEntierPhrase(String precision) {
		Scanner scanner = new Scanner(System.in);
		String saisie = "";

		aff("Veuillez entrer un entier " + precision + " : ");
		affnn("? = ");
		saisie = scanner.nextLine();
		while (!estEntier(saisie)) {
			aff("Veuillez entrer un entier " + precision + " : ");
			affnn("? = ");
			saisie = scanner.nextLine();
		}
		return Integer.parseInt(saisie);
	}

	/**
	 *	Vérifie si une chaîne représente un entier.
	 *
	 *	@param chaine Chaîne à vérifier
	 *	@return true si c'est un entier, false sinon
	 */
	public boolean estEntier(String chaine) {
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
