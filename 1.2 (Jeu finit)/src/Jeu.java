class Jeu {
	Menu menu;
	Affichage affichage;
	Utilitaire utilitaire;
	Grille grille;
	Joueur [] joueurs;
	IA ia;

	public Jeu (int hauteur, int largeur, int nombre_de_joueurs) {
		int choix = 1;
		menu = new Menu();
		affichage = new Affichage();
		utilitaire = new Utilitaire();
		grille = new Grille(hauteur, largeur);
		ia = new IA(grille);

		choix = menu.menu_principal();
		//choix = 1;
		if (choix==2)
			nombre_de_joueurs = menu.entre_nombre_de_joueurs();
		initialisation_du_jeu(nombre_de_joueurs);
		jeu(nombre_de_joueurs);
		// Le joueur a choisi de quitter le jeu
		if (choix==3) System.exit(0);
	}

	public void initialisation_du_jeu (int nombre_de_joueurs) {
		int i, q=0;
		boolean joueur_IA_humain;
		boolean carte_a_montrer;
		boolean placement_auto_humain = false;
		

		joueurs = new Joueur[nombre_de_joueurs];
		for (i=0; i<nombre_de_joueurs; i++) {
			joueur_IA_humain = menu.IA_humain((i+1));
			carte_a_montrer = !joueur_IA_humain;
			//joueur_IA_humain = true;
			//if (!joueur_IA_humain)
			//	q = menu.menu_positionnement_bateaux_1((i+1));
			//else q = 2;
			q = 2;
			if (q==2) placement_auto_humain = true;
			joueurs[i] = placement_bateaux ((i+1), placement_auto_humain, joueur_IA_humain, carte_a_montrer);
			affichage.afficher_carte_joueur(grille, joueurs[i]);
		}
	}

	/**
		placement_auto_humain :
			True : automatique
			False : humain
	**/
	public Joueur placement_bateaux (int numero_du_joueur, boolean placement_auto_humain, boolean IA_humain, boolean carte_a_montrer) {
		Joueur res;
		if (!placement_auto_humain)
			res = new Joueur(numero_du_joueur, grille, IA_humain, carte_a_montrer);
		else res = ia.place_bateaux_IA(grille, numero_du_joueur, IA_humain, carte_a_montrer);
		return res;
	}

	public void jeu (int nombre_de_joueurs) {
		int i, j;
		int gagnant;
		int nombre_de_joueurs_ayant_perdu = 0;
		boolean [] joueurElimine = new boolean[nombre_de_joueurs];
		boolean impossible_de_tenter_une_case = false;
		boolean fin_du_jeu = false;

		while (!fin_du_jeu) {
			for (i=0; i<nombre_de_joueurs; i++) {
				for (j=0; j<nombre_de_joueurs; j++) {
					if (i!=j && !fin_du_jeu) {
						// IA
						if (joueurs[i].IA_humain) {
							impossible_de_tenter_une_case = (!joueurs[i].tenter_une_case_IA(joueurs[j]));
						}
						// Humain
						else {
							impossible_de_tenter_une_case = (!joueurs[i].tenter_une_case(joueurs[j]));
						}
						//joueurs[j].affiche_status_bateaux();
						utilitaire.afficher_evenement_coup(joueurs[j].derniere_case_tentee);
						affichage.afficher_carte_joueur (grille, joueurs[j]);
					if (impossible_de_tenter_une_case) {
						joueurs[i].perd();
						if (!joueurElimine[i]) {
							joueurElimine[i] = true;
							nombre_de_joueurs_ayant_perdu++;
						}
					}
					if (joueurs[j].a_perdu() && !joueurElimine[j]) {
						joueurElimine[j] = true;
						nombre_de_joueurs_ayant_perdu++;
					}
						// Vérifie si le jeu est terminé
						if (nombre_de_joueurs_ayant_perdu==nombre_de_joueurs-1 
									|| impossible_de_tenter_une_case) {
							aff("\nFin du jeu");
							gagnant = utilitaire.recherche_gagnant (this.joueurs)+1;
							if (gagnant>0)
								aff("\nLe gagnant est le joueur "+gagnant+".\n");
							else
								aff("\nIl n'y a aucun gagnant.\n");
							fin_du_jeu = true;
						}
					}
				}
			}
		}
		// Affichage de la carte d'attaque du dernier coup
		for (i=0; i<nombre_de_joueurs; i++) {
			joueurs[i].affiche_plateau_attaquant(joueurs[i]);
		}
			
		// Affichage des cartes des joueurs à la fin du jeu
		for (i=0; i<nombre_de_joueurs; i++) {
			joueurs[i].carte_a_montrer = true;
			affichage.afficher_carte_joueur(grille, joueurs[i]);
		}
	}

// ###################### Main ###################### //

	public static void main (String [] args) {
		Jeu jeu = new Jeu(4, 4, 2);
	}

// ################### Fonctions utilitaires ###################### //

	public void aff (String oo) {
		System.out.println(oo);
	}

	public void affnn (String oo) {
		System.out.print(oo);
	}	
}
