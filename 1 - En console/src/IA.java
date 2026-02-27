import java.util.Arrays;

/**
 * Classe gerant l'intelligence artificielle pour le placement et le jeu.
 */
class IA {
	public int [][] plateau;
	Utilitaire utilitaire;
	Grille grille;

	/**
	 * Constructeur par defaut.
	 */
	public IA () {
		constructeur();
	}

	/**
	 * Constructeur avec grille.
	 * @param grille La grille de jeu.
	 */
	public IA (Grille grille) {
		this.grille = grille;
		constructeur();
	}

	/**
	 * Initialise les composants de l'IA.
	 */
	public void constructeur() {
		utilitaire = new Utilitaire();
	}

	/**
	 * Calcule le nombre de bateaux en fonction de la grille.
	 * @param grille La grille de jeu.
	 * @param nombreCasesMax Nombre maximum de cases pour les bateaux.
	 * @return Le nombre de bateaux a placer.
	 */
	private int calculerNombreBateaux(Grille grille, int nombreCasesMax) {
		int minHauteurLargeur = Math.min(grille.hauteur, grille.largeur);
		int nombreGrosBateaux = 0;
		int nombreCasesRestantes = nombreCasesMax;

		while (nombreCasesRestantes > 0) {
			nombreCasesRestantes -= minHauteurLargeur;
			nombreGrosBateaux++;
		}

		int nombreBateaux = nombreGrosBateaux;
		if ((-nombreCasesRestantes) > 0) {
			nombreBateaux++;
		}
		if (nombreBateaux < 1) {
			nombreBateaux = 1;
		}
		return nombreBateaux;
	}

	/**
	 * Calcule les tailles de chaque bateau.
	 * @param nombreBateaux Nombre de bateaux a creer.
	 * @param nombreCasesMax Nombre maximum de cases.
	 * @param minHauteurLargeur Minimum entre hauteur et largeur.
	 * @return Tableau des tailles de bateaux.
	 */
	private int[] calculerTaillesBateaux(int nombreBateaux, int nombreCasesMax, int minHauteurLargeur) {
		int[] taillesBateaux = new int[nombreBateaux];
		int nombreCasesRestantes = nombreCasesMax;
		int nombreGrosBateaux = nombreBateaux;

		for (int indexBateau = 0; indexBateau < nombreBateaux; indexBateau++) {
			if (nombreGrosBateaux > 0) {
				int tailleMax = Math.min(nombreCasesRestantes, minHauteurLargeur);
				int tailleMin = (int)((4.0/10) * tailleMax);
				int tailleBateau = randInt(tailleMin, tailleMax);
				if (tailleBateau < 1) {
					tailleBateau = 1;
				}
				taillesBateaux[indexBateau] = tailleBateau;
				nombreCasesRestantes -= tailleBateau;
				nombreGrosBateaux--;
			} else {
				taillesBateaux[indexBateau] = Math.min(nombreCasesRestantes, minHauteurLargeur);
			}
			if (taillesBateaux[indexBateau] < 1) {
				taillesBateaux[indexBateau] = 1;
			}
		}
		return taillesBateaux;
	}

	/**
	 * Essaie de placer un bateau avec plusieurs tentatives.
	 * @param grille La grille de jeu.
	 * @param tailleBateau Taille du bateau a placer.
	 * @param minHauteurLargeur Minimum entre hauteur et largeur.
	 * @param numeroBateau Numero du bateau (pour logs).
	 * @return Le bateau place ou null si echec.
	 */
	private Bateau essayerPlacerBateau(Grille grille, int tailleBateau, int minHauteurLargeur, int numeroBateau) {
		Bateau bateauPlace = null;
		int tentativeNiveau2 = 0;
		int tailleActuelle = tailleBateau;

		while (bateauPlace == null && tentativeNiveau2 < 5) {
			int tentativeNiveau1 = 0;
			while (bateauPlace == null && tentativeNiveau1 < 5) {
				bateauPlace = place_un_bateau(grille, tailleActuelle);
				tentativeNiveau1++;
			}
			if (bateauPlace == null) {
				aff("Bateau numero " + numeroBateau + ", " + tentativeNiveau1 + " tentative(s) avec " + tailleActuelle + " case(s).");
				int tailleMax = Math.min(tailleActuelle, minHauteurLargeur);
				tailleActuelle = randInt(1, tailleMax);
				aff("Nouvel essai avec " + tailleActuelle + " case(s).");
			}
			tentativeNiveau2++;
		}

		if (bateauPlace == null) {
			bateauPlace = dernierEssaiPlacement(grille, numeroBateau);
		}
		return bateauPlace;
	}

	/**
	 * Dernier essai de placement avec une seule case.
	 * @param grille La grille de jeu.
	 * @param numeroBateau Numero du bateau.
	 * @return Le bateau place ou null.
	 */
	private Bateau dernierEssaiPlacement(Grille grille, int numeroBateau) {
		aff("Bateau numero " + numeroBateau + " dernier essai avec une seule case.");
		Bateau bateauPlace = null;
		int tentative = 0;

		while (bateauPlace == null && tentative < 5) {
			bateauPlace = place_un_bateau(grille, 1);
			tentative++;
		}

		if (bateauPlace == null) {
			aff("Bateau numero " + numeroBateau + " : echec avec une seule case.");
		}
		return bateauPlace;
	}

	/**
	 * Place tous les bateaux de l'IA sur la grille.
	 * @param grille La grille de jeu.
	 * @param numeroJoueur Numero du joueur.
	 * @param estIA True si joueur IA.
	 * @param carteAMontrer True si carte visible.
	 * @return Le joueur avec ses bateaux places.
	 */
	public Joueur place_bateaux_IA(Grille grille, int numeroJoueur, boolean estIA, boolean carteAMontrer) {
		Bateau bateauTemporaire = new Bateau();
		int nombreCasesMax = bateauTemporaire.nombre_de_place_de_bateau_max(grille);
		int minHauteurLargeur = Math.min(grille.hauteur, grille.largeur);

		int nombreBateaux = calculerNombreBateaux(grille, nombreCasesMax);
		int[] taillesBateaux = calculerTaillesBateaux(nombreBateaux, nombreCasesMax, minHauteurLargeur);

		this.plateau = cree_plateau_tmp(grille);
		Bateau[] bateaux = new Bateau[nombreBateaux];

		for (int indexBateau = 0; indexBateau < nombreBateaux; indexBateau++) {
			bateaux[indexBateau] = essayerPlacerBateau(grille, taillesBateaux[indexBateau], minHauteurLargeur, indexBateau + 1);
		}

		bateaux = adapte_le_nombre_de_bateaux(bateaux);
		return new Joueur(numeroJoueur, grille, bateaux, estIA, carteAMontrer);
	}

	/**
	 * Filtre les bateaux null du tableau.
	 * @param bateaux Tableau de bateaux.
	 * @return Nouveau tableau sans les bateaux null.
	 */
	public Bateau[] adapte_le_nombre_de_bateaux(Bateau[] bateaux) {
		int nombreBateauxValides = 0;
		for (int indexBateau = 0; indexBateau < bateaux.length; indexBateau++) {
			if (bateaux[indexBateau] != null) {
				nombreBateauxValides++;
			}
		}

		Bateau[] bateauxResultat = new Bateau[nombreBateauxValides];
		int indexResultat = 0;
		for (int indexBateau = 0; indexBateau < bateaux.length; indexBateau++) {
			if (bateaux[indexBateau] != null) {
				bateauxResultat[indexResultat++] = bateaux[indexBateau];
			}
		}
		return bateauxResultat;
	}

	/**
	 * Determine aleatoirement l'orientation du bateau.
	 * @return True pour vertical, false pour horizontal.
	 */
	public boolean vertical_horizontal() {
		int valeurAleatoire = randInt(0, 100);
		return (valeurAleatoire < 50);
	}

	/**
	 * Place un bateau avec orientation aleatoire.
	 * @param grille La grille de jeu.
	 * @param tailleBateau Taille du bateau.
	 * @return Le bateau place ou null.
	 */
	public Bateau place_un_bateau(Grille grille, int tailleBateau) {
		boolean estVertical = vertical_horizontal();
		return place_un_bateau_aux(grille, tailleBateau, estVertical);
	}

	/**
	 * Place un bateau avec orientation specifiee.
	 * @param grille La grille de jeu.
	 * @param tailleBateau Taille du bateau.
	 * @param estVertical True pour vertical, false pour horizontal.
	 * @return Le bateau place ou null.
	 */
	public Bateau place_un_bateau_aux(Grille grille, int tailleBateau, boolean estVertical) {
		Case[] casesDisponibles = cases_disponibles(estVertical, tailleBateau);
		if (casesDisponibles == null) {
			estVertical = !estVertical;
			casesDisponibles = cases_disponibles(estVertical, tailleBateau);
		}
		if (casesDisponibles == null) {
			return null;
		}

		int indexAleatoire = randInt(0, casesDisponibles.length - 1);
		Case caseDepart = casesDisponibles[indexAleatoire];
		int[] coordonnees = {caseDepart.ordonnee, caseDepart.abscisse};
		ajoute_un_bateau(coordonnees, tailleBateau, estVertical);

		caseDepart.abscisse += 1;
		caseDepart.ordonnee += 1;

		Case[] casesBateau = construireCasesBateau(caseDepart, tailleBateau, estVertical);
		return new Bateau(casesBateau);
	}

	/**
	 * Construit le tableau des cases d'un bateau.
	 * @param caseDepart Case de depart.
	 * @param tailleBateau Taille du bateau.
	 * @param estVertical Orientation.
	 * @return Tableau des cases du bateau.
	 */
	private Case[] construireCasesBateau(Case caseDepart, int tailleBateau, boolean estVertical) {
		Case[] casesBateau = new Case[tailleBateau];
		int indexCase = 0;

		if (estVertical) {
			for (int ordonnee = caseDepart.ordonnee; ordonnee < caseDepart.ordonnee + tailleBateau; ordonnee++) {
				casesBateau[indexCase++] = new Case(ordonnee, caseDepart.abscisse);
			}
		} else {
			for (int abscisse = caseDepart.abscisse; abscisse < caseDepart.abscisse + tailleBateau; abscisse++) {
				casesBateau[indexCase++] = new Case(caseDepart.ordonnee, abscisse);
			}
		}
		return casesBateau;
	}

	/**
	 * Cree un plateau temporaire vide.
	 * @param grille La grille de jeu.
	 * @return Plateau 2D initialise a 0.
	 */
	public int[][] cree_plateau_tmp(Grille grille) {
		int hauteur = grille.hauteur;
		int largeur = grille.largeur;
		int[][] tableau = new int[hauteur][largeur];

		for (int ligne = 0; ligne < hauteur; ligne++) {
			for (int colonne = 0; colonne < largeur; colonne++) {
				tableau[ligne][colonne] = 0;
			}
		}
		return tableau;
	}

	/**
	 * Ajoute un bateau au plateau avec distance de securite.
	 * @param coordonnees Coordonnees de depart [ordonnee, abscisse].
	 * @param tailleBateau Taille du bateau.
	 * @param estVertical Orientation.
	 */
	public void ajoute_un_bateau(int[] coordonnees, int tailleBateau, boolean estVertical) {
		ajoute_un_bateau_aux(coordonnees, tailleBateau, estVertical, true);
	}

	/**
	 * Ajoute un bateau au plateau.
	 * @param coordonnees Coordonnees de depart [ordonnee, abscisse].
	 * @param tailleBateau Taille du bateau.
	 * @param estVertical Orientation.
	 * @param avecDistanceSecurite Ajouter distance autour.
	 */
	public void ajoute_un_bateau_aux(int[] coordonnees, int tailleBateau, boolean estVertical, boolean avecDistanceSecurite) {
		int ordonnee = coordonnees[0];
		int abscisse = coordonnees[1];

		if (estVertical) {
			marquerCasesVerticales(ordonnee, abscisse, tailleBateau, avecDistanceSecurite);
		} else {
			marquerCasesHorizontales(ordonnee, abscisse, tailleBateau, avecDistanceSecurite);
		}
	}

	/**
	 * Marque les cases pour un bateau vertical.
	 */
	private void marquerCasesVerticales(int ordonnee, int abscisse, int tailleBateau, boolean avecDistanceSecurite) {
		for (int ligne = ordonnee; ligne < ordonnee + tailleBateau; ligne++) {
			this.plateau[ligne][abscisse] = 1;
		}
		if (avecDistanceSecurite) {
			marquerDistanceSecuriteVerticale(ordonnee, abscisse, tailleBateau);
		}
	}

	/**
	 * Marque les cases pour un bateau horizontal.
	 */
	private void marquerCasesHorizontales(int ordonnee, int abscisse, int tailleBateau, boolean avecDistanceSecurite) {
		for (int colonne = abscisse; colonne < abscisse + tailleBateau; colonne++) {
			this.plateau[ordonnee][colonne] = 1;
		}
		if (avecDistanceSecurite) {
			marquerDistanceSecuriteHorizontale(ordonnee, abscisse, tailleBateau);
		}
	}

	/**
	 * Marque la distance de securite pour un bateau vertical.
	 */
	private void marquerDistanceSecuriteVerticale(int ordonnee, int abscisse, int tailleBateau) {
		if (ordonnee - 1 >= 0) {
			this.plateau[ordonnee - 1][abscisse] = 1;
		}
		if (ordonnee + tailleBateau < this.plateau.length) {
			this.plateau[ordonnee + tailleBateau][abscisse] = 1;
		}
		if (abscisse - 1 >= 0) {
			for (int ligne = ordonnee; ligne < ordonnee + tailleBateau; ligne++) {
				this.plateau[ligne][abscisse - 1] = 1;
			}
		}
		if (abscisse + 1 < this.plateau[0].length) {
			for (int ligne = ordonnee; ligne < ordonnee + tailleBateau; ligne++) {
				this.plateau[ligne][abscisse + 1] = 1;
			}
		}
	}

	/**
	 * Marque la distance de securite pour un bateau horizontal.
	 */
	private void marquerDistanceSecuriteHorizontale(int ordonnee, int abscisse, int tailleBateau) {
		if (abscisse - 1 >= 0) {
			this.plateau[ordonnee][abscisse - 1] = 1;
		}
		if (abscisse + tailleBateau < this.plateau[0].length) {
			this.plateau[ordonnee][abscisse + tailleBateau] = 1;
		}
		if (ordonnee - 1 >= 0) {
			for (int colonne = abscisse; colonne < abscisse + tailleBateau; colonne++) {
				this.plateau[ordonnee - 1][colonne] = 1;
			}
		}
		if (ordonnee + 1 < this.plateau.length) {
			for (int colonne = abscisse; colonne < abscisse + tailleBateau; colonne++) {
				this.plateau[ordonnee + 1][colonne] = 1;
			}
		}
	}

	/**
	 * Retourne les cases disponibles pour placer un bateau.
	 * @param estVertical Orientation.
	 * @param tailleBateau Taille du bateau.
	 * @return Tableau des cases disponibles ou null.
	 */
	public Case[] cases_disponibles(boolean estVertical, int tailleBateau) {
		int[][] casesParLigneColonne = cases_disponibles_aux_1(estVertical, tailleBateau);

		int nombreLignesNonNull = 0;
		int largeur = 0;
		for (int index = 0; index < casesParLigneColonne.length; index++) {
			if (casesParLigneColonne[index] != null) {
				nombreLignesNonNull++;
				largeur = casesParLigneColonne[index].length;
			}
		}

		if (nombreLignesNonNull == 0) {
			return null;
		}

		int nombreCases = compterCasesDisponibles(casesParLigneColonne, largeur);
		return construireCasesDisponibles(casesParLigneColonne, largeur, nombreCases, estVertical);
	}

	/**
	 * Compte le nombre de cases disponibles.
	 */
	private int compterCasesDisponibles(int[][] casesParLigneColonne, int largeur) {
		int nombreCases = 0;
		for (int ligne = 0; ligne < casesParLigneColonne.length; ligne++) {
			for (int colonne = 0; colonne < largeur; colonne++) {
				if (casesParLigneColonne[ligne] != null) {
					nombreCases++;
				}
			}
		}
		return nombreCases;
	}

	/**
	 * Construit le tableau des cases disponibles.
	 */
	private Case[] construireCasesDisponibles(int[][] casesParLigneColonne, int largeur, int nombreCases, boolean estVertical) {
		Case[] casesResultat = new Case[nombreCases];
		int indexResultat = 0;
		int hauteur = casesParLigneColonne.length;

		for (int ligne = 0; ligne < hauteur; ligne++) {
			for (int colonne = 0; colonne < largeur; colonne++) {
				if (casesParLigneColonne[ligne] != null) {
					if (estVertical) {
						casesResultat[indexResultat] = new Case(colonne, ligne);
					} else {
						casesResultat[indexResultat] = new Case(ligne, colonne);
					}
					indexResultat++;
				}
			}
		}
		return casesResultat;
	}

	/**
	 * Analyse le plateau pour trouver les cases disponibles.
	 * @param estVertical Orientation.
	 * @param tailleBateau Taille du bateau.
	 * @return Tableau 2D des cases disponibles.
	 */
	public int[][] cases_disponibles_aux_1(boolean estVertical, int tailleBateau) {
		int hauteur = this.plateau.length;
		int largeur = this.plateau[0].length;
		int[][] casesDisponibles;

		if (estVertical) {
			casesDisponibles = new int[largeur][];
			int[] ligneColonne = new int[hauteur];
			for (int colonne = 0; colonne < largeur; colonne++) {
				for (int ligne = 0; ligne < hauteur; ligne++) {
					ligneColonne[ligne] = this.plateau[ligne][colonne];
				}
				casesDisponibles[colonne] = scanne_ligne_colonne(ligneColonne, tailleBateau);
			}
		} else {
			casesDisponibles = new int[hauteur][];
			int[] ligneColonne = new int[largeur];
			for (int ligne = 0; ligne < hauteur; ligne++) {
				for (int colonne = 0; colonne < largeur; colonne++) {
					ligneColonne[colonne] = this.plateau[ligne][colonne];
				}
				casesDisponibles[ligne] = scanne_ligne_colonne(ligneColonne, tailleBateau);
			}
		}
		return casesDisponibles;
	}

	/**
	 * Scanne une ligne/colonne pour trouver les positions valides.
	 * @param tableau Ligne ou colonne du plateau.
	 * @param tailleBateau Taille du bateau.
	 * @return Positions valides ou null.
	 */
	public int[] scanne_ligne_colonne(int[] tableau, int tailleBateau) {
		int tailleTableau = tableau.length;
		int nombrePositionsPossibles = compterPositionsPossibles(tableau, tailleBateau, tailleTableau);

		if (nombrePositionsPossibles == 0) {
			return null;
		}

		return construirePositionsPossibles(tableau, tailleBateau, tailleTableau, nombrePositionsPossibles);
	}

	/**
	 * Compte les positions possibles dans une ligne/colonne.
	 */
	private int compterPositionsPossibles(int[] tableau, int tailleBateau, int tailleTableau) {
		int nombrePositions = 0;
		for (int position = 0; position < tailleTableau; position++) {
			if (estPositionValide(tableau, position, tailleBateau, tailleTableau)) {
				nombrePositions++;
			}
		}
		return nombrePositions;
	}

	/**
	 * Construit le tableau des positions possibles.
	 */
	private int[] construirePositionsPossibles(int[] tableau, int tailleBateau, int tailleTableau, int nombrePositions) {
		int[] positionsPossibles = new int[nombrePositions];
		int indexResultat = 0;
		for (int position = 0; position < tailleTableau; position++) {
			if (estPositionValide(tableau, position, tailleBateau, tailleTableau)) {
				positionsPossibles[indexResultat++] = position;
			}
		}
		return positionsPossibles;
	}

	/**
	 * Verifie si une position est valide pour placer un bateau.
	 */
	private boolean estPositionValide(int[] tableau, int position, int tailleBateau, int tailleTableau) {
		if (position + tailleBateau > tailleTableau) {
			return false;
		}
		for (int offset = 0; offset < tailleBateau; offset++) {
			if (tableau[position + offset] == 1) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Retourne les cases occupees sur le plateau.
	 * @return Tableau des cases occupees ou null.
	 */
	public Case[] cases_occupees() {
		int tailleResultat = 0;
		for (int ligne = 0; ligne < this.plateau.length; ligne++) {
			for (int colonne = 0; colonne < this.plateau[ligne].length; colonne++) {
				if (this.plateau[ligne][colonne] == 1) {
					tailleResultat++;
				}
			}
		}

		if (tailleResultat == 0) {
			return null;
		}

		Case[] resultat = new Case[tailleResultat];
		int indexResultat = 0;
		for (int ligne = 0; ligne < this.plateau.length; ligne++) {
			for (int colonne = 0; colonne < this.plateau[ligne].length; colonne++) {
				if (this.plateau[ligne][colonne] == 1) {
					resultat[indexResultat++] = new Case(ligne, colonne);
				}
			}
		}
		return resultat;
	}

	/**
	 * Fait jouer un coup par l'IA.
	 * @param joueurAttaquant Joueur qui attaque.
	 * @param joueurAttaque Joueur attaque.
	 * @return Resultat du tir.
	 */
	public boolean joueur_un_coup(Joueur joueurAttaquant, Joueur joueurAttaque) {
		return joueurAttaquant.tenter_une_case_IA(joueurAttaque);
	}

	/**
	 * Verifie si une ligne contient des cases possibles.
	 * @param tableau Ligne a verifier.
	 * @return True si au moins une case possible.
	 */
	public boolean ligne_cases_possibles(int[] tableau) {
		for (int index = 0; index < tableau.length; index++) {
			if (tableau[index] != 0) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Retourne la liste des indices des cases possibles.
	 * @param tableau Ligne a analyser.
	 * @return Indices des cases non nulles.
	 */
	public int[] ligne_cases_possibles_liste(int[] tableau) {
		int tailleResultat = 0;
		for (int index = 0; index < tableau.length; index++) {
			if (tableau[index] != 0) {
				tailleResultat++;
			}
		}

		int[] resultat = new int[tailleResultat];
		int indexResultat = 0;
		for (int index = 0; index < tableau.length; index++) {
			if (tableau[index] != 0) {
				resultat[indexResultat++] = index;
			}
		}
		return resultat;
	}

	/**
	 * Liste toutes les cases non encore tentees.
	 * @param joueurAttaquant Joueur attaquant.
	 * @return Tableau des cases possibles.
	 */
	public Case[] liste_des_cases_possibles(Joueur joueurAttaquant) {
		int[][] plateauTente = joueurAttaquant.genere_plateau_cases_tentees(joueurAttaquant);
		int tailleResultat = 0;

		for (int ligne = 0; ligne < plateauTente.length; ligne++) {
			for (int colonne = 0; colonne < plateauTente[ligne].length; colonne++) {
				if (plateauTente[ligne][colonne] == -1) {
					tailleResultat++;
				}
			}
		}

		Case[] resultat = new Case[tailleResultat];
		int indexResultat = 0;
		for (int ligne = 0; ligne < plateauTente.length; ligne++) {
			for (int colonne = 0; colonne < plateauTente[ligne].length; colonne++) {
				if (plateauTente[ligne][colonne] == -1) {
					resultat[indexResultat++] = new Case(ligne + 1, colonne + 1);
				}
			}
		}
		return resultat;
	}

	/**
	 * Genere une case aleatoire parmi les cases possibles.
	 * @param joueurAttaquant Joueur attaquant.
	 * @return Case choisie aleatoirement.
	 */
	public Case genere_une_case_aleat(Joueur joueurAttaquant) {
		Case[] casesPossibles = liste_des_cases_possibles(joueurAttaquant);
		int indexChoisi = randInt(0, casesPossibles.length - 1);
		return casesPossibles[indexChoisi];
	}

	// ################### Fonctions utilitaires #################### //

	/**
	 * Affiche le plateau de l'IA.
	 */
	public void afficher_plateau() {
		afficher_plateau_aux("du plateau (IA)", this.plateau);
	}

	/**
	 * Affiche un plateau avec un nom.
	 * @param nom Nom du plateau.
	 * @param plateauAAfficher Plateau a afficher.
	 */
	public void afficher_plateau_aux(String nom, int[][] plateauAAfficher) {
		if (plateauAAfficher == null || plateauAAfficher.length < 1) {
			return;
		}
		int hauteur = plateauAAfficher.length;
		int largeur = plateauAAfficher[0].length;

		aff("\nAffichage " + nom + " : \n");
		for (int ligne = 0; ligne < hauteur; ligne++) {
			affnn("  ");
			for (int colonne = 0; colonne < largeur; colonne++) {
				affnn("" + plateauAAfficher[ligne][colonne] + "   ");
			}
			affnn("\n");
		}
		affnn("\n");
	}

	/**
	 * Affiche un tableau d'entiers.
	 * @param tableau Tableau a afficher.
	 * @param nom Nom du tableau.
	 */
	public void aff_tab(int[] tableau, String nom) {
		if (tableau == null) {
			aff("aff_tab : " + nom + " est null");
			return;
		}
		for (int index = 0; index < tableau.length; index++) {
			aff(nom + "[" + index + "] = " + tableau[index]);
		}
	}

	/**
	 * Verifie si une chaine est un entier.
	 * @param chaine Chaine a verifier.
	 * @return True si entier valide.
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
	 * Genere un entier aleatoire entre min et max inclus.
	 * @param min Valeur minimale.
	 * @param max Valeur maximale.
	 * @return Entier aleatoire.
	 */
	public int randInt(int min, int max) {
		if (min == max) {
			return min;
		}
		int resultat = (int)(Math.random() * max) + min;
		if (resultat > max) {
			resultat = max;
		}
		if (resultat < min) {
			resultat = min;
		}
		return resultat;
	}

	/**
	 * Genere un entier aleatoire en excluant certaines valeurs.
	 * @param min Valeur minimale.
	 * @param max Valeur maximale.
	 * @param valeursExclues Valeurs a exclure.
	 * @return Entier aleatoire hors exclusions ou -1 si impossible.
	 */
	public int randInt_sauf(int min, int max, int[] valeursExclues) {
		if (valeursExclues == null || valeursExclues.length == 0) {
			return randInt(min, max);
		}

		Arrays.sort(valeursExclues);
		utilitaire.aff_tab("valeursExclues", valeursExclues);

		int[] valeursIntermediaires = new int[valeursExclues.length + 1];
		valeursIntermediaires[0] = calculerValeurIntermediaire(min, valeursExclues[0]);

		if (valeursExclues.length > 1) {
			for (int index = 0; index < valeursExclues.length - 1; index++) {
				int valeurCalculee = randInt_trie_valeurs(valeursExclues[index], valeursExclues[index + 1]);
				aff("randInt_trie_valeurs(" + valeursExclues[index] + ", " + valeursExclues[index + 1] + ") = " + valeurCalculee);
				valeursIntermediaires[index + 1] = valeurCalculee;
			}
		}
		valeursIntermediaires[valeursIntermediaires.length - 1] = calculerValeurIntermediaire(valeursExclues[valeursExclues.length - 1], max);

		return selectionnerValeurValide(valeursIntermediaires, min, max);
	}

	/**
	 * Calcule une valeur intermediaire entre deux bornes.
	 */
	private int calculerValeurIntermediaire(int borne1, int borne2) {
		if (borne1 + 1 == borne2) {
			return borne1;
		}
		return randInt_trie_valeurs(borne1, borne2);
	}

	/**
	 * Selectionne une valeur valide parmi les intermediaires.
	 */
	private int selectionnerValeurValide(int[] valeursIntermediaires, int min, int max) {
		int tailleValeursValides = 0;
		for (int index = 0; index < valeursIntermediaires.length; index++) {
			if (valeursIntermediaires[index] > 0) {
				tailleValeursValides++;
			}
		}

		if (tailleValeursValides == 0) {
			return -1;
		}

		int[] valeursValides = new int[tailleValeursValides];
		int indexValide = 0;
		for (int index = 0; index < valeursIntermediaires.length; index++) {
			if (valeursIntermediaires[index] > 0) {
				valeursValides[indexValide++] = valeursIntermediaires[index];
			}
		}

		utilitaire.aff_tab("valeursValides", valeursValides);
		int indexChoisi = randInt(0, valeursValides.length - 1);
		int resultat = valeursValides[indexChoisi];

		if (resultat > max) {
			resultat = max;
		}
		if (resultat < min) {
			resultat = min;
		}
		return resultat;
	}

	/**
	 * Retourne un entier entre min et max, ou -1 si consecutifs.
	 * @param min Valeur minimale.
	 * @param max Valeur maximale.
	 * @return Entier aleatoire ou -1.
	 */
	public int randInt_trie_valeurs(int min, int max) {
		if (min >= max || (min + 1) == max) {
			return -1;
		}
		return randInt(min, max);
	}

	/**
	 * Affiche un message avec saut de ligne.
	 * @param message Message a afficher.
	 */
	public void aff(String message) {
		System.out.println(message);
	}

	/**
	 * Affiche un message sans saut de ligne.
	 * @param message Message a afficher.
	 */
	public void affnn(String message) {
		System.out.print(message);
	}
}
