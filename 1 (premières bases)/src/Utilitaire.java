// ==============================================================================
// Classe Utilitaire
// ==============================================================================

/**
 *	Classe utilitaire regroupant les fonctions communes au jeu.
 */
class Utilitaire {

	// ==========================================================================
	// Constructeur
	// ==========================================================================

	/**
	 *	Constructeur par défaut.
	 */
	public Utilitaire() {
	}

	// ==========================================================================
	// Fonctions principales - Validation
	// ==========================================================================

	/**
	 *	Vérifie le format d'une case (ex: A5).
	 *
	 *	@param chaine Chaîne à vérifier
	 *	@param hauteur Hauteur de la grille
	 *	@return true si le format est valide, false sinon
	 */
	public boolean verifie_format_case(String chaine, int hauteur) {
		if (chaine == null || chaine.length() != 2) {
			return false;
		}
		char lettre = chaine.charAt(0);
		char chiffre = chaine.charAt(1);

		boolean lettreValide = (lettre >= 'A' && lettre <= ('A' + (hauteur - 1)));
		boolean chiffreValide = is_integer("" + chiffre);

		return lettreValide && chiffreValide;
	}

	/**
	 *	Convertit une case au format "A5" en coordonnées [ordonnée, abscisse].
	 *
	 *	@param chaine Case au format lettre-chiffre
	 *	@param hauteur Hauteur de la grille
	 *	@return Tableau [ordonnée, abscisse] ou null si format invalide
	 */
	public int[] convertit_case_en_coordonnee(String chaine, int hauteur) {
		if (!verifie_format_case(chaine, hauteur)) {
			aff("convertit_case_en_coordonnee : Format incorrect");
			return null;
		}
		int[] resultat = new int[2];
		resultat[0] = (int) (chaine.charAt(0) - 'A' + 1);
		resultat[1] = (int) (chaine.charAt(1) - '0');
		return resultat;
	}

	// --------------------------------------------------------------------------
	// Comparaison de coordonnées
	// --------------------------------------------------------------------------

	/**
	 *	Compare deux paires de coordonnées.
	 *
	 *	@param ordonneeSource Ordonnée de la source
	 *	@param abscisseSource Abscisse de la source
	 *	@param ordonneeBut Ordonnée de la cible
	 *	@param abscisseBut Abscisse de la cible
	 *	@return true si les coordonnées sont identiques
	 */
	public boolean compareCoordonnee(int ordonneeSource, int abscisseSource, int ordonneeBut, int abscisseBut) {
		return (ordonneeSource == ordonneeBut && abscisseSource == abscisseBut);
	}

	// ==========================================================================
	// Fonctions principales - Placement des bateaux
	// ==========================================================================

	/**
	 *	Vérifie si le placement d'un bateau est valide.
	 *	Le bateau peut être horizontal ou vertical, pas diagonal.
	 *
	 *	@param case1 Coordonnées de la première case [ordonnée, abscisse]
	 *	@param caseFin Coordonnées de la dernière case [ordonnée, abscisse]
	 *	@param nombreCases Nombre de cases du bateau
	 *	@return true si le placement est valide, false sinon
	 */
	public boolean verifie_placement_bateau(int[] case1, int[] caseFin, int nombreCases) {
		if (estMemeCaseEtBateauUneCase(case1, caseFin, nombreCases)) {
			return true;
		}
		if (estPlacementHorizontalValide(case1, caseFin, nombreCases)) {
			return true;
		}
		if (estPlacementVerticalValide(case1, caseFin, nombreCases)) {
			return true;
		}
		return false;
	}

	/**
	 *	Vérifie si c'est un bateau d'une case sur la même position.
	 */
	private boolean estMemeCaseEtBateauUneCase(int[] case1, int[] caseFin, int nombreCases) {
		boolean memeCases = (case1[0] == caseFin[0] && case1[1] == caseFin[1]);
		return memeCases && nombreCases == 1;
	}

	/**
	 *	Vérifie si le placement horizontal est valide.
	 */
	private boolean estPlacementHorizontalValide(int[] case1, int[] caseFin, int nombreCases) {
		if (case1[0] != caseFin[0]) {
			return false;
		}
		int distance = Math.abs(caseFin[1] - case1[1]);
		return (distance == nombreCases && distance > 0);
	}

	/**
	 *	Vérifie si le placement vertical est valide.
	 */
	private boolean estPlacementVerticalValide(int[] case1, int[] caseFin, int nombreCases) {
		if (case1[1] != caseFin[1]) {
			return false;
		}
		int distance = Math.abs(caseFin[0] - case1[0]);
		return (distance == nombreCases && distance > 0);
	}

	// ==========================================================================
	// Fonctions utilitaires - Conversion
	// ==========================================================================

	/**
	 *	Vérifie si une chaîne représente un entier.
	 *
	 *	@param chaine Chaîne à vérifier
	 *	@return true si c'est un entier, false sinon
	 */
	public boolean is_integer(String chaine) {
		try {
			Integer.parseInt(chaine);
			return true;
		} catch (Exception erreur) {
			return false;
		}
	}

	/**
	 *	Génère un entier aléatoire entre min et max inclus.
	 *
	 *	@param min Valeur minimale
	 *	@param max Valeur maximale
	 *	@return Entier aléatoire
	 */
	public int randInt(int min, int max) {
		int resultat = (int) (Math.random() * max) + min;
		if (resultat > max) {
			resultat = max;
		}
		if (resultat < min) {
			resultat = min;
		}
		return resultat;
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
