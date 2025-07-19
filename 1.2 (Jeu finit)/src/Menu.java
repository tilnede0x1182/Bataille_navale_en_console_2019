import java.util.Scanner;

class Menu {
	Utilitaire utilitaire;
	Case case_1;

	public Menu () {
		this.case_1 = new Case();
		this.utilitaire = new Utilitaire();
	}

	public int menu_principal () {
		int res=-1;
		aff("\n   ******* Menu principal ******* \n");
		while (res!=1 && res!=2 && res!=3) {
			aff("\t1 : Jouer");
			aff("\t2 : Jouer � plus de deux joueurs");
			aff("\t3 : Quitter");
			affnn("\n   ");
			res = entrer_entier("entre 1 et 3");
		}
		return res;
	}

// ###################### Fonctions du jeu ######################## //

// ###******** Fonctions de positionnements des bateaux ********### //

	public int menu_positionnement_bateaux_1 (int numero_du_joueur) {
		int res=-1;	
		aff("\n   ******* Menu joueur 2 ******* \n");
		aff("Le joueur "+numero_du_joueur+" doit :");
		while (res!=1 && res!=2) {
			aff("\t1 : Placer ses bateau soi-m�me");
			aff("\t2 : Placer ses bateau al�atoirement");
			affnn("\n   ");
			res = entrer_entier("entre 1 et 2");
		}
		return res;
	}

	public int menu_IA_humain (int numero_du_joueur) {
		int res=-1;	
		aff("\n   ******* Menu joueur 1 ******* \n");
		aff("Le joueur "+numero_du_joueur+" est un :");
		while (res!=1 && res!=2) {
			aff("\t1 : Joueur humain");
			aff("\t2 : joueur IA");
			affnn("\n   ");
			res = entrer_entier("entre 1 et 2");
		}
		return res;
	}

	public boolean IA_humain (int numero_du_joueur) {
		int q = menu_IA_humain(numero_du_joueur);
		if (q==1) return false;
		else return true;
	}

	public int entre_nombre_de_joueurs () {
		int res = -1;
		aff("\nEntrer le nombre de joueurs :");
		while (res<2) {
			res = entrer_entier(" supp�rieur � ou �gal 2");
		}
		return res;
	}

	public int entre_nombre_de_bateaux (int nombre_de_cases_restantes) {
		int res = -1;

		while (res<1 || res>nombre_de_cases_restantes) {
			aff("Veuillez entrer le nombre de bateaux :");
			res = entrer_entier_phrase("inf�rieur ou �gal � "+nombre_de_cases_restantes+" et supp�rieur � 0");
		}
		return res;
	}

	public int entre_nombre_de_case_bateau (Grille grille, int numero_du_bateau, 
			int nombre_de_cases_restantes, int nombre_de_bateaux_restants) {
		int res = -1;
		Bateau bateau_tmp = new Bateau();
		int chiffre_a_entrer_inferieur_a = bateau_tmp.taille_max_bateau(grille, 
				nombre_de_cases_restantes-(nombre_de_bateaux_restants-1));
		while (res<1 || res>chiffre_a_entrer_inferieur_a) {
			aff("Entrer le nombre de cases du bateau "+numero_du_bateau+" : ");
			res = entrer_entier_phrase("inf�rieur ou �gal � "+chiffre_a_entrer_inferieur_a+" et supp�rieur � 0");
		}
		return res;
	}

	/**
		Donne les bateaux d'un joueur
		Chaque case du tableau int[] 
		contient le nombre du case d'un bateau.
	**/
	public int[] entre_nombre_de_case_bateaux (Grille grille, int nombre_de_cases_restantes) {
		int i;
		int tmp;
		int [] res;
		int nombre_de_cases_restantes_tmp = nombre_de_cases_restantes;
		int nombre_de_bateaux_restants;

		aff("Il vous reste "+nombre_de_cases_restantes+" cases � occuper avec vos bateaux.");
		int nombre_de_bateaux = entre_nombre_de_bateaux(nombre_de_cases_restantes);
		nombre_de_bateaux_restants = nombre_de_bateaux;
		res = new int[nombre_de_bateaux];
		for (i=0; i<nombre_de_bateaux ; i++) {
			tmp = entre_nombre_de_case_bateau(grille, (i+1), nombre_de_cases_restantes_tmp, nombre_de_bateaux_restants);
			res[i] = tmp;
			nombre_de_cases_restantes_tmp-=tmp;
			nombre_de_bateaux_restants--;
		}
		return res;
	}

	/**
		Donne la pemi�re et la derni�re case 
		du bateau.
		Renvoie un int [] :
			int[0] : premi�re case, ordonn�e
			int[1] : premi�re case, abscisse
			int[2] : deuxi�me case, ordonn�e
			int[3] : deuxi�me case, abscisse
	**/
	public int [] donne_cases_bateau (Grille grille, int numero_du_bateau, int nombre_de_cases_bateau) {
		String message = "Entrer la premi�re case du bateau num�ro "+numero_du_bateau+" � "+nombre_de_cases_bateau+" case(s) : ";
		int [] case1 = entre_case(grille, message);
		message = "Entrer la derni�re case du bateau num�ro "+numero_du_bateau+" � "+nombre_de_cases_bateau+" case(s) : ";
		int [] case_fin = entre_case(grille, message);
		int [] res = new int[4];

		res[0] = case1[0];
		res[1] = case1[1];
		res[2] = case_fin[0];
		res[3] = case_fin[1];

		return res;
	}

	public int [] entre_case (Grille grille, String message) {
		int i = 0;
		Scanner sc = new Scanner(System.in);
		String reponse = "";
		affnn(message+"\n? = ");
		while (!case_1.verifie_case_existe(grille, reponse)) {
			if (i>0 && !case_1.verifie_case_existe(grille, reponse))
				affnn("Format de case incorrect\n? = ");
			reponse = sc.nextLine();
			i++;
		}
		return case_1.convertit_case_en_coordonnee(grille, reponse);
	}

// ################### Fonctions utilitaires ###################### //

	public int entrer_entier (String precision) {
		Scanner sc = new Scanner(System.in);
		String res = "";

		affnn("? = ");
		res = sc.nextLine();
		while (!is_integer(res)) {
			aff("Veuillez entrer en entier "+precision+" : ");
			affnn("? = ");
			res = sc.nextLine();
		}
		return Integer.parseInt(res);
	}

	public int entrer_entier_phrase (String precision) {
		Scanner sc = new Scanner(System.in);
		String res = "";

		aff("Veuillez entrer en entier "+precision+" : ");
		affnn("? = ");
		res = sc.nextLine();
		while (!is_integer(res)) {
			aff("Veuillez entrer en entier "+precision+" : ");
			affnn("? = ");
			res = sc.nextLine();
		}
		return Integer.parseInt(res);
	}

	public boolean is_integer (String s0) {
		try {
			int n1 = Integer.parseInt(s0);
			return true;
		}
		catch (Exception e) {
			return false;
		}
	}

	public void aff (String oo) {
		System.out.println(oo);
	}

	public void affnn (String oo) {
		System.out.print(oo);
	}
}