// ==============================================================================
// Classe Utilitaire
// ==============================================================================

/**
	Classe contenant des fonctions utilitaires pour le jeu de bataille navale.
	Gere les verifications de placement, la construction de plateaux et les conversions.
*/
class Utilitaire {

	/**
		Constructeur par defaut.
	*/
	public Utilitaire() {}

// ==============================================================================
// Fonctions de verification
// ==============================================================================

	/**
		Compare deux coordonnees pour verifier si elles sont identiques.

		@param ordonnee_source Ordonnee de la source
		@param abscisse_source Abscisse de la source
		@param ordonnee_but Ordonnee du but
		@param abscisse_but Abscisse du but
		@return true si les coordonnees sont identiques
	*/
	public boolean compare_coordonneee(int ordonnee_source, int abscisse_source, int ordonnee_but, int abscisse_but) {
		return (ordonnee_source == ordonnee_but && abscisse_source == abscisse_but);
	}

	/**
		Verifie si le placement du bateau est correct.
		Un bateau peut etre horizontal ou vertical.

		@param case_debut Coordonnees de la premiere case [ordonnee, abscisse]
		@param case_fin Coordonnees de la derniere case [ordonnee, abscisse]
		@param nombre_de_cases Nombre de cases du bateau
		@return true si le placement est valide
	*/
	public boolean verifie_placement_bateau(int[] case_debut, int[] case_fin, int nombre_de_cases) {
		if (estCaseUnique(case_debut, case_fin)) {
			return (nombre_de_cases == 1);
		}
		if (estPlacementHorizontal(case_debut, case_fin)) {
			return verifieDistanceHorizontale(case_debut, case_fin, nombre_de_cases);
		}
		if (estPlacementVertical(case_debut, case_fin)) {
			return verifieDistanceVerticale(case_debut, case_fin, nombre_de_cases);
		}
		return false;
	}

	/**
		Verifie si les deux cases sont identiques.

		@param case_debut Case de debut
		@param case_fin Case de fin
		@return true si les cases sont identiques
	*/
	private boolean estCaseUnique(int[] case_debut, int[] case_fin) {
		return (case_debut[0] == case_fin[0] && case_debut[1] == case_fin[1]);
	}

	/**
		Verifie si le placement est horizontal.

		@param case_debut Case de debut
		@param case_fin Case de fin
		@return true si le placement est horizontal
	*/
	private boolean estPlacementHorizontal(int[] case_debut, int[] case_fin) {
		return (case_debut[0] == case_fin[0]);
	}

	/**
		Verifie si le placement est vertical.

		@param case_debut Case de debut
		@param case_fin Case de fin
		@return true si le placement est vertical
	*/
	private boolean estPlacementVertical(int[] case_debut, int[] case_fin) {
		return (case_debut[1] == case_fin[1]);
	}

	/**
		Verifie la distance horizontale entre deux cases.

		@param case_debut Case de debut
		@param case_fin Case de fin
		@param nombre_de_cases Nombre de cases attendu
		@return true si la distance correspond au nombre de cases
	*/
	private boolean verifieDistanceHorizontale(int[] case_debut, int[] case_fin, int nombre_de_cases) {
		int distance = Math.abs(case_fin[1] - case_debut[1]);
		return (distance == nombre_de_cases && distance > 0);
	}

	/**
		Verifie la distance verticale entre deux cases.

		@param case_debut Case de debut
		@param case_fin Case de fin
		@param nombre_de_cases Nombre de cases attendu
		@return true si la distance correspond au nombre de cases
	*/
	private boolean verifieDistanceVerticale(int[] case_debut, int[] case_fin, int nombre_de_cases) {
		int distance = Math.abs(case_fin[0] - case_debut[0]);
		return (distance == nombre_de_cases && distance > 0);
	}

	/**
		Verifie si une case a deja ete tentee.

		@param cases_tentees Tableau des cases tentees
		@param indice_cases_tentees Nombre de cases tentees
		@param case_tentee Case a verifier
		@return false si la case a deja ete tentee, true sinon
	*/
	public boolean verifie_case_tentee(Case[] cases_tentees, int indice_cases_tentees, Case case_tentee) {
		Case comparateur = new Case();
		for (int index = 0; index < indice_cases_tentees; index++) {
			if (comparateur.compare_case(cases_tentees[index], case_tentee)) {
				return false;
			}
		}
		return true;
	}

// ==============================================================================
// Fonctions de generation de cases
// ==============================================================================

	/**
		Genere l ensemble des cases occupees par un bateau.

		@param coordonnees Tableau [ordonnee_debut, abscisse_debut, ordonnee_fin, abscisse_fin]
		@param nombre_de_cases Nombre de cases du bateau
		@return Tableau des cases du bateau
	*/
	public Case[] donne_cases_bateau(int[] coordonnees, int nombre_de_cases) {
		int[] case_debut = {coordonnees[0], coordonnees[1]};
		int[] case_fin = {coordonnees[2], coordonnees[3]};
		Case[] cases_bateau = new Case[nombre_de_cases];

		if (estCaseUnique(case_debut, case_fin)) {
			cases_bateau[0] = new Case(coordonnees[0], coordonnees[1]);
			return cases_bateau;
		}
		if (estPlacementHorizontal(case_debut, case_fin)) {
			return genere_cases_horizontales(case_debut, case_fin, nombre_de_cases);
		}
		if (estPlacementVertical(case_debut, case_fin)) {
			return genere_cases_verticales(case_debut, case_fin, nombre_de_cases);
		}
		return cases_bateau;
	}

	/**
		Genere les cases pour un placement horizontal.

		@param case_debut Case de debut
		@param case_fin Case de fin
		@param nombre_de_cases Nombre de cases
		@return Tableau des cases
	*/
	private Case[] genere_cases_horizontales(int[] case_debut, int[] case_fin, int nombre_de_cases) {
		Case[] cases = new Case[nombre_de_cases];
		int direction = (case_fin[1] < case_debut[1]) ? -1 : 1;
		for (int index = 0; index < nombre_de_cases; index++) {
			cases[index] = new Case(case_debut[0], case_debut[1] + (index * direction));
		}
		return cases;
	}

	/**
		Genere les cases pour un placement vertical.

		@param case_debut Case de debut
		@param case_fin Case de fin
		@param nombre_de_cases Nombre de cases
		@return Tableau des cases
	*/
	private Case[] genere_cases_verticales(int[] case_debut, int[] case_fin, int nombre_de_cases) {
		Case[] cases = new Case[nombre_de_cases];
		int direction = (case_fin[0] < case_debut[0]) ? -1 : 1;
		for (int index = 0; index < nombre_de_cases; index++) {
			cases[index] = new Case(case_debut[0] + (index * direction), case_debut[1]);
		}
		return cases;
	}

// ==============================================================================
// Fonctions de gestion des cases occupees
// ==============================================================================

	/**
		Construit un tableau de toutes les cases occupees par les bateaux du joueur.

		@param joueur Joueur dont on veut les cases occupees
		@return Tableau des cases occupees
	*/
	public Case[] cases_occuppees(Joueur joueur) {
		Bateau[] bateaux = joueur.bateaux;
		int nombre_total_cases = compteNombreTotalCases(bateaux);
		Case[] cases_occupees = new Case[nombre_total_cases];
		remplitCasesOccupees(cases_occupees, bateaux, joueur);
		return cases_occupees;
	}

	/**
		Compte le nombre total de cases occupees par les bateaux.

		@param bateaux Tableau des bateaux
		@return Nombre total de cases
	*/
	private int compteNombreTotalCases(Bateau[] bateaux) {
		int total = 0;
		for (int index_bateau = 0; index_bateau < bateaux.length; index_bateau++) {
			if (bateaux[index_bateau] != null) {
				total += bateaux[index_bateau].cases.length;
			}
		}
		return total;
	}

	/**
		Remplit le tableau des cases occupees.

		@param cases_occupees Tableau a remplir
		@param bateaux Tableau des bateaux
		@param joueur Joueur proprietaire
	*/
	private void remplitCasesOccupees(Case[] cases_occupees, Bateau[] bateaux, Joueur joueur) {
		int index_case = 0;
		for (int index_bateau = 0; index_bateau < bateaux.length; index_bateau++) {
			if (bateaux[index_bateau] != null) {
				index_case = ajouteCasesBateau(cases_occupees, bateaux[index_bateau], joueur, index_bateau, index_case);
			}
		}
	}

	/**
		Ajoute les cases d un bateau au tableau des cases occupees.

		@param cases_occupees Tableau des cases occupees
		@param bateau Bateau a ajouter
		@param joueur Joueur proprietaire
		@param numero_bateau Numero du bateau
		@param index_depart Index de depart dans le tableau
		@return Nouvel index apres ajout
	*/
	private int ajouteCasesBateau(Case[] cases_occupees, Bateau bateau, Joueur joueur, int numero_bateau, int index_depart) {
		int index_courant = index_depart;
		for (int index_case_bateau = 0; index_case_bateau < bateau.cases.length; index_case_bateau++) {
			int ordonnee = bateau.cases[index_case_bateau].ordonnee;
			int abscisse = bateau.cases[index_case_bateau].abscisse;
			cases_occupees[index_courant] = new Case(joueur, bateau, (numero_bateau + 1), ordonnee, abscisse);
			index_courant++;
		}
		return index_courant;
	}

	/**
		Recherche une case dans un tableau de cases.

		@param cases Tableau de cases
		@param case_recherchee Case a rechercher
		@return La case trouvee ou null
	*/
	public Case cherche_case(Case[] cases, Case case_recherchee) {
		Case comparateur = new Case();
		for (int index = 0; index < cases.length; index++) {
			if (comparateur.compare_case(cases[index], case_recherchee)) {
				return cases[index];
			}
		}
		return null;
	}

// ==============================================================================
// Fonctions de construction de plateau
// ==============================================================================

	/**
		Construit le plateau de jeu pour un joueur.

		@param joueur Joueur dont on construit le plateau
		@return Plateau sous forme de tableau 2D de cases
	*/
	public Case[][] construit_plateau(Joueur joueur) {
		Case[][] plateau = new Case[joueur.grille.hauteur][joueur.grille.largeur];
		for (int ligne = 0; ligne < plateau.length; ligne++) {
			for (int colonne = 0; colonne < plateau[ligne].length; colonne++) {
				plateau[ligne][colonne] = construitCasePlateau(joueur, ligne, colonne);
			}
		}
		return plateau;
	}

	/**
		Construit une case du plateau.

		@param joueur Joueur proprietaire
		@param ligne Ligne de la case
		@param colonne Colonne de la case
		@return Case construite (occupee ou vide)
	*/
	private Case construitCasePlateau(Joueur joueur, int ligne, int colonne) {
		Case case_position = new Case((ligne + 1), (colonne + 1));
		Case case_occupee = cherche_case(joueur.cases_occupees, case_position);
		if (case_occupee != null) {
			return case_occupee;
		}
		return case_position;
	}

// ==============================================================================
// Fonctions utilitaires de jeu
// ==============================================================================

	/**
		Fonction temporaire (placeholder).

		@return true
	*/
	public boolean tmp() {
		return true;
	}

	/**
		Recherche le joueur gagnant (premier non perdant).

		@param joueurs Tableau des joueurs
		@return Index du gagnant ou -1 si aucun
	*/
	public int recherche_gagnant(Joueur[] joueurs) {
		for (int index = 0; index < joueurs.length; index++) {
			if (!joueurs[index].a_perdu()) {
				return index;
			}
		}
		return -1;
	}

	/**
		Convertit des coordonnees en format lisible (ex: A1, B3).

		@param ordonnee Ordonnee (1-based)
		@param abscisse Abscisse (1-based)
		@return Chaine formatee
	*/
	public String convertit_coordonnees_str(int ordonnee, int abscisse) {
		return "" + (char)('A' + ordonnee - 1) + abscisse;
	}

	/**
		Affiche le resultat d un coup.

		@param code_evenement Code : 0=eau, 1=touche, 2=coule
	*/
	public void afficher_evenement_coup(int code_evenement) {
		String message = "";
		if (code_evenement == 0) message = "Dans l'eau !";
		if (code_evenement == 1) message = "Touche !";
		if (code_evenement == 2) message = "Coule !";
		aff("\n\t\t" + message + "\n");
	}

// ==============================================================================
// Fonctions utilitaires generiques
// ==============================================================================

	/**
		Affiche un tableau de cases (version complete).

		@param nom Nom du tableau
		@param cases Tableau de cases
	*/
	public void aff_tab(String nom, Case[] cases) {
		aff_tab(nom, cases, cases.length);
	}

	/**
		Affiche un tableau de cases (version limitee).

		@param nom Nom du tableau
		@param cases Tableau de cases
		@param limite_indice Nombre de cases a afficher
	*/
	public void aff_tab(String nom, Case[] cases, int limite_indice) {
		int limite = Math.min(limite_indice, cases.length);
		for (int index = 0; index < limite; index++) {
			if (cases[index] == null) {
				aff(nom + "[" + index + "] = null");
			} else {
				aff(nom + "[" + index + "] = \n" + cases[index].aff_case_str());
			}
		}
	}

	/**
		Affiche un tableau d entiers.

		@param nom Nom du tableau
		@param tableau Tableau d entiers
	*/
	public void aff_tab(String nom, int[] tableau) {
		for (int index = 0; index < tableau.length; index++) {
			aff(nom + "[" + index + "] = " + tableau[index]);
		}
	}

	/**
		Verifie si une chaine represente un entier.

		@param chaine Chaine a verifier
		@return true si c est un entier
	*/
	public boolean is_integer(String chaine) {
		try {
			Integer.parseInt(chaine);
			return true;
		} catch (Exception erreur_conversion) {
			return false;
		}
	}

	/**
		Genere un entier aleatoire dans un intervalle.

		@param minimum Valeur minimum
		@param maximum Valeur maximum
		@return Entier aleatoire
	*/
	public int randInt(int minimum, int maximum) {
		int resultat = (int)(Math.random() * maximum) + minimum;
		if (resultat > maximum) resultat = maximum;
		if (resultat < minimum) resultat = minimum;
		return resultat;
	}

	/**
		Formate un entier pour qu il reste dans un intervalle.

		@param minimum Valeur minimum
		@param maximum Valeur maximum
		@param valeur Valeur a formater
		@return Valeur formatee
	*/
	public int formate_int(int minimum, int maximum, int valeur) {
		int resultat = valeur;
		if (resultat < minimum) resultat = minimum;
		if (resultat > maximum) resultat = maximum;
		return resultat;
	}

	/**
		Affiche une chaine avec saut de ligne.

		@param message Message a afficher
	*/
	public void aff(String message) {
		System.out.println(message);
	}

	/**
		Affiche une chaine sans saut de ligne.

		@param message Message a afficher
	*/
	public void affnn(String message) {
		System.out.print(message);
	}
}
