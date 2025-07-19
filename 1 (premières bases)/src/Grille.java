class Grille {
	public int hauteur;
	public int largeur;

	Utilitaire u;
	public Bateau [][] grille;

	public Grille (int hauteur, int largeur) {
		u = new Utilitaire();
		this.hauteur = hauteur;
		this.largeur = largeur;
		this.grille = new Bateau[hauteur][largeur];
	}

	public void initialisation_de_la_grille () {
		int i, j;

		for (i=0; i<hauteur; i++) {
			for (j=0; j<largeur; j++) {
				grille[i][j] = null;
			}
		}
	}

// ###################### Attaquer une case ####################### //

	/**
		renvoie un boolean :
			true : touché
			false : "dans l'eau !"			
	**/
	public boolean attaquer_une_case (int ordonnee, int abscisse) {
		// "Dans l'eau !"
		if (grille[ordonnee][abscisse]==null) return false;
		// touché
		else {
			grille[ordonnee][abscisse].attaque();
			return true;
		}
	}

// ###################### Ajout d'un bateau ####################### //

	/**
		int nombre_de_cases : nombre de cases du bateau
		int joueur : numéro du joueur
	**/
	public void ajoute_un_bateau_une_case (int ordonnee, int abscisse, int nombre_de_cases, int joueur) {
		grille[ordonnee][abscisse] = new Bateau(nombre_de_cases, joueur);
	}

	/*public void ajoute_bateau (int case_1, int cas_fin) {
		int i;

		for (i=0; i<
	}*/

//################ Fonctions utilitaires du jeu ################//

// ################### Fonctions utilitaires ###################### //

	public void aff (String oo) {
		System.out.println(oo);
	}

	public void affnn (String oo) {
		System.out.print(oo);
	}
}