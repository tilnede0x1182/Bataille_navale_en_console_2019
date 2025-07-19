class Jeu {
	Grille grille;
	int nombre_de_joueurs;

	// Utilitaire
	Utilitaire u;
	Affichage affichage;
	Menu menu;

	public Jeu (int hauteur, int largeur, int nombre_de_joueurs) {
		u = new Utilitaire();
		menu = new Menu();
		this.nombre_de_joueurs = nombre_de_joueurs;
		grille = new Grille(hauteur, largeur);
		affichage = new Affichage(grille);
		affichage.affiche();

	// ####################### Tests ########################## //

		/*int [] tmp = menu.donne_cases_bateau (1, 2, grille.hauteur);
		aff("ordonnée case 1 : "+tmp[0]);
		aff("abscisse case 1 : "+tmp[1]);
		aff("ordonnée case fin : "+tmp[2]);
		aff("abscisse case fin : "+tmp[3]);*/

		int choix;
		while (true) {
			choix = menu.menu_principal();
			if (choix==3) System.exit(0);
		}
		// A continuer...
	}

	public void initialisation_du_jeu () {

	}

	public void partie () {

	}















// ################# Fonctions utiliaires du jeu ################## //

	public void placer_un_bateau (int case_debut, int case_fin, int nombre_de_cases) {
		
	}

// ############################## Main ############################ //

	public static void main (String [] args) {
		Jeu jeu = new Jeu(5, 5, 1);
	}

// ################### Fonctions utilitaires ###################### //

	public void aff (String oo) {
		System.out.println(oo);
	}

	public void affnn (String oo) {
		System.out.print(oo);
	}
}