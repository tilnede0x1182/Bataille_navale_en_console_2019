// ==============================================================================
// Classe Bateau
// ==============================================================================

/**
 *	Représente un bateau dans le jeu de bataille navale.
 *	Gère les cases occupées, l'état touché/coulé.
 */
class Bateau {

	// ==========================================================================
	// Données
	// ==========================================================================

	Menu menu;
	int nombre_de_cases;
	boolean touche;
	boolean coule;
	public Case[] cases;

	// ==========================================================================
	// Constructeurs
	// ==========================================================================

	/**
	 *	Constructeur par défaut.
	 */
	public Bateau() {
		this.menu = new Menu();
	}

	/**
	 *	Constructeur avec cases.
	 *
	 *	@param casesBateau Liste des cases occupées par le bateau
	 */
	public Bateau(Case[] casesBateau) {
		this.menu = new Menu();
		this.nombre_de_cases = casesBateau.length;
		this.cases = casesBateau;
		this.touche = false;
		this.coule = false;
	}

	// ==========================================================================
	// Fonctions principales - Placement des bateaux
	// ==========================================================================

	/**
	 *	Calcule le nombre maximum de cases pour les bateaux.
	 *	Formule : (3/5) * nombreCasesTotal
	 *
	 *	@param grille Grille de jeu
	 *	@return Nombre maximum de cases autorisées
	 */
	public int nombre_de_place_de_bateau_max(Grille grille) {
		int hauteur = grille.hauteur;
		int largeur = grille.largeur;
		int nombreCasesTotal = hauteur * largeur;
		return (int) ((3.0 / 5.0) * nombreCasesTotal);
	}

	/**
	 *	Détermine la taille maximale d'un bateau selon la grille.
	 *
	 *	@param grille Grille de jeu
	 *	@param proposition Taille proposée
	 *	@return Taille maximale autorisée
	 */
	public int taille_max_bateau(Grille grille, int proposition) {
		int maxHauteurLargeur = Math.min(grille.hauteur, grille.largeur);
		return (proposition > maxHauteurLargeur) ? maxHauteurLargeur : proposition;
	}

	/**
	 *	Récupère les tailles des bateaux via le menu.
	 *
	 *	@param grille Grille de jeu
	 *	@return Tableau des tailles de bateaux
	 */
	public int[] donne_nombre_de_cases_bateaux(Grille grille) {
		int nombrePlaceMax = nombre_de_place_de_bateau_max(grille);
		return menu.entre_nombre_de_case_bateaux(grille, nombrePlaceMax);
	}

	// ==========================================================================
	// Fonctions principales - Combat
	// ==========================================================================

	/**
	 *	Attaque une case du bateau.
	 *
	 *	@param caseAttaquee Case à attaquer
	 *	@return true si la case appartient au bateau
	 */
	public boolean attaque(Case caseAttaquee) {
		Case caseTemp = new Case();
		if (!caseAttaquee.contient_case(this.cases, caseAttaquee)) {
			return false;
		}
		attaquerCaseDuBateau(caseAttaquee);
		this.coule = verifie_coule();
		return true;
	}

	/**
	 *	Attaque la case correspondante dans le bateau.
	 */
	private void attaquerCaseDuBateau(Case caseAttaquee) {
		Case caseTemp = new Case();
		for (int index = 0; index < cases.length; index++) {
			if (caseTemp.compare_case(cases[index], caseAttaquee)) {
				cases[index].attaquer();
			}
		}
	}

	/**
	 *	Vérifie si le bateau est coulé (toutes cases attaquées).
	 *
	 *	@return true si coulé, false sinon
	 */
	public boolean verifie_coule() {
		for (int index = 0; index < cases.length; index++) {
			if (!cases[index].est_attaquee()) {
				return false;
			}
		}
		marquerToutesCasesCoulees();
		return true;
	}

	/**
	 *	Marque toutes les cases du bateau comme coulées.
	 */
	private void marquerToutesCasesCoulees() {
		for (int index = 0; index < cases.length; index++) {
			cases[index].couler();
		}
	}

	/**
	 *	Retourne l'état coulé du bateau.
	 *
	 *	@return true si coulé
	 */
	public boolean est_coule() {
		return coule;
	}

	// ==========================================================================
	// Fonctions utilitaires - Affichage debug
	// ==========================================================================

	/**
	 *	Affiche les cases du bateau.
	 */
	public void aff_cases() {
		for (int index = 0; index < cases.length; index++) {
			aff("Case " + (index + 1) + " : \n" + cases[index].aff_case_str());
		}
	}

	/**
	 *	Affiche l'état attaqué de chaque case.
	 */
	public void aff_cases_touchee() {
		for (int index = 0; index < cases.length; index++) {
			String etat = cases[index].est_attaquee() ? "est attaquée" : "n'est pas attaquée";
			aff("cases[" + (index + 1) + "] " + etat);
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
