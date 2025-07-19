class Utilitaire {

	public Utilitaire() {}

//################ Fonctions utilitaires du jeu ################//

	public boolean tmp () {
		return true;
	}

	/**
		Format correct : A5.
	**/
	public boolean verifie_format_case (String s0, int hauteur) {
		char tmp, tmp2;
		if (s0==null) return false;
		if (s0.length()==2) {
			tmp = s0.charAt(0);
			if (tmp>='A' && tmp<=('A'+(hauteur-1))) {
				tmp2 = s0.charAt(1);
				if (is_integer(""+tmp2)) return true;
				else return false;
			} else return false;
		} else return false;
	}

	/**
		On donne des coordonnées type A5
		et la fonction donne 1, 5 pour A5
	**/
	public int [] convertit_case_en_coordonnee (String s0, int hauteur) {
		int [] res = new int[2];
		char c='A';
		int ordonnee;
		int abscisse;

		if (verifie_format_case(s0, hauteur)) {
			ordonnee = (int)(s0.charAt(0)-(int)('A')+1);
			abscisse = (int)(s0.charAt(1)-(int)('0'));
			res[0] = ordonnee;
			res[1] = abscisse;
		}
		else {
			aff("convertit_case_en_coordonnee : Format incorrect");
			return null;
		}

		return res;
	}

	public boolean compare_coordonneee (int ordonnee_source, int abscisse_source, int ordonnee_but, int abscisse_but) {
		return (ordonnee_source==ordonnee_but &&
			abscisse_source==abscisse_but);
	}

	/**
		Un bateau peut être soit horizontal ou vertical soit oblique.
		Cas :

		case_fin est égale à case_1
		case_fin à gauche de case_1
		case_fin à droite de case_1
		case_fin en haut de case_1
		case_fin en bas de case_1		
	**/
	public boolean verifie_placement_bateau (int [] case_1, int [] case_fin, int nombre_de_cases) {
		int tmp = 0;

		if (case_1[0]==case_fin[0] && case_1[1]==case_fin[1]) {
			if (nombre_de_cases==1) return true;
			else return false;
		}
		//Horizontal
		if (case_1[0]==case_fin[0]) {
			//case_fin en haut de case_1
			if (case_fin[1]<case_1[1]) {
				tmp = case_1[1]-case_fin[1];
				if (tmp==nombre_de_cases && tmp>0) return true;
				else return false;
			}
			//case_fin en bas de case_1
			if (case_fin[1]>case_1[1]) {
				tmp = case_fin[1]-case_1[1];
				if (tmp==nombre_de_cases && tmp>0) return true;
				else return false;
			}
		}
		//Vertical
		if (case_1[1]==case_fin[1]) {
			//case_fin à gauche de case_1
			if (case_fin[0]<case_1[0]) {
				tmp = case_1[1]-case_fin[1];
				if (tmp==nombre_de_cases && tmp>0) return true;
				else return false;
			}
			//case_fin à droite de case_1
			if (case_fin[0]>case_1[0]) {
				tmp = case_fin[1]-case_1[1];
				if (tmp==nombre_de_cases && tmp>0) return true;
				else return false;
			}
		}
		return false;
	}

//################### Fonctions utilitaires ####################//

	public boolean is_integer (String s0) {
		try {
			int n1 = Integer.parseInt(s0);
			return true;
		}
		catch (Exception e) {
			return false;
		}
	}

	public int randInt (int min, int max) {
		int res = (int)(Math.random()*max)+min;
		if (res>max) res = max;
		if (res<min) res = min;

		return res;
	}

	public void aff (String oo) {
		System.out.println(oo);
	}

	public void affnn (String oo) {
		System.out.print(oo);
	}
}