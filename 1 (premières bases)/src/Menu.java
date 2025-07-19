import java.util.Scanner;

class Menu {
	Utilitaire u;

	public Menu() {
		Utilitaire u = new Utilitaire();
	}

	public int menu_principal () {
		int res = -1;
		aff("\n   ******* Menu principal ******* \n");
		while (res!=1 && res!=2 && res!=3) {
			aff("\t1 : Jeu humain");
			aff("\t2 : Jeu IA (ordinateur contre ordinateur)");
			aff("\t3 : Quitter");
			affnn("\n   ");
			res = entrer_entier("entre 1 et 3");
		}
		return res;
	}

// ###################### Fonctions du jeu ######################## //

// ###******** Fonctions de positionnements des bateaux ********### //

	public int entre_nombre_de_bateaux (int nombre_de_cases_restantes) {
		int res = -1;

		while (res<1 || res>nombre_de_cases_restantes) {
			res = entrer_entier_phrase("inférieur ou égal à "+nombre_de_cases_restantes+" et suppérieur à 0");
		}
		return res;
	}

	public int entre_nombre_de_case_bateau (int nombre_de_cases_restantes, int nombre_de_bateaux_restants) {
		int res = -1;
		int chiffre_a_entrer_inferieur_a = nombre_de_cases_restantes-(nombre_de_bateaux_restants-1);
		while (res<1 || res>chiffre_a_entrer_inferieur_a) {
			res = entrer_entier_phrase("inférieur ou égal à "+chiffre_a_entrer_inferieur_a+" et suppérieur à 0");
		}
		return res;
	}

	/**
		Donne les bateaux d'un joueur
		Chaque case du tableau int[] 
		contient le nombre du case d'un bateau.
	**/
	public int[] entre_nombre_de_case_bateau (int nombre_de_cases_restantes) {
		int i;
		int tmp;
		int [] res;
		int nombre_de_cases_restantes_tmp = nombre_de_cases_restantes;
		int nombre_de_bateaux_restants;

		aff("Il vous reste "+nombre_de_cases_restantes+" cases à occuper avec vos bateaux.");
		aff("Veuillez entrer le nombre de bateaux : ");
		int nombre_de_bateaux = entre_nombre_de_bateaux(nombre_de_cases_restantes);
		nombre_de_bateaux_restants = nombre_de_bateaux;
		res = new int[nombre_de_bateaux];
		for (i=0; i<nombre_de_bateaux ; i++) {
			tmp = entre_nombre_de_case_bateau(nombre_de_cases_restantes_tmp, nombre_de_bateaux_restants);
			res[i] = tmp;
			nombre_de_cases_restantes_tmp-=tmp;
			nombre_de_bateaux_restants--;
		}
		return res;
	}

	/**
		Donne la pemière et la dernière case 
		du bateau.
		Renvoie un int [] :
			int[0] : première case, ordonnée
			int[1] : première case, abscisse
			int[2] : deuxième case, ordonnée
			int[3] : deuxième case, abscisse
	**/
	public int [] donne_cases_bateau (int numero_du_bateau, int nombre_de_cases_bateau, int hauteur) {
		String message = "Entrer la première case du bateau numéro "+numero_du_bateau+" à "+nombre_de_cases_bateau+" case(s) : ";
		int [] case1 = entre_case(message, numero_du_bateau, hauteur);
		message = "Entrer la dernière case du bateau numéro "+numero_du_bateau+" à "+nombre_de_cases_bateau+" case(s) : ";
		int [] case_fin = entre_case(message, numero_du_bateau, hauteur);
		int [] res = new int[4];

		res[0] = case1[0];
		res[1] = case1[1];
		res[2] = case_fin[0];
		res[3] = case_fin[1];

		return res;
	}

	public int [] entre_case (String message, int numero_du_bateau, int hauteur) {
		int i = 0;
		Scanner sc = new Scanner(System.in);
		u = new Utilitaire();
		String reponse = "";
		aff(message);
		while (!u.verifie_format_case(reponse, hauteur)) {
			if (i>0 && !u.verifie_format_case(reponse, hauteur))
				aff("Format de case incorrect");
			reponse = sc.nextLine();
			i++;
		}
		return u.convertit_case_en_coordonnee(reponse, hauteur);
	}

// ################### Fonctions utilitaires ###################### //

	public int entrer_entier (String precision) {
		Scanner sc = new Scanner(System.in);
		u = new Utilitaire();
		String res = "";

		affnn("? = ");
		res = sc.nextLine();
		while (!u.is_integer(res)) {
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