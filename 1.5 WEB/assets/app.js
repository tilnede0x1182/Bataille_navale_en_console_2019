// ==============================================================================
// Application Bataille Navale - Frontend
// ==============================================================================

// ==============================================================================
// Donnees
// ==============================================================================

let tailleGrille = 10;
let placementAutomatique = true;
const ETAT_VIDE = -1;
const ETAT_EAU = 0;
const ETAT_TOUCHE = 1;
const ETAT_COULE = 2;

let grilleJoueur = [];
let grilleAdversaire = [];
let bateauxJoueur = [];
let bateauxAdversaire = [];
let partieEnCours = false;

// Phase de placement
let phasePlacement = false;
let placementCaseDepart = null;
let bateauxAplacer = [];
let indexBateauActuel = 0;
let orientationHorizontale = true;
let tailleCase = 40;

// IA procedurale
const MODE_RECHERCHE = 0;
const MODE_PONCAGE = 1;
let iaMode = MODE_RECHERCHE;
let iaCaseToucheeInitiale = null;
let iaDirectionActuelle = 0;
let iaDirectionsEssayees = [false, false, false, false, false];
let iaCasesTouchees = [];

// Configuration serveur
let modeTriche = false;

// ==============================================================================
// Fonctions utilitaires - Logs
// ==============================================================================

function log(message) {
	console.log("[APP] " + message);
}

// ==============================================================================
// Fonctions utilitaires - DOM
// ==============================================================================

function obtenirElement(identifiant) {
	const element = document.getElementById(identifiant);
	if (!element) {
		log("ERREUR: Element non trouve: " + identifiant);
	}
	return element;
}

function creerElement(balise, classeCSS) {
	const element = document.createElement(balise);
	element.className = classeCSS;
	return element;
}

function afficherMessage(texte) {
	log("Message: " + texte);
	const elementMessage = obtenirElement("message-statut");
	if (elementMessage) {
		elementMessage.textContent = texte;
	}
}

// ==============================================================================
// Fonctions utilitaires - Grille
// ==============================================================================

function initialiserGrilleVide() {
	log("Init grille " + tailleGrille + "x" + tailleGrille);
	const grille = [];
	for (let ligne = 0; ligne < tailleGrille; ligne++) {
		grille[ligne] = [];
		for (let colonne = 0; colonne < tailleGrille; colonne++) {
			grille[ligne][colonne] = ETAT_VIDE;
		}
	}
	return grille;
}

function coordonneesValides(ligne, colonne) {
	const ligneOk = ligne >= 0 && ligne < tailleGrille;
	const colonneOk = colonne >= 0 && colonne < tailleGrille;
	return ligneOk && colonneOk;
}

// ==============================================================================
// Fonctions principales - Rendu grille
// ==============================================================================

function calculerTailleCase() {
	const largeurMax = 500;
	tailleCase = Math.floor(largeurMax / tailleGrille);
	if (tailleCase > 60) tailleCase = 60;
	if (tailleCase < 30) tailleCase = 30;
	log("Taille case: " + tailleCase + "px");
}

function rendreGrille(conteneurId, grille, cliquable) {
	log("Rendu grille: " + conteneurId);
	const conteneur = obtenirElement(conteneurId);
	conteneur.style.gridTemplateColumns = "repeat(" + tailleGrille + ", " + tailleCase + "px)";
	conteneur.style.gridTemplateRows = "repeat(" + tailleGrille + ", " + tailleCase + "px)";
	conteneur.replaceChildren();
	for (let ligne = 0; ligne < tailleGrille; ligne++) {
		for (let colonne = 0; colonne < tailleGrille; colonne++) {
			const etat = grille[ligne][colonne];
			const caseElement = creerCaseElement(ligne, colonne, etat, cliquable, conteneurId);
			conteneur.appendChild(caseElement);
		}
	}
}

function creerCaseElement(ligne, colonne, etat, cliquable, conteneurId) {
	const caseElement = creerElement("div", "case");
	caseElement.dataset.ligne = ligne;
	caseElement.dataset.colonne = colonne;
	caseElement.style.width = tailleCase + "px";
	caseElement.style.height = tailleCase + "px";
	appliquerClasseEtat(caseElement, etat, conteneurId);
	if (cliquable) {
		caseElement.addEventListener("click", gererClicCase);
	}
	if (conteneurId === "grille-joueur" && phasePlacement && !placementAutomatique) {
		caseElement.addEventListener("mouseenter", gererSurvolCase);
		caseElement.addEventListener("mouseleave", effacerPreview);
	}
	return caseElement;
}

function appliquerClasseEtat(element, etat, conteneurId) {
	if (etat === ETAT_EAU) {
		element.classList.add("case--eau");
	} else if (etat === ETAT_TOUCHE) {
		element.classList.add("case--touche");
	} else if (etat === ETAT_COULE) {
		element.classList.add("case--coule");
	} else if (etat === "B") {
		const estGrilleJoueur = conteneurId === "grille-joueur";
		if (estGrilleJoueur || modeTriche) {
			element.classList.add("case--bateau");
		}
	}
}

// ==============================================================================
// Fonctions principales - Placement bateaux
// ==============================================================================

function placerBateauxAleatoire(grille) {
	const taillesBateaux = calculerBateauxSelonGrille();
	log("Placement aleatoire: " + JSON.stringify(taillesBateaux));
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
	const ligneMax = horizontal ? tailleGrille : tailleGrille - taille;
	const colonneMax = horizontal ? tailleGrille - taille : tailleGrille;
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
// Fonctions principales - Placement manuel
// ==============================================================================

function initialiserBateauxAplacer() {
	log("Init bateaux a placer - taille grille: " + tailleGrille);
	bateauxAplacer = calculerBateauxSelonGrille();
	log("Bateaux a placer: " + JSON.stringify(bateauxAplacer));
	indexBateauActuel = 0;
	orientationHorizontale = true;
}

function calculerBateauxSelonGrille() {
	if (tailleGrille >= 10) {
		return [5, 4, 3, 3, 2];
	} else if (tailleGrille >= 8) {
		return [4, 3, 3, 2];
	} else if (tailleGrille >= 6) {
		return [3, 3, 2];
	} else {
		return [2, 2];
	}
}

function demarrerPhasePlacement() {
	log("Phase placement - auto: " + placementAutomatique);
	phasePlacement = true;
	partieEnCours = false;
	if (placementAutomatique) {
		bateauxJoueur = placerBateauxAleatoire(grilleJoueur);
		rendreGrille("grille-joueur", grilleJoueur, true);
		finirPhasePlacement();
	} else {
		afficherMessage("Placez bateau " + (indexBateauActuel + 1) + " (taille " + bateauxAplacer[indexBateauActuel] + ") - R pour pivoter");
	}
}

function gererClicPlacement(ligne, colonne) {
	log("Clic placement [" + ligne + "," + colonne + "]");
	const tailleBateau = bateauxAplacer[indexBateauActuel];
	const cases = construireCasesPreview(ligne, colonne, tailleBateau);
	if (cases === null) {
		afficherMessage("Position invalide !");
		return;
	}
	placerBateauManuel(cases);
}

function construireCasesPreview(ligneDepart, colonneDepart, taille) {
	const cases = [];
	for (let idx = 0; idx < taille; idx++) {
		const caseLigne = orientationHorizontale ? ligneDepart : ligneDepart + idx;
		const caseColonne = orientationHorizontale ? colonneDepart + idx : colonneDepart;
		if (!coordonneesValides(caseLigne, caseColonne)) return null;
		if (grilleJoueur[caseLigne][caseColonne] !== ETAT_VIDE) return null;
		cases.push({ ligne: caseLigne, colonne: caseColonne });
	}
	return cases;
}

function gererSurvolCase(evenement) {
	if (!phasePlacement || placementAutomatique) return;
	const caseElement = evenement.target;
	const ligne = parseInt(caseElement.dataset.ligne);
	const colonne = parseInt(caseElement.dataset.colonne);
	afficherPreview(ligne, colonne);
}

function afficherPreview(ligne, colonne) {
	effacerPreview();
	const tailleBateau = bateauxAplacer[indexBateauActuel];
	const cases = construireCasesPreview(ligne, colonne, tailleBateau);
	const conteneur = obtenirElement("grille-joueur");
	const toutesLesCases = conteneur.querySelectorAll(".case");
	for (let idx = 0; idx < tailleBateau; idx++) {
		const caseLigne = orientationHorizontale ? ligne : ligne + idx;
		const caseColonne = orientationHorizontale ? colonne + idx : colonne;
		const index = caseLigne * tailleGrille + caseColonne;
		if (index >= 0 && index < toutesLesCases.length) {
			const classePreview = cases !== null ? "case--preview" : "case--preview-invalide";
			toutesLesCases[index].classList.add(classePreview);
		}
	}
}

function effacerPreview() {
	const conteneur = obtenirElement("grille-joueur");
	const casesPreview = conteneur.querySelectorAll(".case--preview, .case--preview-invalide");
	casesPreview.forEach(function(caseEl) {
		caseEl.classList.remove("case--preview", "case--preview-invalide");
	});
}

function pivoterBateau() {
	orientationHorizontale = !orientationHorizontale;
	log("Orientation: " + (orientationHorizontale ? "horizontale" : "verticale"));
	afficherMessage("Orientation: " + (orientationHorizontale ? "horizontale" : "verticale"));
}

function placerBateauManuel(cases) {
	log("Bateau place: " + JSON.stringify(cases));
	marquerCasesBateau(grilleJoueur, cases);
	bateauxJoueur.push({ cases: cases, coule: false });
	rendreGrille("grille-joueur", grilleJoueur, true);
	indexBateauActuel++;
	if (indexBateauActuel >= bateauxAplacer.length) {
		finirPhasePlacement();
	} else {
		afficherMessage("Placez bateau " + (indexBateauActuel + 1) + " (taille " + bateauxAplacer[indexBateauActuel] + ") - R pour pivoter");
	}
}

function finirPhasePlacement() {
	log("Fin placement - Debut partie");
	phasePlacement = false;
	partieEnCours = true;
	bateauxAdversaire = placerBateauxAleatoire(grilleAdversaire);
	resetIA();
	rendreGrille("grille-joueur", grilleJoueur, false);
	rendreGrille("grille-adversaire", grilleAdversaire, true);
	afficherMessage("Tirez sur la grille adverse !");
}

// ==============================================================================
// Fonctions principales - IA procedurale
// ==============================================================================

function resetIA() {
	log("Reset IA");
	iaMode = MODE_RECHERCHE;
	iaCaseToucheeInitiale = null;
	iaDirectionActuelle = 0;
	iaDirectionsEssayees = [false, false, false, false, false];
	iaCasesTouchees = [];
}

function iaChoisirCase() {
	log("IA choisit case - mode: " + (iaMode === MODE_RECHERCHE ? "RECHERCHE" : "PONCAGE"));
	if (iaMode === MODE_RECHERCHE) {
		return iaChoisirCaseRecherche();
	} else {
		return iaChoisirCasePoncage();
	}
}

function iaChoisirCaseRecherche() {
	const caseDiagonale = iaTrouverCaseDiagonale();
	if (caseDiagonale !== null) return caseDiagonale;
	return iaTrouverPremiereCaseLibre();
}

function iaTrouverCaseDiagonale() {
	for (let ligne = 0; ligne < tailleGrille; ligne++) {
		for (let colonne = 0; colonne < tailleGrille; colonne++) {
			if ((ligne + colonne) % 2 === 0 && iaCaseLibre(ligne, colonne)) {
				return { ligne: ligne, colonne: colonne };
			}
		}
	}
	return null;
}

function iaTrouverPremiereCaseLibre() {
	for (let ligne = 0; ligne < tailleGrille; ligne++) {
		for (let colonne = 0; colonne < tailleGrille; colonne++) {
			if (iaCaseLibre(ligne, colonne)) {
				return { ligne: ligne, colonne: colonne };
			}
		}
	}
	return null;
}

function iaCaseLibre(ligne, colonne) {
	const etat = grilleJoueur[ligne][colonne];
	return etat === ETAT_VIDE || etat === "B";
}

// ------------------------------------------------------------------------------
// IA procedurale - Mode poncage
// ------------------------------------------------------------------------------

function iaChoisirCasePoncage() {
	if (iaDirectionActuelle === 0) {
		return iaChoisirDirectionInitiale();
	} else {
		return iaContinuerDansDirection();
	}
}

function iaChoisirDirectionInitiale() {
	const directions = [4, 2, 3, 1];
	for (let idx = 0; idx < directions.length; idx++) {
		const dir = directions[idx];
		if (!iaDirectionsEssayees[dir]) {
			const caseAdj = iaCalculerCaseAdjacente(iaCaseToucheeInitiale, dir);
			if (iaCaseValideEtLibre(caseAdj)) {
				iaDirectionActuelle = dir;
				return caseAdj;
			}
			iaDirectionsEssayees[dir] = true;
		}
	}
	resetIA();
	return iaChoisirCaseRecherche();
}

function iaContinuerDansDirection() {
	const derniereTouchee = iaCasesTouchees.length > 0 ? iaCasesTouchees[iaCasesTouchees.length - 1] : iaCaseToucheeInitiale;
	const caseSuivante = iaCalculerCaseAdjacente(derniereTouchee, iaDirectionActuelle);
	if (iaCaseValideEtLibre(caseSuivante)) {
		return caseSuivante;
	}
	const dirInverse = iaInverserDirection(iaDirectionActuelle);
	iaDirectionsEssayees[iaDirectionActuelle] = true;
	if (!iaDirectionsEssayees[dirInverse]) {
		iaDirectionActuelle = dirInverse;
		const caseInverse = iaCalculerCaseAdjacente(iaCaseToucheeInitiale, dirInverse);
		if (iaCaseValideEtLibre(caseInverse)) {
			return caseInverse;
		}
	}
	return iaChoisirDirectionInitiale();
}

function iaCalculerCaseAdjacente(caseOrigine, direction) {
	let nouvLigne = caseOrigine.ligne;
	let nouvColonne = caseOrigine.colonne;
	if (direction === 1) nouvLigne--;
	else if (direction === 2) nouvLigne++;
	else if (direction === 3) nouvColonne--;
	else if (direction === 4) nouvColonne++;
	return { ligne: nouvLigne, colonne: nouvColonne };
}

function iaCaseValideEtLibre(caseTest) {
	if (!coordonneesValides(caseTest.ligne, caseTest.colonne)) return false;
	return iaCaseLibre(caseTest.ligne, caseTest.colonne);
}

function iaInverserDirection(direction) {
	if (direction === 1) return 2;
	if (direction === 2) return 1;
	if (direction === 3) return 4;
	if (direction === 4) return 3;
	return 0;
}

// ------------------------------------------------------------------------------
// IA procedurale - Mise a jour et tir
// ------------------------------------------------------------------------------

function iaMettreAJourEtat(resultat, caseTiree) {
	log("IA MAJ etat - resultat: " + resultat);
	if (resultat === ETAT_COULE) {
		resetIA();
		return;
	}
	if (resultat === ETAT_TOUCHE) {
		iaGererTouche(caseTiree);
		return;
	}
	if (resultat === ETAT_EAU && iaMode === MODE_PONCAGE) {
		iaDirectionsEssayees[iaDirectionActuelle] = true;
		iaDirectionActuelle = 0;
	}
}

function iaGererTouche(caseTiree) {
	if (iaMode === MODE_RECHERCHE) {
		iaMode = MODE_PONCAGE;
		iaCaseToucheeInitiale = caseTiree;
		iaDirectionActuelle = 0;
		iaDirectionsEssayees = [false, false, false, false, false];
		iaCasesTouchees = [];
	}
	iaCasesTouchees.push(caseTiree);
}

function tourIA() {
	log("=== TOUR IA ===");
	const caseIA = iaChoisirCase();
	if (caseIA === null) {
		log("IA: plus de case disponible");
		return;
	}
	effectuerTirIA(caseIA.ligne, caseIA.colonne);
}

function effectuerTirIA(ligne, colonne) {
	log("IA tire [" + ligne + "," + colonne + "]");
	const caseActuelle = grilleJoueur[ligne][colonne];
	let resultat = ETAT_EAU;
	if (caseActuelle === "B") {
		grilleJoueur[ligne][colonne] = ETAT_TOUCHE;
		const bateauCoule = verifierBateauCouleJoueur(ligne, colonne);
		if (bateauCoule) {
			marquerBateauCoule(bateauxJoueur, grilleJoueur, ligne, colonne);
			resultat = ETAT_COULE;
		} else {
			resultat = ETAT_TOUCHE;
		}
	} else {
		grilleJoueur[ligne][colonne] = ETAT_EAU;
	}
	iaMettreAJourEtat(resultat, { ligne: ligne, colonne: colonne });
	rendreGrille("grille-joueur", grilleJoueur, false);
	afficherMessageIA(resultat);
	verifierFinPartie();
}

function afficherMessageIA(resultat) {
	if (resultat === ETAT_EAU) {
		afficherMessage("IA: Rate");
	} else if (resultat === ETAT_TOUCHE) {
		afficherMessage("IA: Touche !");
	} else if (resultat === ETAT_COULE) {
		afficherMessage("IA: Coule !");
	}
}

// ------------------------------------------------------------------------------
// Verification bateau coule
// ------------------------------------------------------------------------------

function verifierBateauCouleJoueur(ligne, colonne) {
	for (let idx = 0; idx < bateauxJoueur.length; idx++) {
		const bateau = bateauxJoueur[idx];
		if (bateau.coule) continue;
		let appartient = bateauContientCase(bateau, ligne, colonne);
		if (!appartient) continue;
		let toutTouche = verifierToutTouche(bateau, grilleJoueur);
		if (toutTouche) {
			bateau.coule = true;
			return true;
		}
	}
	return false;
}

function verifierBateauCouleAdversaire(ligne, colonne) {
	for (let idx = 0; idx < bateauxAdversaire.length; idx++) {
		const bateau = bateauxAdversaire[idx];
		if (bateau.coule) continue;
		let appartient = bateauContientCase(bateau, ligne, colonne);
		if (!appartient) continue;
		let toutTouche = verifierToutTouche(bateau, grilleAdversaire);
		if (toutTouche) {
			bateau.coule = true;
			return true;
		}
	}
	return false;
}

function bateauContientCase(bateau, ligne, colonne) {
	for (let cIdx = 0; cIdx < bateau.cases.length; cIdx++) {
		if (bateau.cases[cIdx].ligne === ligne && bateau.cases[cIdx].colonne === colonne) {
			return true;
		}
	}
	return false;
}

function verifierToutTouche(bateau, grille) {
	for (let cIdx = 0; cIdx < bateau.cases.length; cIdx++) {
		const coord = bateau.cases[cIdx];
		const etat = grille[coord.ligne][coord.colonne];
		if (etat === "B" || etat === ETAT_VIDE) {
			return false;
		}
	}
	return true;
}

function marquerBateauCoule(bateaux, grille, ligne, colonne) {
	for (let idx = 0; idx < bateaux.length; idx++) {
		const bateau = bateaux[idx];
		if (bateauContientCase(bateau, ligne, colonne)) {
			bateau.coule = true;
			for (let cIdx = 0; cIdx < bateau.cases.length; cIdx++) {
				const coord = bateau.cases[cIdx];
				grille[coord.ligne][coord.colonne] = ETAT_COULE;
			}
			break;
		}
	}
}

// ------------------------------------------------------------------------------
// Fin de partie
// ------------------------------------------------------------------------------

function verifierFinPartie() {
	const joueurPerdu = tousCoules(bateauxJoueur);
	const adversairePerdu = tousCoules(bateauxAdversaire);
	if (joueurPerdu) {
		partieEnCours = false;
		afficherModaleFinPartie(false);
	}
	if (adversairePerdu) {
		partieEnCours = false;
		afficherModaleFinPartie(true);
	}
}

function afficherModaleFinPartie(victoire) {
	log("Affichage modale fin de partie - victoire: " + victoire);
	const overlay = creerElement("div", "modale-overlay");
	const modale = creerElement("div", "modale");
	const titre = creerElement("h2", victoire ? "modale-titre--victoire" : "modale-titre--defaite");
	titre.textContent = victoire ? "Gagné" : "Perdu";
	const bouton = creerElement("button", victoire ? "modale-bouton--victoire" : "modale-bouton--defaite");
	bouton.textContent = "OK";
	bouton.addEventListener("click", function() {
		overlay.remove();
		demarrerNouvellePartie();
	});
	modale.appendChild(titre);
	modale.appendChild(bouton);
	overlay.appendChild(modale);
	document.body.appendChild(overlay);
}

function tousCoules(bateaux) {
	for (let idx = 0; idx < bateaux.length; idx++) {
		if (!bateaux[idx].coule) return false;
	}
	return bateaux.length > 0;
}

// ==============================================================================
// Fonctions principales - Logique de jeu
// ==============================================================================

function gererClicCase(evenement) {
	log("Clic case");
	const caseElement = evenement.target;
	const ligne = parseInt(caseElement.dataset.ligne);
	const colonne = parseInt(caseElement.dataset.colonne);
	if (phasePlacement) {
		gererClicPlacement(ligne, colonne);
		return;
	}
	if (!partieEnCours) {
		log("Partie non en cours");
		return;
	}
	effectuerTir(ligne, colonne);
}

function effectuerTir(ligne, colonne) {
	log("Tir [" + ligne + "," + colonne + "]");
	const caseActuelle = grilleAdversaire[ligne][colonne];
	if (caseActuelle !== ETAT_VIDE && caseActuelle !== "B") {
		afficherMessage("Case deja jouee !");
		return;
	}
	const resultat = calculerResultatTir(ligne, colonne);
	rendreGrille("grille-adversaire", grilleAdversaire, true);
	afficherResultatTir(resultat);
	verifierFinPartie();
	if (partieEnCours) {
		setTimeout(tourIA, 800);
	}
}

function calculerResultatTir(ligne, colonne) {
	if (grilleAdversaire[ligne][colonne] === "B") {
		grilleAdversaire[ligne][colonne] = ETAT_TOUCHE;
		const bateauCoule = verifierBateauCouleAdversaire(ligne, colonne);
		if (bateauCoule) {
			marquerBateauCoule(bateauxAdversaire, grilleAdversaire, ligne, colonne);
			return ETAT_COULE;
		}
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
	log("=== NOUVELLE PARTIE ===");
	grilleJoueur = initialiserGrilleVide();
	grilleAdversaire = initialiserGrilleVide();
	bateauxJoueur = [];
	bateauxAdversaire = [];
	initialiserBateauxAplacer();
	rendreGrille("grille-joueur", grilleJoueur, true);
	rendreGrille("grille-adversaire", grilleAdversaire, false);
	demarrerPhasePlacement();
}

function initialiserEcouteurs() {
	log("Init ecouteurs");
	const boutonCommencer = obtenirElement("bouton-commencer");
	if (boutonCommencer) {
		boutonCommencer.addEventListener("click", lancerPartieDepuisMenu);
		log("Ecouteur bouton-commencer OK");
	}
	const boutonNouvelle = obtenirElement("bouton-nouvelle-partie");
	if (boutonNouvelle) {
		boutonNouvelle.addEventListener("click", retourMenu);
		log("Ecouteur bouton-nouvelle-partie OK");
	}
	document.addEventListener("keydown", gererToucheClavier);
	log("Ecouteur clavier OK");
}

function gererToucheClavier(evenement) {
	if (evenement.key === "r" || evenement.key === "R") {
		if (phasePlacement && !placementAutomatique) {
			pivoterBateau();
		}
	}
}

function lancerPartieDepuisMenu() {
	log("=== LANCEMENT PARTIE ===");
	const inputTaille = obtenirElement("input-taille");
	const switchPlacement = obtenirElement("switch-placement-auto");
	tailleGrille = parseInt(inputTaille.value) || 10;
	if (tailleGrille < 4) tailleGrille = 4;
	if (tailleGrille > 10) tailleGrille = 10;
	placementAutomatique = switchPlacement ? switchPlacement.checked : true;
	log("Taille grille: " + tailleGrille);
	log("Placement auto: " + placementAutomatique);
	calculerTailleCase();
	afficherZoneJeu();
	demarrerNouvellePartie();
}

function afficherZoneJeu() {
	log("Affichage zone jeu");
	const menuConfig = obtenirElement("menu-config");
	const zoneJeu = obtenirElement("zone-jeu");
	const zoneControles = obtenirElement("zone-controles");
	if (menuConfig) menuConfig.style.display = "none";
	if (zoneJeu) zoneJeu.style.display = "flex";
	if (zoneControles) zoneControles.style.display = "flex";
}

function retourMenu() {
	log("Retour menu");
	const menuConfig = obtenirElement("menu-config");
	const zoneJeu = obtenirElement("zone-jeu");
	const zoneControles = obtenirElement("zone-controles");
	if (menuConfig) menuConfig.style.display = "flex";
	if (zoneJeu) zoneJeu.style.display = "none";
	if (zoneControles) zoneControles.style.display = "none";
	partieEnCours = false;
}

function chargerConfig() {
	log("Chargement config serveur");
	fetch("/api/config")
		.then(function(reponse) { return reponse.json(); })
		.then(function(config) {
			modeTriche = config.triche || false;
			log("Mode triche: " + modeTriche);
		})
		.catch(function(erreur) {
			log("Erreur chargement config: " + erreur);
		});
}

function initialiserApplication() {
	log("=== INIT APP ===");
	chargerConfig();
	initialiserEcouteurs();
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

document.addEventListener("DOMContentLoaded", function() {
	log("DOMContentLoaded");
	main();
});
