class Bateau {
	int nombre_de_cases;
	int nombre_de_cases_restantes;
	int joueur;

	// Utilitaire
	Menu menu;

	/**
		int joueur : numéro du joueur
	**/
	public Bateau (int nombre_de_cases, int joueur) {
		menu = new Menu();
		this.nombre_de_cases = nombre_de_cases;
		this.nombre_de_cases = nombre_de_cases_restantes;
		this.joueur = joueur;
	}

// ######## Fonctions utilitaires pour les bateaux ######### //

// ###************** Placement des bateaux **************### //

	public int nombre_de_place_de_bateau (int nombre_de_joueurs, 
		int proposition, int hauteur, int largeur) {
		int nombre_de_cases_total = hauteur*largeur;
		int max_autorise = nombre_de_place_de_bateau_max(nombre_de_joueurs, 
								hauteur, largeur);

		if (proposition<max_autorise) return proposition;
		else return max_autorise;
	}

	/**
		Renvoie le nombre de cases max.

		Soit n le nombre de joueurs
		et m le nombre de cases total
		de la grille.

		Calcul : res = (1/(n+1))*m
	**/
	public int nombre_de_place_de_bateau_max (int nombre_de_joueurs, 
		int hauteur, int largeur) {

		int nombre_de_cases_total = hauteur*largeur;
		int max_autorise = (int)((1.0/(nombre_de_joueurs+1))*nombre_de_cases_total);

		return max_autorise;
	}

	/**
		Donne la taille max d'un bateau
		en fonction du nombre de cases restant.
	**/
	public int taille_max_bateau (int nombre_de_cases_restant, 
		int proposition, int hauteur, int largeur) {
		int max_hauteur_largeur = Math.min(hauteur, largeur);

		if (proposition<nombre_de_cases_restant) {
			if (proposition>max_hauteur_largeur) return max_hauteur_largeur;
			else return proposition;
		}
		else return nombre_de_cases_restant;
	}

	/**
		Donne les bateaux d'un joueur
		Chaque case du tableau int[] 
		contient le nombre du case d'un bateau.
	**/
	public int [] donne_les_bateaux (int nombre_de_joueurs, int hauteur, int largeur) {
		int nombre_de_place_de_bateau_max = nombre_de_place_de_bateau_max(nombre_de_joueurs, hauteur, largeur);

		int [] res = menu.entre_nombre_de_case_bateau(nombre_de_place_de_bateau_max);		

		return res;
	}

// ###********************* Attaque *********************### //

	public void attaque () {
		this.nombre_de_cases_restantes = this.nombre_de_cases_restantes-1;
	}

// ################# Fonctions d'affichage ################# //

	public String toString() {
		String res = "B"+joueur;
		return res;
	}

// ################### Fonctions utilitaires ###################### //

	public void aff (String oo) {
		System.out.println(oo);
	}

	public void affnn (String oo) {
		System.out.print(oo);
	}
}