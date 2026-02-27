// ==============================================================================
// Classe Case
// ==============================================================================

/**
 *	Représente une case du plateau de jeu.
 *	Contient les coordonnées, l'état et les références au joueur/bateau.
 */
class Case {

	// ==========================================================================
	// Données
	// ==========================================================================

	public int ordonnee;
	public int abscisse;
	public Joueur joueur;
	public Bateau bateau;
	public int numero_du_bateau;
	public boolean attaquee;
	public boolean coule;
	/** Etat pour tableau d'attaque : 0=eau, 1=touché, 2=coulé, -1=rien */
	public int etat;

	// ==========================================================================
	// Constructeurs
	// ==========================================================================

	/**
	 *	Constructeur par défaut.
	 */
	public Case() {
	}

	/**
	 *	Constructeur avec coordonnées.
	 *
	 *	@param ordonneeCase Coordonnée Y
	 *	@param abscisseCase Coordonnée X
	 */
	public Case(int ordonneeCase, int abscisseCase) {
		constructeur(ordonneeCase, abscisseCase);
		this.etat = -1;
	}

	/**
	 *	Constructeur complet avec joueur et bateau.
	 */
	public Case(Joueur joueurProprio, Bateau bateauCase, int numeroBateau, int ordonneeCase, int abscisseCase) {
		this.joueur = joueurProprio;
		this.bateau = bateauCase;
		this.numero_du_bateau = numeroBateau;
		constructeur(ordonneeCase, abscisseCase);
		this.etat = -1;
	}

	/**
	 *	Constructeur avec coordonnées et état.
	 */
	public Case(int ordonneeCase, int abscisseCase, int etatCase) {
		constructeur(ordonneeCase, abscisseCase);
		this.etat = etatCase;
	}

	/**
	 *	Initialise les attributs communs.
	 */
	public void constructeur(int ordonneeCase, int abscisseCase) {
		this.ordonnee = ordonneeCase;
		this.abscisse = abscisseCase;
		this.attaquee = false;
		this.coule = false;
	}

	// ==========================================================================
	// Fonctions principales - Etat de la case
	// ==========================================================================

	/**
	 *	Vérifie si la case a été attaquée.
	 *
	 *	@return true si attaquée
	 */
	public boolean est_attaquee() {
		return attaquee;
	}

	/**
	 *	Vérifie si la case est coulée.
	 *
	 *	@return true si coulée
	 */
	public boolean est_coulee() {
		return coule;
	}

	/**
	 *	Marque la case comme attaquée.
	 */
	public void attaquer() {
		attaquee = true;
	}

	/**
	 *	Marque la case comme coulée.
	 */
	public void couler() {
		coule = true;
	}

	// ==========================================================================
	// Fonctions principales - Validation de format
	// ==========================================================================

	/**
	 *	Vérifie le format d'une case (ex: A5).
	 *
	 *	@param chaine Chaîne à vérifier
	 *	@param hauteur Hauteur de la grille
	 *	@return true si format valide
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
	 *	Vérifie qu'une case existe dans la grille.
	 *
	 *	@param grille Grille de jeu
	 *	@param caseStr Case au format texte
	 *	@return true si la case existe
	 */
	public boolean verifie_case_existe(Grille grille, String caseStr) {
		if (caseStr == null || caseStr.isEmpty() || caseStr.length() != 2) {
			return false;
		}
		if (!verifie_format_case(caseStr, grille.hauteur)) {
			return false;
		}
		int abscisseCase = (int) (caseStr.charAt(1) - '1' + 1);
		return (abscisseCase >= 1 && abscisseCase <= grille.largeur);
	}

	// --------------------------------------------------------------------------
	// Conversion de coordonnées
	// --------------------------------------------------------------------------

	/**
	 *	Convertit une case au format "A5" en coordonnées.
	 *
	 *	@param grille Grille de jeu
	 *	@param chaine Case au format texte
	 *	@return Tableau [ordonnée, abscisse] ou null si invalide
	 */
	public int[] convertit_case_en_coordonnee(Grille grille, String chaine) {
		if (!verifie_case_existe(grille, chaine)) {
			aff("convertit_case_en_coordonnee : Format incorrect");
			return null;
		}
		int[] resultat = new int[2];
		resultat[0] = (int) (chaine.charAt(0) - 'A' + 1);
		resultat[1] = (int) (chaine.charAt(1) - '0');
		return resultat;
	}

	// ==========================================================================
	// Fonctions principales - Comparaison et recherche
	// ==========================================================================

	/**
	 *	Compare deux cases par leurs coordonnées.
	 *
	 *	@param case1 Première case
	 *	@param case2 Deuxième case
	 *	@return true si mêmes coordonnées
	 */
	public boolean compare_case(Case case1, Case case2) {
		return (case1.ordonnee == case2.ordonnee && case1.abscisse == case2.abscisse);
	}

	/**
	 *	Vérifie si une case est présente dans un tableau.
	 *
	 *	@param tableauCases Tableau de cases
	 *	@param caseRecherchee Case à rechercher
	 *	@return true si trouvée
	 */
	public boolean contient_case(Case[] tableauCases, Case caseRecherchee) {
		for (int index = 0; index < tableauCases.length; index++) {
			if (compare_case(tableauCases[index], caseRecherchee)) {
				return true;
			}
		}
		return false;
	}

	/**
	 *	Vérifie si une case de tab2 est dans tab1.
	 *
	 *	@param tab1 Premier tableau
	 *	@param tab2 Deuxième tableau
	 *	@return true si intersection non vide
	 */
	public boolean contient_case(Case[] tab1, Case[] tab2) {
		for (int index1 = 0; index1 < tab1.length; index1++) {
			for (int index2 = 0; index2 < tab2.length; index2++) {
				if (compare_case(tab1[index1], tab2[index2])) {
					return true;
				}
			}
		}
		return false;
	}

	// ==========================================================================
	// Fonctions utilitaires - Conversion et affichage
	// ==========================================================================

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

	/**
	 *	Retourne les coordonnées sous forme de chaîne.
	 */
	public String aff_case_str() {
		return "Ordonnee = " + ordonnee + "\nAbscisse = " + abscisse + "\n";
	}

	/**
	 *	Retourne la case au format écrit (ex: A5).
	 */
	public String case_format_ecrit() {
		return (new Utilitaire()).convertit_coordonnees_str(ordonnee, abscisse);
	}

	/**
	 *	Représentation textuelle de la case.
	 */
	public String toString() {
		if (joueur == null) {
			return attaquee ? "X  " : "0  ";
		}
		String resultat = "J" + joueur.toString() + "B" + this.numero_du_bateau;
		if (attaquee && !coule) {
			resultat = "T" + resultat;
		}
		if (attaquee && coule) {
			resultat = "C" + resultat;
		}
		return resultat;
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
