class Affichage {
	Grille grille;
	int nombre_d_espaces;

	public Affichage (Grille grille) {
		this.grille = grille;
		this.nombre_d_espaces = 3;
	}

	public void affiche () {
		char c = 'A';
		int i, j, h;
		int hauteur = grille.hauteur;
		int largeur = grille.largeur;

		affiche_espaces(nombre_d_espaces+1);
		for (i=0; i<largeur; i++) {
			affnn(""+(i+1));
			if (i>(9-1))
				affiche_espaces(nombre_d_espaces-1);
			else
				affiche_espaces(nombre_d_espaces);
		}
		affiche_sauts_de_ligne(1);

		affiche_sauts_de_ligne(1);
		for (i=0; i<hauteur; i++) {
			affnn(""+(c++));
			affiche_espaces(nombre_d_espaces);
			for (j=0; j<largeur; j++) {
				if (grille.grille[i][j]!=null) {
					affnn(grille.grille[i][j].toString());
					affiche_espaces(1);
				}
				else {
					affnn("0");
					affiche_espaces(nombre_d_espaces);
				}
			}
			affiche_sauts_de_ligne(2);
		}
	}

// ############# Fonctions utilitaires d'affichage ################ //


// ################### Fonctions utilitaires ###################### //

	public void affiche_espaces (int nombre_d_espaces) {
		int i;

		for (i=0; i<nombre_d_espaces; i++) {
			affnn(" ");
		}
	}

	public void affiche_sauts_de_ligne (int nombre_de_lignes) {
		int i;

		for (i=0; i<nombre_de_lignes; i++) {
			affnn("\n");
		}
	}

	public void aff (String oo) {
		System.out.println(oo);
	}

	public void affnn (String oo) {
		System.out.print(oo);
	}
}