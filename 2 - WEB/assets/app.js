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
let derniereSurvolLigne = -1;
let derniereSurvolColonne = -1;

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

/**
	Affiche un message de log avec prefixe [APP].

	@param {string} message Message a afficher
*/
function log(message) {
	console.log("[APP] " + message);
}

// ==============================================================================
// Fonctions utilitaires - DOM
// ==============================================================================

/**
	Recupere un element du DOM par son identifiant.

	@param {string} identifiant Identifiant de l element
	@return {HTMLElement|null} Element trouve ou null
*/
function obtenirElement(identifiant) {
	const element = document.getElementById(identifiant);
	if (!element) {
		log("ERREUR: Element non trouve: " + identifiant);
	}
	return element;
}

/**
	Cree un element HTML avec une classe CSS.

	@param {string} balise Nom de la balise HTML
	@param {string} classeCSS Classe CSS a appliquer
	@return {HTMLElement} Element cree
*/
function creerElement(balise, classeCSS) {
	const element = document.createElement(balise);
	element.className = classeCSS;
	return element;
}

/**
	Affiche un message de statut a l utilisateur.

	@param {string} texte Texte du message
*/
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

/**
	Cree une grille vide avec toutes les cases a ETAT_VIDE.

	@return {Array} Grille 2D initialisee
*/
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

/**
	Verifie si des coordonnees sont dans les limites de la grille.

	@param {number} ligne Numero de ligne
	@param {number} colonne Numero de colonne
	@return {boolean} True si coordonnees valides
*/
function coordonneesValides(ligne, colonne) {
	const ligneOk = ligne >= 0 && ligne < tailleGrille;
	const colonneOk = colonne >= 0 && colonne < tailleGrille;
	return ligneOk && colonneOk;
}

// ==============================================================================
// Fonctions principales - Rendu grille
// ==============================================================================

/**
	Calcule la taille optimale des cases selon la largeur de l ecran.
*/
function calculerTailleCase() {
	const largeurEcran = window.innerWidth;
	const margeSecurite = 40;
	const gapZoneJeu = 32;
	const paddingSection = 48;
	const paddingGrille = 16;
	const gapCases = 4;
	const espaceFixeParGrille = paddingSection + paddingGrille + (gapCases * (tailleGrille - 1));
	const largeurDisponible = largeurEcran - margeSecurite - gapZoneJeu - (espaceFixeParGrille * 2);
	const largeurParGrille = largeurDisponible / 2;
	tailleCase = Math.floor(largeurParGrille / tailleGrille);
	if (tailleCase > 60) tailleCase = 60;
	if (tailleCase < 10) tailleCase = 10;
	log("Ecran: " + largeurEcran + "px, taille case: " + tailleCase + "px");
}

/**
	Affiche une grille dans le DOM.

	@param {string} conteneurId Identifiant du conteneur
	@param {Array} grille Grille a afficher
	@param {boolean} cliquable True si les cases sont cliquables
*/
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

/**
	Cree un element DOM representant une case de la grille.

	@param {number} ligne Numero de ligne
	@param {number} colonne Numero de colonne
	@param {number|string} etat Etat de la case
	@param {boolean} cliquable True si la case est cliquable
	@param {string} conteneurId Identifiant du conteneur parent
	@return {HTMLElement} Element case cree
*/
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

/**
	Applique la classe CSS correspondant a l etat de la case.

	@param {HTMLElement} element Element DOM de la case
	@param {number|string} etat Etat de la case
	@param {string} conteneurId Identifiant du conteneur parent
*/
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

/**
	Place tous les bateaux aleatoirement sur une grille.

	@param {Array} grille Grille ou placer les bateaux
	@return {Array} Liste des bateaux places
*/
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

/**
	Place un bateau de taille donnee sur la grille.

	@param {Array} grille Grille ou placer le bateau
	@param {number} taille Taille du bateau
	@return {Object} Bateau place avec ses cases
*/
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

/**
	Genere une position aleatoire pour un bateau.

	@param {number} taille Taille du bateau
	@return {Array} Liste des cases du bateau
*/
function genererPositionBateau(taille) {
	const horizontal = Math.random() < 0.5;
	const ligneMax = horizontal ? tailleGrille : tailleGrille - taille;
	const colonneMax = horizontal ? tailleGrille - taille : tailleGrille;
	const ligneDepart = Math.floor(Math.random() * ligneMax);
	const colonneDepart = Math.floor(Math.random() * colonneMax);
	return construireCasesBateau(ligneDepart, colonneDepart, taille, horizontal);
}

/**
	Construit la liste des cases occupees par un bateau.

	@param {number} ligne Ligne de depart
	@param {number} colonne Colonne de depart
	@param {number} taille Taille du bateau
	@param {boolean} horizontal True si orientation horizontale
	@return {Array} Liste des cases du bateau
*/
function construireCasesBateau(ligne, colonne, taille, horizontal) {
	const cases = [];
	for (let index = 0; index < taille; index++) {
		const caseLigne = horizontal ? ligne : ligne + index;
		const caseColonne = horizontal ? colonne + index : colonne;
		cases.push({ ligne: caseLigne, colonne: caseColonne });
	}
	return cases;
}

/**
	Verifie si un placement de bateau est valide.

	@param {Array} grille Grille a verifier
	@param {Array} cases Cases a occuper
	@return {boolean} True si placement valide
*/
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

/**
	Marque les cases d un bateau sur la grille.

	@param {Array} grille Grille a modifier
	@param {Array} cases Cases a marquer
*/
function marquerCasesBateau(grille, cases) {
	for (let index = 0; index < cases.length; index++) {
		const coord = cases[index];
		grille[coord.ligne][coord.colonne] = "B";
	}
}

// ==============================================================================
// Fonctions principales - Placement manuel
// ==============================================================================

/**
	Initialise la liste des bateaux a placer manuellement.
*/
function initialiserBateauxAplacer() {
	log("Init bateaux a placer - taille grille: " + tailleGrille);
	bateauxAplacer = calculerBateauxSelonGrille();
	log("Bateaux a placer: " + JSON.stringify(bateauxAplacer));
	indexBateauActuel = 0;
	orientationHorizontale = true;
}

/**
	Calcule le nombre et tailles des bateaux selon la taille de grille.

	@return {Array} Liste des tailles de bateaux
*/
function calculerBateauxSelonGrille() {
	const surface = tailleGrille * tailleGrille;
	const casesOccupees = Math.round(surface * 0.17);
	const tailleMax = Math.max(2, Math.floor(tailleGrille / 2));
	const tailleMin = 2;
	const tailleMoyenne = (tailleMax + tailleMin) / 2;
	const nombreBateaux = Math.max(1, Math.round(casesOccupees / tailleMoyenne));
	const bateaux = genererTaillesBateaux(nombreBateaux, tailleMax, tailleMin, casesOccupees);
	log("Surface: " + surface + ", cases occupees: " + casesOccupees + ", bateaux: " + nombreBateaux);
	return bateaux;
}

/**
	Genere les tailles de bateaux a placer.

	@param {number} nombreBateaux Nombre de bateaux souhaite
	@param {number} tailleMax Taille maximale d un bateau
	@param {number} tailleMin Taille minimale d un bateau
	@param {number} casesOccupees Nombre total de cases a occuper
	@return {Array} Liste des tailles de bateaux
*/
function genererTaillesBateaux(nombreBateaux, tailleMax, tailleMin, casesOccupees) {
	const bateaux = [];
	let casesRestantes = casesOccupees;
	for (var idx = 0; idx < nombreBateaux; idx++) {
		const ratio = 1 - (idx / nombreBateaux);
		let taille = Math.round(tailleMin + (tailleMax - tailleMin) * ratio);
		taille = Math.max(tailleMin, Math.min(tailleMax, taille));
		if (casesRestantes < taille) {
			taille = Math.max(tailleMin, casesRestantes);
		}
		if (taille >= tailleMin && casesRestantes >= taille) {
			bateaux.push(taille);
			casesRestantes -= taille;
		}
	}
	if (bateaux.length === 0) {
		bateaux.push(tailleMin);
	}
	return bateaux;
}

/**
	Demarre la phase de placement des bateaux.
*/
function demarrerPhasePlacement() {
	log("Phase placement - auto: " + placementAutomatique);
	phasePlacement = true;
	partieEnCours = false;
	if (tailleGrille === 1) {
		placerBateauCasUnique();
		return;
	}
	if (placementAutomatique) {
		bateauxJoueur = placerBateauxAleatoire(grilleJoueur);
		rendreGrille("grille-joueur", grilleJoueur, true);
		finirPhasePlacement();
	} else {
		rendreGrille("grille-joueur", grilleJoueur, true);
		afficherPanneauBateauxRestants();
		afficherMessage("Placez bateau " + (indexBateauActuel + 1) + " (taille " + bateauxAplacer[indexBateauActuel] + ") - R pour pivoter");
	}
}

/**
	Affiche le panneau listant les bateaux restants a placer.
*/
function afficherPanneauBateauxRestants() {
	const panneau = obtenirElement("panneau-bateaux-restants");
	if (!panneau) return;
	const bateauxRestants = bateauxAplacer.slice(indexBateauActuel);
	if (bateauxRestants.length === 0) {
		panneau.style.display = "none";
		return;
	}
	panneau.style.display = "block";
	const compteurParTaille = compterBateauxParTaille(bateauxRestants);
	const contenu = construireContenuPanneau(bateauxRestants.length, compteurParTaille);
	panneau.replaceChildren();
	panneau.appendChild(contenu);
}

/**
	Compte les bateaux par taille.

	@param {Array} bateaux Liste des tailles de bateaux
	@return {Object} Compteur par taille
*/
function compterBateauxParTaille(bateaux) {
	const compteur = {};
	for (var idx = 0; idx < bateaux.length; idx++) {
		const taille = bateaux[idx];
		if (compteur[taille]) {
			compteur[taille]++;
		} else {
			compteur[taille] = 1;
		}
	}
	return compteur;
}

/**
	Construit le contenu HTML du panneau de bateaux restants.

	@param {number} nombreTotal Nombre total de bateaux restants
	@param {Object} compteurParTaille Compteur de bateaux par taille
	@return {DocumentFragment} Fragment DOM du contenu
*/
function construireContenuPanneau(nombreTotal, compteurParTaille) {
	const conteneur = document.createDocumentFragment();
	const titre = creerElement("h3", "");
	titre.textContent = nombreTotal + " bateau" + (nombreTotal > 1 ? "x" : "") + " restant" + (nombreTotal > 1 ? "s" : "");
	conteneur.appendChild(titre);
	const liste = creerElement("ul", "");
	const tailles = Object.keys(compteurParTaille).map(Number).sort(function(a, b) { return b - a });
	for (var idx = 0; idx < tailles.length; idx++) {
		const taille = tailles[idx];
		const nombre = compteurParTaille[taille];
		const item = creerElement("li", "");
		item.textContent = nombre + " bateau" + (nombre > 1 ? "x" : "") + " de " + taille + " case" + (taille > 1 ? "s" : "");
		liste.appendChild(item);
	}
	conteneur.appendChild(liste);
	return conteneur;
}

/**
	Place un bateau unique pour une grille 1x1.
*/
function placerBateauCasUnique() {
	log("Cas unique 1x1 - placement automatique");
	grilleJoueur[0][0] = "B";
	bateauxJoueur = [{ cases: [{ ligne: 0, colonne: 0 }], coule: false }];
	rendreGrille("grille-joueur", grilleJoueur, true);
	finirPhasePlacement();
}

/**
	Gere le clic sur une case pendant la phase de placement.

	@param {number} ligne Ligne cliquee
	@param {number} colonne Colonne cliquee
*/
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

/**
	Construit les cases de previsualisation du bateau.

	@param {number} ligneDepart Ligne de depart
	@param {number} colonneDepart Colonne de depart
	@param {number} taille Taille du bateau
	@return {Array|null} Cases du bateau ou null si invalide
*/
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

/**
	Gere le survol d une case pendant le placement.

	@param {Event} evenement Evenement mouseenter
*/
function gererSurvolCase(evenement) {
	if (!phasePlacement || placementAutomatique) return;
	const caseElement = evenement.target;
	const ligne = parseInt(caseElement.dataset.ligne);
	const colonne = parseInt(caseElement.dataset.colonne);
	derniereSurvolLigne = ligne;
	derniereSurvolColonne = colonne;
	afficherPreview(ligne, colonne);
}

/**
	Affiche la previsualisation du bateau a placer.

	@param {number} ligne Ligne de depart
	@param {number} colonne Colonne de depart
*/
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

/**
	Efface la previsualisation du bateau.
*/
function effacerPreview() {
	const conteneur = obtenirElement("grille-joueur");
	const casesPreview = conteneur.querySelectorAll(".case--preview, .case--preview-invalide");
	casesPreview.forEach(function(caseEl) {
		caseEl.classList.remove("case--preview", "case--preview-invalide");
	});
}

/**
	Change l orientation du bateau a placer.
*/
function pivoterBateau() {
	orientationHorizontale = !orientationHorizontale;
	log("Orientation: " + (orientationHorizontale ? "horizontale" : "verticale"));
	afficherMessage("Orientation: " + (orientationHorizontale ? "horizontale" : "verticale"));
	if (derniereSurvolLigne >= 0 && derniereSurvolColonne >= 0) {
		afficherPreview(derniereSurvolLigne, derniereSurvolColonne);
	}
}

/**
	Place un bateau manuellement sur la grille du joueur.

	@param {Array} cases Cases du bateau a placer
*/
function placerBateauManuel(cases) {
	log("Bateau place: " + JSON.stringify(cases));
	marquerCasesBateau(grilleJoueur, cases);
	bateauxJoueur.push({ cases: cases, coule: false });
	rendreGrille("grille-joueur", grilleJoueur, true);
	indexBateauActuel++;
	if (indexBateauActuel >= bateauxAplacer.length) {
		cacherPanneauBateauxRestants();
		finirPhasePlacement();
	} else {
		afficherPanneauBateauxRestants();
		afficherMessage("Placez bateau " + (indexBateauActuel + 1) + " (taille " + bateauxAplacer[indexBateauActuel] + ") - R pour pivoter");
	}
}

/**
	Cache le panneau des bateaux restants.
*/
function cacherPanneauBateauxRestants() {
	const panneau = obtenirElement("panneau-bateaux-restants");
	if (panneau) {
		panneau.style.display = "none";
	}
}

/**
	Termine la phase de placement et demarre la partie.
*/
function finirPhasePlacement() {
	log("Fin placement - Debut partie");
	phasePlacement = false;
	partieEnCours = true;
	if (tailleGrille === 1) {
		grilleAdversaire[0][0] = "B";
		bateauxAdversaire = [{ cases: [{ ligne: 0, colonne: 0 }], coule: false }];
	} else {
		bateauxAdversaire = placerBateauxAleatoire(grilleAdversaire);
	}
	resetIA();
	rendreGrille("grille-joueur", grilleJoueur, false);
	rendreGrille("grille-adversaire", grilleAdversaire, true);
	afficherMessage("Tirez sur la grille adverse !");
}

// ==============================================================================
// Fonctions principales - IA procedurale
// ==============================================================================

/**
	Reinitialise l etat de l IA.
*/
function resetIA() {
	log("Reset IA");
	iaMode = MODE_RECHERCHE;
	iaCaseToucheeInitiale = null;
	iaDirectionActuelle = 0;
	iaDirectionsEssayees = [false, false, false, false, false];
	iaCasesTouchees = [];
}

/**
	Choisit la prochaine case a tirer pour l IA.

	@return {Object|null} Coordonnees de la case ou null
*/
function iaChoisirCase() {
	log("IA choisit case - mode: " + (iaMode === MODE_RECHERCHE ? "RECHERCHE" : "PONCAGE"));
	if (iaMode === MODE_RECHERCHE) {
		return iaChoisirCaseRecherche();
	} else {
		return iaChoisirCasePoncage();
	}
}

/**
	Choisit une case en mode recherche (diagonale puis sequentiel).

	@return {Object|null} Coordonnees de la case ou null
*/
function iaChoisirCaseRecherche() {
	const caseDiagonale = iaTrouverCaseDiagonale();
	if (caseDiagonale !== null) return caseDiagonale;
	return iaTrouverPremiereCaseLibre();
}

/**
	Trouve une case libre sur les diagonales.

	@return {Object|null} Coordonnees de la case ou null
*/
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

/**
	Trouve la premiere case libre de la grille.

	@return {Object|null} Coordonnees de la case ou null
*/
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

/**
	Verifie si une case est libre pour l IA.

	@param {number} ligne Ligne a verifier
	@param {number} colonne Colonne a verifier
	@return {boolean} True si case libre
*/
function iaCaseLibre(ligne, colonne) {
	const etat = grilleJoueur[ligne][colonne];
	return etat === ETAT_VIDE || etat === "B";
}

// ------------------------------------------------------------------------------
// IA procedurale - Mode poncage
// ------------------------------------------------------------------------------

/**
	Choisit une case en mode poncage (recherche autour d un touche).

	@return {Object|null} Coordonnees de la case ou null
*/
function iaChoisirCasePoncage() {
	if (iaDirectionActuelle === 0) {
		return iaChoisirDirectionInitiale();
	} else {
		return iaContinuerDansDirection();
	}
}

/**
	Choisit la premiere direction a explorer apres un touche.

	@return {Object|null} Coordonnees de la case ou null
*/
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

/**
	Continue a tirer dans la direction actuelle.

	@return {Object|null} Coordonnees de la case ou null
*/
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

/**
	Calcule la case adjacente dans une direction donnee.

	@param {Object} caseOrigine Case de depart
	@param {number} direction Direction (1=haut, 2=bas, 3=gauche, 4=droite)
	@return {Object} Coordonnees de la case adjacente
*/
function iaCalculerCaseAdjacente(caseOrigine, direction) {
	let nouvLigne = caseOrigine.ligne;
	let nouvColonne = caseOrigine.colonne;
	if (direction === 1) nouvLigne--;
	else if (direction === 2) nouvLigne++;
	else if (direction === 3) nouvColonne--;
	else if (direction === 4) nouvColonne++;
	return { ligne: nouvLigne, colonne: nouvColonne };
}

/**
	Verifie si une case est valide et libre.

	@param {Object} caseTest Case a verifier
	@return {boolean} True si case valide et libre
*/
function iaCaseValideEtLibre(caseTest) {
	if (!coordonneesValides(caseTest.ligne, caseTest.colonne)) return false;
	return iaCaseLibre(caseTest.ligne, caseTest.colonne);
}

/**
	Retourne la direction opposee.

	@param {number} direction Direction actuelle
	@return {number} Direction opposee
*/
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

/**
	Met a jour l etat de l IA apres un tir.

	@param {number} resultat Resultat du tir
	@param {Object} caseTiree Case tiree
*/
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

/**
	Gere un tir touche pour l IA.

	@param {Object} caseTiree Case touchee
*/
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

/**
	Execute le tour de l IA.
*/
function tourIA() {
	log("=== TOUR IA ===");
	const caseIA = iaChoisirCase();
	if (caseIA === null) {
		log("IA: plus de case disponible");
		return;
	}
	effectuerTirIA(caseIA.ligne, caseIA.colonne);
}

/**
	Effectue un tir de l IA sur la grille du joueur.

	@param {number} ligne Ligne visee
	@param {number} colonne Colonne visee
*/
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

/**
	Affiche le message correspondant au resultat du tir IA.

	@param {number} resultat Resultat du tir
*/
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

/**
	Verifie si un bateau du joueur est coule.

	@param {number} ligne Ligne du tir
	@param {number} colonne Colonne du tir
	@return {boolean} True si un bateau est coule
*/
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

/**
	Verifie si un bateau adverse est coule.

	@param {number} ligne Ligne du tir
	@param {number} colonne Colonne du tir
	@return {boolean} True si un bateau est coule
*/
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

/**
	Verifie si un bateau contient une case donnee.

	@param {Object} bateau Bateau a verifier
	@param {number} ligne Ligne de la case
	@param {number} colonne Colonne de la case
	@return {boolean} True si le bateau contient la case
*/
function bateauContientCase(bateau, ligne, colonne) {
	for (let cIdx = 0; cIdx < bateau.cases.length; cIdx++) {
		if (bateau.cases[cIdx].ligne === ligne && bateau.cases[cIdx].colonne === colonne) {
			return true;
		}
	}
	return false;
}

/**
	Verifie si toutes les cases d un bateau sont touchees.

	@param {Object} bateau Bateau a verifier
	@param {Array} grille Grille contenant le bateau
	@return {boolean} True si tout est touche
*/
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

/**
	Marque un bateau comme coule sur la grille.

	@param {Array} bateaux Liste des bateaux
	@param {Array} grille Grille a modifier
	@param {number} ligne Ligne du tir final
	@param {number} colonne Colonne du tir final
*/
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

/**
	Verifie si la partie est terminee.
*/
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

/**
	Affiche la modale de fin de partie.

	@param {boolean} victoire True si le joueur a gagne
*/
function afficherModaleFinPartie(victoire) {
	log("Affichage modale fin de partie - victoire: " + victoire);
	supprimerModalesExistantes();
	const overlay = creerElement("div", "modale-overlay");
	const modale = creerElement("div", "modale");
	const boutonFermer = creerElement("button", "modale-bouton-fermer");
	boutonFermer.textContent = "×";
	boutonFermer.addEventListener("click", fermerModaleSansRelancer);
	const titre = creerElement("h2", victoire ? "modale-titre--victoire" : "modale-titre--defaite");
	titre.textContent = victoire ? "Gagné" : "Perdu";
	const bouton = creerElement("button", victoire ? "modale-bouton--victoire" : "modale-bouton--defaite");
	bouton.textContent = "OK";
	bouton.addEventListener("click", fermerModaleEtRelancer);
	modale.appendChild(boutonFermer);
	modale.appendChild(titre);
	modale.appendChild(bouton);
	overlay.appendChild(modale);
	document.body.appendChild(overlay);
}

/**
	Ferme la modale et relance une nouvelle partie.
*/
function fermerModaleEtRelancer() {
	supprimerModalesExistantes();
	demarrerNouvellePartie();
}

/**
	Ferme la modale sans relancer de partie.
*/
function fermerModaleSansRelancer() {
	supprimerModalesExistantes();
}

/**
	Supprime toutes les modales existantes du DOM.
*/
function supprimerModalesExistantes() {
	const modales = document.querySelectorAll(".modale-overlay");
	modales.forEach(function(modale) {
		modale.remove();
	});
}

/**
	Verifie si tous les bateaux sont coules.

	@param {Array} bateaux Liste des bateaux
	@return {boolean} True si tous coules
*/
function tousCoules(bateaux) {
	for (let idx = 0; idx < bateaux.length; idx++) {
		if (!bateaux[idx].coule) return false;
	}
	return bateaux.length > 0;
}

// ==============================================================================
// Fonctions principales - Logique de jeu
// ==============================================================================

/**
	Gere le clic sur une case de grille.

	@param {Event} evenement Evenement click
*/
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

/**
	Effectue un tir du joueur sur la grille adverse.

	@param {number} ligne Ligne visee
	@param {number} colonne Colonne visee
*/
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

/**
	Calcule le resultat d un tir sur la grille adverse.

	@param {number} ligne Ligne du tir
	@param {number} colonne Colonne du tir
	@return {number} Resultat du tir (ETAT_EAU, ETAT_TOUCHE, ETAT_COULE)
*/
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

/**
	Affiche le message correspondant au resultat du tir.

	@param {number} resultat Resultat du tir
*/
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

/**
	Demarre une nouvelle partie.
*/
function demarrerNouvellePartie() {
	log("=== NOUVELLE PARTIE ===");
	mettreAJourPlacementAutomatique();
	grilleJoueur = initialiserGrilleVide();
	grilleAdversaire = initialiserGrilleVide();
	bateauxJoueur = [];
	bateauxAdversaire = [];
	initialiserBateauxAplacer();
	cacherPanneauBateauxRestants();
	rendreGrille("grille-joueur", grilleJoueur, true);
	rendreGrille("grille-adversaire", grilleAdversaire, false);
	demarrerPhasePlacement();
}

/**
	Met a jour le mode de placement selon le switch.
*/
function mettreAJourPlacementAutomatique() {
	const switchPlacement = obtenirElement("switch-placement-auto");
	placementAutomatique = switchPlacement ? switchPlacement.checked : true;
	log("Placement auto mis a jour: " + placementAutomatique);
}

/**
	Initialise tous les ecouteurs d evenements.
*/
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
	const switchPlacement = obtenirElement("switch-placement-auto");
	if (switchPlacement) {
		switchPlacement.addEventListener("change", sauvegarderPreferences);
		log("Ecouteur switch-placement-auto OK");
	}
	const inputTaille = obtenirElement("input-taille");
	if (inputTaille) {
		inputTaille.addEventListener("change", sauvegarderPreferences);
		log("Ecouteur input-taille OK");
	}
	document.addEventListener("keydown", gererToucheClavier);
	log("Ecouteur clavier OK");
}

/**
	Gere les touches clavier.

	@param {KeyboardEvent} evenement Evenement clavier
*/
function gererToucheClavier(evenement) {
	if (evenement.key === "r" || evenement.key === "R") {
		if (phasePlacement && !placementAutomatique) {
			pivoterBateau();
		}
	}
	if (evenement.key === "Enter") {
		if (modaleOuverte()) {
			fermerModaleEtRelancer();
		} else if (menuVisible()) {
			lancerPartieDepuisMenu();
		}
	}
	if (evenement.key === "Escape") {
		if (modaleOuverte()) {
			fermerModaleSansRelancer();
		}
	}
}

/**
	Verifie si une modale est ouverte.

	@return {boolean} True si modale ouverte
*/
function modaleOuverte() {
	return document.querySelector(".modale-overlay") !== null;
}

/**
	Verifie si le menu de configuration est visible.

	@return {boolean} True si menu visible
*/
function menuVisible() {
	const menuConfig = obtenirElement("menu-config");
	return menuConfig && menuConfig.style.display !== "none";
}

/**
	Lance une partie depuis le menu de configuration.
*/
function lancerPartieDepuisMenu() {
	log("=== LANCEMENT PARTIE ===");
	const inputTaille = obtenirElement("input-taille");
	const switchPlacement = obtenirElement("switch-placement-auto");
	tailleGrille = parseInt(inputTaille.value) || 10;
	if (tailleGrille < 1) tailleGrille = 1;
	if (tailleGrille > 63) tailleGrille = 63;
	placementAutomatique = switchPlacement ? switchPlacement.checked : true;
	log("Taille grille: " + tailleGrille);
	log("Placement auto: " + placementAutomatique);
	calculerTailleCase();
	afficherZoneJeu();
	demarrerNouvellePartie();
}

/**
	Affiche la zone de jeu et cache le menu.
*/
function afficherZoneJeu() {
	log("Affichage zone jeu");
	const menuConfig = obtenirElement("menu-config");
	const zoneJeu = obtenirElement("zone-jeu");
	const zoneControles = obtenirElement("zone-controles");
	if (menuConfig) menuConfig.style.display = "none";
	if (zoneJeu) zoneJeu.style.display = "flex";
	if (zoneControles) zoneControles.style.display = "flex";
}

/**
	Retourne au menu de configuration.
*/
function retourMenu() {
	log("Retour menu");
	const menuConfig = obtenirElement("menu-config");
	const zoneJeu = obtenirElement("zone-jeu");
	const zoneControles = obtenirElement("zone-controles");
	if (menuConfig) menuConfig.style.display = "flex";
	if (zoneJeu) zoneJeu.style.display = "none";
	if (zoneControles) zoneControles.style.display = "none";
	partieEnCours = false;
	focusInputTaille();
}

/**
	Charge la configuration depuis le serveur.
*/
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

/**
	Initialise l application au chargement.
*/
function initialiserApplication() {
	log("=== INIT APP ===");
	chargerConfig();
	chargerPreferences();
	initialiserEcouteurs();
	focusInputTaille();
}

/**
	Sauvegarde les preferences dans le localStorage.
*/
function sauvegarderPreferences() {
	const switchPlacement = obtenirElement("switch-placement-auto");
	const inputTaille = obtenirElement("input-taille");
	const preferences = {
		placementAutomatique: switchPlacement ? switchPlacement.checked : true,
		tailleGrille: inputTaille ? parseInt(inputTaille.value) || 10 : 10
	};
	localStorage.setItem("bataille_navale", JSON.stringify(preferences));
	log("Preferences sauvegardees - placement auto: " + preferences.placementAutomatique + ", taille: " + preferences.tailleGrille);
}

/**
	Charge les preferences depuis le localStorage.
*/
function chargerPreferences() {
	const donnees = localStorage.getItem("bataille_navale");
	if (donnees) {
		const preferences = JSON.parse(donnees);
		const switchPlacement = obtenirElement("switch-placement-auto");
		const inputTaille = obtenirElement("input-taille");
		if (switchPlacement && preferences.placementAutomatique !== undefined) {
			switchPlacement.checked = preferences.placementAutomatique;
		}
		if (inputTaille && preferences.tailleGrille !== undefined) {
			inputTaille.value = preferences.tailleGrille;
		}
		log("Preferences chargees - placement auto: " + preferences.placementAutomatique + ", taille: " + preferences.tailleGrille);
	}
}

/**
	Met le focus sur l input de taille de grille.
*/
function focusInputTaille() {
	const inputTaille = obtenirElement("input-taille");
	if (inputTaille) {
		inputTaille.focus();
		inputTaille.select();
	}
}

// ==============================================================================
// Main
// ==============================================================================

/**
	Point d entree de l application.
*/
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
