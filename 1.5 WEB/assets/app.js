// ==============================================================================
// Application Bataille Navale - Frontend
// ==============================================================================

// ==============================================================================
// Donnees
// ==============================================================================

const TAILLE_GRILLE = 4;
const ETAT_VIDE = -1;
const ETAT_EAU = 0;
const ETAT_TOUCHE = 1;
const ETAT_COULE = 2;

let grilleJoueur = [];
let grilleAdversaire = [];
let bateauxJoueur = [];
let partieEnCours = false;

// ==============================================================================
// Fonctions utilitaires - DOM
// ==============================================================================

function obtenirElement(identifiant) {
	return document.getElementById(identifiant);
}

function creerElement(balise, classeCSS) {
	const element = document.createElement(balise);
	element.className = classeCSS;
	return element;
}

function afficherMessage(texte) {
	const elementMessage = obtenirElement("message-statut");
	elementMessage.textContent = texte;
}

// ==============================================================================
// Fonctions utilitaires - Grille
// ==============================================================================

function initialiserGrilleVide() {
	const grille = [];
	for (let ligne = 0; ligne < TAILLE_GRILLE; ligne++) {
		grille[ligne] = [];
		for (let colonne = 0; colonne < TAILLE_GRILLE; colonne++) {
			grille[ligne][colonne] = ETAT_VIDE;
		}
	}
	return grille;
}

function coordonneesValides(ligne, colonne) {
	const ligneOk = ligne >= 0 && ligne < TAILLE_GRILLE;
	const colonneOk = colonne >= 0 && colonne < TAILLE_GRILLE;
	return ligneOk && colonneOk;
}

// ==============================================================================
// Fonctions principales - Rendu grille
// ==============================================================================

function rendreGrille(conteneurId, grille, cliquable) {
	const conteneur = obtenirElement(conteneurId);
	conteneur.replaceChildren();
	for (let ligne = 0; ligne < TAILLE_GRILLE; ligne++) {
		for (let colonne = 0; colonne < TAILLE_GRILLE; colonne++) {
			const etat = grille[ligne][colonne];
			const caseElement = creerCaseElement(ligne, colonne, etat, cliquable);
			conteneur.appendChild(caseElement);
		}
	}
}

function creerCaseElement(ligne, colonne, etat, cliquable) {
	const caseElement = creerElement("div", "case");
	caseElement.dataset.ligne = ligne;
	caseElement.dataset.colonne = colonne;
	appliquerClasseEtat(caseElement, etat);
	if (cliquable) {
		caseElement.addEventListener("click", gererClicCase);
	}
	return caseElement;
}

function appliquerClasseEtat(element, etat) {
	if (etat === ETAT_EAU) {
		element.classList.add("case--eau");
	} else if (etat === ETAT_TOUCHE) {
		element.classList.add("case--touche");
	} else if (etat === ETAT_COULE) {
		element.classList.add("case--coule");
	}
}

// ==============================================================================
// Fonctions principales - Placement bateaux
// ==============================================================================

function placerBateauxAleatoire(grille) {
	const taillesBateaux = [2, 2];
	const bateaux = [];
	for (let index = 0; index < taillesBateaux.length; index++) {
		const bateau = placerUnBateau(grille, taillesBateaux[index]);
		bateaux.push(bateau);
	}
	return bateaux;
}

function placerUnBateau(grille, taille) {
	let placementOk = false;
	let cases = [];
	while (!placementOk) {
		cases = genererPositionBateau(taille);
		placementOk = verifierPlacementValide(grille, cases);
	}
	marquerCasesBateau(grille, cases);
	return { cases: cases, coule: false };
}

function genererPositionBateau(taille) {
	const horizontal = Math.random() < 0.5;
	const ligneMax = horizontal ? TAILLE_GRILLE : TAILLE_GRILLE - taille;
	const colonneMax = horizontal ? TAILLE_GRILLE - taille : TAILLE_GRILLE;
	const ligneDepart = Math.floor(Math.random() * ligneMax);
	const colonneDepart = Math.floor(Math.random() * colonneMax);
	return construireCasesBateau(ligneDepart, colonneDepart, taille, horizontal);
}

function construireCasesBateau(ligne, colonne, taille, horizontal) {
	const cases = [];
	for (let index = 0; index < taille; index++) {
		const caseLigne = horizontal ? ligne : ligne + index;
		const caseColonne = horizontal ? colonne + index : colonne;
		cases.push({ ligne: caseLigne, colonne: caseColonne });
	}
	return cases;
}

function verifierPlacementValide(grille, cases) {
	for (let index = 0; index < cases.length; index++) {
		const coord = cases[index];
		if (!coordonneesValides(coord.ligne, coord.colonne)) {
			return false;
		}
		if (grille[coord.ligne][coord.colonne] !== ETAT_VIDE) {
			return false;
		}
	}
	return true;
}

function marquerCasesBateau(grille, cases) {
	for (let index = 0; index < cases.length; index++) {
		const coord = cases[index];
		grille[coord.ligne][coord.colonne] = "B";
	}
}

// ==============================================================================
// Fonctions principales - Logique de jeu
// ==============================================================================

function gererClicCase(evenement) {
	if (!partieEnCours) {
		return;
	}
	const caseElement = evenement.target;
	const ligne = parseInt(caseElement.dataset.ligne);
	const colonne = parseInt(caseElement.dataset.colonne);
	effectuerTir(ligne, colonne);
}

function effectuerTir(ligne, colonne) {
	const caseActuelle = grilleAdversaire[ligne][colonne];
	if (caseActuelle !== ETAT_VIDE && caseActuelle !== "B") {
		afficherMessage("Case deja jouee !");
		return;
	}
	const resultat = calculerResultatTir(ligne, colonne);
	grilleAdversaire[ligne][colonne] = resultat;
	rendreGrille("grille-adversaire", grilleAdversaire, true);
	afficherResultatTir(resultat);
}

function calculerResultatTir(ligne, colonne) {
	if (grilleAdversaire[ligne][colonne] === "B") {
		return ETAT_TOUCHE;
	}
	return ETAT_EAU;
}

function afficherResultatTir(resultat) {
	if (resultat === ETAT_EAU) {
		afficherMessage("Dans l eau !");
	} else if (resultat === ETAT_TOUCHE) {
		afficherMessage("Touche !");
	} else if (resultat === ETAT_COULE) {
		afficherMessage("Coule !");
	}
}

// ==============================================================================
// Fonctions principales - Initialisation
// ==============================================================================

function demarrerNouvellePartie() {
	grilleJoueur = initialiserGrilleVide();
	grilleAdversaire = initialiserGrilleVide();
	bateauxJoueur = placerBateauxAleatoire(grilleJoueur);
	placerBateauxAleatoire(grilleAdversaire);
	rendreGrille("grille-joueur", grilleJoueur, false);
	rendreGrille("grille-adversaire", grilleAdversaire, true);
	partieEnCours = true;
	afficherMessage("A vous de jouer !");
}

function initialiserEcouteurs() {
	const boutonNouvelle = obtenirElement("bouton-nouvelle-partie");
	boutonNouvelle.addEventListener("click", demarrerNouvellePartie);
}

function initialiserApplication() {
	initialiserEcouteurs();
	demarrerNouvellePartie();
}

// ==============================================================================
// Main
// ==============================================================================

function main() {
	initialiserApplication();
}

// ==============================================================================
// Lancement du programme
// ==============================================================================

document.addEventListener("DOMContentLoaded", main);
