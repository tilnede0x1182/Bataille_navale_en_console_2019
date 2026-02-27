// ==============================================================================
// Classe Joueur
// ==============================================================================

/**
	Classe representant un joueur dans le jeu de bataille navale.
	Gere le placement des bateaux, les attaques et l'etat du jeu.
*/
class Joueur {

// ==============================================================================
// Attributs
// ==============================================================================

	int numero_du_joueur;
	Affichage affichage;
	Menu menu;
	Utilitaire utilitaire;
	public boolean IA_humain;
	public Case[][] plateau;
	public Case[] cases_occupees;
	public Case[] cases_tentees;
	public int cases_tentees_indice;
	public boolean a_perdu;
	Bateau[] bateaux;
	public int derniere_case_tentee;
	Grille grille;
	/**
		true : Montrer les deux cartes
		false : ne pas les montrer
	*/
	public boolean carte_a_montrer;
	/**
		IA procedurale pour ce joueur (si IA).
	*/
	IA_procedurale ia_procedurale;
	/**
		Resultat du dernier tir (pour l'IA procedurale).
	*/
	int resultat_dernier_tir;
	/**
		Case du dernier tir (pour l'IA procedurale).
	*/
	Case case_dernier_tir;

// ==============================================================================
// Constructeurs
// ==============================================================================

	/**
		Constructeur avec placement manuel des bateaux.

		@param numero_du_joueur Numero du joueur
		@param grille Grille de jeu
		@param IA_humain true si IA, false si humain
		@param carte_a_montrer true pour afficher les cartes
	*/
	public Joueur(int numero_du_joueur, Grille grille, boolean IA_humain, boolean carte_a_montrer) {
		constructeur(grille, numero_du_joueur, IA_humain, carte_a_montrer);
		place_bateaux();
	}

	/**
		Constructeur avec bateaux pre-definis.

		@param numero_du_joueur Numero du joueur
		@param grille Grille de jeu
		@param bateaux Tableau des bateaux
		@param IA_humain true si IA, false si humain
		@param carte_a_montrer true pour afficher les cartes
	*/
	public Joueur(int numero_du_joueur, Grille grille, Bateau[] bateaux, boolean IA_humain, boolean carte_a_montrer) {
		constructeur(grille, numero_du_joueur, IA_humain, carte_a_montrer);
		this.bateaux = bateaux;
		cases_occupees = utilitaire.cases_occuppees(this);
		plateau = utilitaire.construit_plateau(this);
	}

	/**
		Initialise les attributs communs aux constructeurs.

		@param grille Grille de jeu
		@param numero_du_joueur Numero du joueur
		@param IA_humain true si IA, false si humain
		@param carte_a_montrer true pour afficher les cartes
	*/
	public void constructeur(Grille grille, int numero_du_joueur, boolean IA_humain, boolean carte_a_montrer) {
		this.IA_humain = IA_humain;
		this.carte_a_montrer = carte_a_montrer;
		derniere_case_tentee = -1;
		a_perdu = false;
		this.grille = grille;
		this.numero_du_joueur = numero_du_joueur;
		utilitaire = new Utilitaire();
		affichage = new Affichage();
		menu = new Menu();
		cases_tentees = new Case[grille.hauteur * grille.largeur];
		cases_tentees_indice = 0;
		ia_procedurale = new IA_procedurale(grille);
		resultat_dernier_tir = -1;
		case_dernier_tir = null;
	}

// ==============================================================================
// Fonctions de placement des bateaux
// ==============================================================================

	/**
		Place un bateau sur la grille.

		@param numero_du_bateau Numero du bateau a placer
		@param nombre_de_cases Nombre de cases du bateau
		@return Bateau place
	*/
	public Bateau place_bateau(int numero_du_bateau, int nombre_de_cases) {
		boolean placement_valide = false;
		int[] coordonnees_bateau = null;

		while (!placement_valide) {
			coordonnees_bateau = menu.donne_cases_bateau(grille, numero_du_bateau, nombre_de_cases);
			placement_valide = verifiePlacementBateau(coordonnees_bateau, nombre_de_cases);
			if (!placement_valide) {
				aff("Placement incorrect");
			}
		}
		Case[] cases = utilitaire.donne_cases_bateau(coordonnees_bateau, nombre_de_cases);
		return new Bateau(cases);
	}

	/**
		Verifie si le placement d'un bateau est valide.

		@param coordonnees Coordonnees du bateau
		@param nombre_de_cases Nombre de cases
		@return true si placement valide
	*/
	private boolean verifiePlacementBateau(int[] coordonnees, int nombre_de_cases) {
		int[] case_debut = {coordonnees[0], coordonnees[1]};
		int[] case_fin = {coordonnees[2], coordonnees[3]};
		return utilitaire.verifie_placement_bateau(case_debut, case_fin, nombre_de_cases);
	}

	/**
		Place tous les bateaux du joueur.
	*/
	public void place_bateaux() {
		Bateau bateau_reference = new Bateau();
		int[] tailles_bateaux = bateau_reference.donne_nombre_de_cases_bateaux(this.grille);
		int nombre_de_bateaux = tailles_bateaux.length;
		this.bateaux = new Bateau[nombre_de_bateaux];
		affichage.afficher_carte_joueur(this.grille, this);

		for (int index_bateau = 0; index_bateau < nombre_de_bateaux; index_bateau++) {
			placeBateauAvecVerification(index_bateau, tailles_bateaux[index_bateau]);
		}
	}

	/**
		Place un bateau avec verification de collision.

		@param index_bateau Index du bateau
		@param taille_bateau Taille du bateau
	*/
	private void placeBateauAvecVerification(int index_bateau, int taille_bateau) {
		boolean placement_ok = false;
		Case comparateur = new Case();
		Bateau nouveau_bateau = null;

		while (!placement_ok) {
			nouveau_bateau = place_bateau((index_bateau + 1), taille_bateau);
			cases_occupees = utilitaire.cases_occuppees(this);
			if (!comparateur.contient_case(cases_occupees, nouveau_bateau.cases)) {
				placement_ok = true;
			} else {
				aff("Il y a deja un bateau place la");
			}
			bateaux[index_bateau] = nouveau_bateau;
		}
		cases_occupees = utilitaire.cases_occuppees(this);
		plateau = utilitaire.construit_plateau(this);
		affichage.afficher_carte_joueur(this.grille, this);
	}

// ==============================================================================
// Fonctions d'attaque
// ==============================================================================

	/**
		Attaque une case du plateau.

		@param case_attaquee Case a attaquer
		@return 0=eau, 1=touche, 2=coule
	*/
	public int attaquer_une_case(Case case_attaquee) {
		int ordonnee_attaquee = utilitaire.formate_int(0, grille.hauteur, case_attaquee.ordonnee - 1);
		int abscisse_attaquee = utilitaire.formate_int(0, grille.largeur, case_attaquee.abscisse - 1);
		int resultat = -1;

		resultat = attaqueCasesOccupees(case_attaquee);
		resultat = verifieBateauxCoules(resultat);
		plateau[ordonnee_attaquee][abscisse_attaquee].attaquer();

		if (resultat == -1) {
			this.derniere_case_tentee = 0;
			return 0;
		} else {
			this.derniere_case_tentee = resultat;
			return resultat;
		}
	}

	/**
		Attaque les cases occupees correspondantes.

		@param case_attaquee Case attaquee
		@return Resultat de l'attaque
	*/
	private int attaqueCasesOccupees(Case case_attaquee) {
		Case comparateur = new Case();
		int resultat = -1;

		for (int index = 0; index < cases_occupees.length; index++) {
			if (comparateur.compare_case(cases_occupees[index], case_attaquee)) {
				cases_occupees[index].attaquer();
				resultat = 1;
			}
		}
		return resultat;
	}

	/**
		Verifie si un bateau a ete coule.

		@param resultat_actuel Resultat actuel de l'attaque
		@return Nouveau resultat (2 si coule)
	*/
	private int verifieBateauxCoules(int resultat_actuel) {
		for (int index = 0; index < bateaux.length; index++) {
			if (bateaux[index].attaque(new Case(-1, -1))) {
				if (bateaux[index].est_coule()) {
					met_a_jour_plateau_coule();
					return 2;
				}
			}
		}
		return resultat_actuel;
	}

	/**
		Tente une case en mode IA.

		@param joueur_attaque Joueur attaque
		@return true si la tentative a reussi
	*/
	public boolean tenter_une_case_IA(Joueur joueur_attaque) {
		// IA intelligence = new IA(grille);
		if (carte_a_montrer) affiche_plateau_attaquant(this);

		if (!cases_a_tenter()) {
			aff("Joueur " + numero_du_joueur + ", tenter_une_case : toutes les cases du plateau ont ete tentees");
			return false;
		}

		// Ancienne IA aleatoire (commentee)
		// Case case_tentee = intelligence.genere_une_case_aleat(this);

		// Nouvelle IA procedurale
		Case case_tentee = ia_procedurale.choisir_case(this, resultat_dernier_tir, case_dernier_tir);
		afficheCoordonneesTentee(case_tentee);
		int etat_case = joueur_attaque.attaquer_une_case(case_tentee);
		ajoute_une_case_tentee(joueur_attaque, case_tentee, etat_case);

		// Memorise pour le prochain appel
		resultat_dernier_tir = etat_case;
		case_dernier_tir = case_tentee;
		return true;
	}

	/**
		Affiche les coordonnees de la case tentee.

		@param case_tentee Case tentee
	*/
	private void afficheCoordonneesTentee(Case case_tentee) {
		aff("Joueur " + this.numero_du_joueur + " : ");
		aff("? = " + (char)(('A' + case_tentee.ordonnee - 1)) + case_tentee.abscisse);
	}

	/**
		Tente une case en mode humain.

		@param joueur_attaque Joueur attaque
		@return true si la tentative a reussi
	*/
	public boolean tenter_une_case(Joueur joueur_attaque) {
		if (carte_a_montrer) affiche_plateau_attaquant(this);

		if (!cases_a_tenter()) {
			aff("Joueur " + numero_du_joueur + ", tenter_une_case : toutes les cases du plateau ont ete tentees");
			return false;
		}

		Case case_tentee = demandeCase();
		int etat_case = joueur_attaque.attaquer_une_case(case_tentee);
		ajoute_une_case_tentee(this, case_tentee, etat_case);
		return true;
	}

	/**
		Demande une case a l'utilisateur avec verification.

		@return Case choisie
	*/
	private Case demandeCase() {
		boolean case_en_double = true;
		Case case_tentee = null;

		while (case_en_double) {
			int[] coordonnees = menu.entre_case(grille, "Joueur " + numero_du_joueur + " : \nEntrer la case a jouer");
			case_tentee = new Case(coordonnees[0], coordonnees[1]);

			if (!utilitaire.verifie_case_tentee(this.cases_tentees, cases_tentees_indice, case_tentee)) {
				aff("Attention, la case " + case_tentee.case_format_ecrit() + " a deja ete tentee.");
				aff("Veuillez entrer une autre case :");
			} else {
				case_en_double = false;
			}
		}
		return case_tentee;
	}

// ==============================================================================
// Fonctions de mise a jour du plateau
// ==============================================================================

	/**
		Met a jour les bateaux coules sur le plateau.
	*/
	public void met_a_jour_plateau_coule() {
		for (int index_bateau = 0; index_bateau < bateaux.length; index_bateau++) {
			if (bateaux[index_bateau].est_coule()) {
				marqueCasesBateauCoule(bateaux[index_bateau]);
			}
		}
	}

	/**
		Marque les cases d'un bateau coule.

		@param bateau Bateau coule
	*/
	private void marqueCasesBateauCoule(Bateau bateau) {
		for (int index_case = 0; index_case < bateau.cases.length; index_case++) {
			int ordonnee = bateau.cases[index_case].ordonnee - 1;
			int abscisse = bateau.cases[index_case].abscisse - 1;
			plateau[ordonnee][abscisse].couler();
		}
	}

	/**
		Met a jour les cases coulees dans les cases tentees.

		@param joueur_attaque Joueur attaque
	*/
	public void met_a_jour_cases_coulees(Joueur joueur_attaque) {
		Case comparateur = new Case();
		Case[] cases_coulees = joueur_attaque.liste_des_cases_coulees();

		for (int index_coulee = 0; index_coulee < cases_coulees.length; index_coulee++) {
			for (int index_tentee = 0; index_tentee < cases_tentees_indice; index_tentee++) {
				if (comparateur.compare_case(cases_coulees[index_coulee], cases_tentees[index_tentee])) {
					cases_tentees[index_tentee].couler();
					cases_tentees[index_tentee].etat = 2;
				}
			}
		}
	}

// ==============================================================================
// Fonctions de generation de plateau
// ==============================================================================

	/**
		Genere le plateau des cases tentees.

		@param joueur_attaquant Joueur attaquant
		@return Plateau des cases tentees
	*/
	public int[][] genere_plateau_cases_tentees(Joueur joueur_attaquant) {
		return joueur_attaquant.construit_plateau_cases_tentees();
	}

	/**
		Affiche le plateau de l'attaquant.

		@param joueur_attaquant Joueur attaquant
	*/
	public void affiche_plateau_attaquant(Joueur joueur_attaquant) {
		Affichage aff_local = new Affichage();
		int[][] plateau_tentees = genere_plateau_cases_tentees(joueur_attaquant);
		aff_local.afficher_carte_attaque_joueur(plateau_tentees, joueur_attaquant.numero_du_joueur);
	}

	/**
		Construit le plateau des cases tentees.

		@return Plateau sous forme de tableau 2D
	*/
	public int[][] construit_plateau_cases_tentees() {
		int[][] plateau_tentees = new int[grille.hauteur][grille.largeur];
		initialisePlateauTentees(plateau_tentees);
		remplitPlateauTentees(plateau_tentees);
		return plateau_tentees;
	}

	/**
		Initialise le plateau des tentatives a -1.

		@param plateau_tentees Plateau a initialiser
	*/
	private void initialisePlateauTentees(int[][] plateau_tentees) {
		for (int ligne = 0; ligne < plateau_tentees.length; ligne++) {
			for (int colonne = 0; colonne < plateau_tentees[ligne].length; colonne++) {
				plateau_tentees[ligne][colonne] = -1;
			}
		}
	}

	/**
		Remplit le plateau avec les cases tentees.

		@param plateau_tentees Plateau a remplir
	*/
	private void remplitPlateauTentees(int[][] plateau_tentees) {
		for (int index = 0; index < cases_tentees_indice; index++) {
			int ordonnee = cases_tentees[index].ordonnee - 1;
			int abscisse = cases_tentees[index].abscisse - 1;
			plateau_tentees[ordonnee][abscisse] = cases_tentees[index].etat;
		}
	}

// ==============================================================================
// Fonctions de gestion des bateaux coules
// ==============================================================================

	/**
		Liste toutes les cases des bateaux coules.

		@return Tableau des cases coulees
	*/
	public Case[] liste_des_cases_coulees() {
		int nombre_de_cases = compteNombreCasesCoulees();
		Case[] cases_coulees = new Case[nombre_de_cases];
		remplitCasesCoulees(cases_coulees);
		return cases_coulees;
	}

	/**
		Compte le nombre de cases coulees.

		@return Nombre de cases
	*/
	private int compteNombreCasesCoulees() {
		int total = 0;
		for (int index = 0; index < bateaux.length; index++) {
			if (bateaux[index].coule) {
				total += bateaux[index].nombre_de_cases;
			}
		}
		return total;
	}

	/**
		Remplit le tableau des cases coulees.

		@param cases_coulees Tableau a remplir
	*/
	private void remplitCasesCoulees(Case[] cases_coulees) {
		int index_courant = 0;
		for (int index_bateau = 0; index_bateau < bateaux.length; index_bateau++) {
			if (bateaux[index_bateau].coule) {
				for (int index_case = 0; index_case < bateaux[index_bateau].cases.length; index_case++) {
					cases_coulees[index_courant] = bateaux[index_bateau].cases[index_case];
					index_courant++;
				}
			}
		}
	}

// ==============================================================================
// Fonctions d'etat du joueur
// ==============================================================================

	/**
		Marque le joueur comme perdant.
	*/
	public void perd() {
		this.a_perdu = true;
	}

	/**
		Verifie si le joueur a perdu.

		@return true si tous les bateaux sont coules
	*/
	public boolean a_perdu() {
		if (a_perdu) return true;
		for (int index = 0; index < bateaux.length; index++) {
			if (!bateaux[index].est_coule()) return false;
		}
		perd();
		return true;
	}

	/**
		Verifie s'il reste des cases a tenter.

		@return true s'il reste des cases
	*/
	public boolean cases_a_tenter() {
		return (cases_tentees_indice < cases_tentees.length);
	}

	/**
		Ajoute une case tentee a la liste.

		@param joueur_attaque Joueur attaque
		@param case_tentee Case tentee
		@param etat_case Etat de la case
		@return true si ajout reussi
	*/
	public boolean ajoute_une_case_tentee(Joueur joueur_attaque, Case case_tentee, int etat_case) {
		if (etat_case == 2) {
			met_a_jour_cases_coulees(joueur_attaque);
		}
		if (!cases_a_tenter()) return false;

		case_tentee.etat = etat_case;
		cases_tentees[cases_tentees_indice++] = case_tentee;
		return true;
	}

// ==============================================================================
// Fonctions d'affichage
// ==============================================================================

	/**
		Affiche le statut des bateaux.
	*/
	public void affiche_status_bateaux() {
		for (int index = 0; index < bateaux.length; index++) {
			aff("Bateau " + (index + 1) + " : ");
			bateaux[index].aff_cases_touchee();
		}
	}

	/**
		Affiche des espaces.

		@param nombre_espaces Nombre d'espaces a afficher
	*/
	public void affiche_espaces(int nombre_espaces) {
		for (int index = 0; index < nombre_espaces; index++) {
			affnn(" ");
		}
	}

	/**
		Affiche des sauts de ligne.

		@param nombre_lignes Nombre de lignes
	*/
	public void affiche_sauts_de_ligne(int nombre_lignes) {
		for (int index = 0; index < nombre_lignes; index++) {
			affnn("\n");
		}
	}

	/**
		Retourne le numero du joueur en chaine.

		@return Numero du joueur
	*/
	public String toString() {
		return "" + numero_du_joueur;
	}

	/**
		Retourne le numero du joueur.

		@return Numero du joueur
	*/
	public int get_numero_du_joueur() {
		return this.numero_du_joueur;
	}

// ==============================================================================
// Fonctions utilitaires
// ==============================================================================

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
