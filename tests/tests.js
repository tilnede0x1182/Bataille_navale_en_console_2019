/**
 * Tests pour Bataille_navale_en_console_2019
 * Validation de la logique de jeu
 * Execution: node tests.js
 */

const TESTS_RESULTS = { passed: 0, failed: 0 };

/**
 * Fonction d'assertion simple.
 * @param {string} description Description du test.
 * @param {boolean} condition Condition a verifier.
 */
function assert(description, condition) {
    if (condition) {
        console.log(`[PASS] ${description}`);
        TESTS_RESULTS.passed++;
    } else {
        console.log(`[FAIL] ${description}`);
        TESTS_RESULTS.failed++;
    }
}

/**
 * Cree une grille vide.
 * @param {number} hauteur Hauteur de la grille.
 * @param {number} largeur Largeur de la grille.
 * @returns {number[][]} Grille initialisee a 0.
 */
function creerGrille(hauteur, largeur) {
    const grille = [];
    for (let ligne = 0; ligne < hauteur; ligne++) {
        grille[ligne] = [];
        for (let colonne = 0; colonne < largeur; colonne++) {
            grille[ligne][colonne] = 0;
        }
    }
    return grille;
}

/**
 * Place un bateau sur la grille.
 * @param {number[][]} grille Grille de jeu.
 * @param {number} ligne Ligne de depart.
 * @param {number} colonne Colonne de depart.
 * @param {number} taille Taille du bateau.
 * @param {boolean} estVertical Orientation.
 * @returns {boolean} True si placement reussi.
 */
function placerBateau(grille, ligne, colonne, taille, estVertical) {
    // Verification des limites
    if (estVertical) {
        if (ligne + taille > grille.length) return false;
    } else {
        if (colonne + taille > grille[0].length) return false;
    }

    // Verification collision
    for (let offset = 0; offset < taille; offset++) {
        const ligneCheck = estVertical ? ligne + offset : ligne;
        const colonneCheck = estVertical ? colonne : colonne + offset;
        if (grille[ligneCheck][colonneCheck] !== 0) return false;
    }

    // Placement
    for (let offset = 0; offset < taille; offset++) {
        const lignePlacement = estVertical ? ligne + offset : ligne;
        const colonnePlacement = estVertical ? colonne : colonne + offset;
        grille[lignePlacement][colonnePlacement] = 1;
    }
    return true;
}

/**
 * Effectue un tir sur la grille.
 * @param {number[][]} grille Grille cible.
 * @param {number} ligne Ligne du tir.
 * @param {number} colonne Colonne du tir.
 * @returns {string} Resultat: "touche", "rate", "deja_tire".
 */
function tirer(grille, ligne, colonne) {
    if (ligne < 0 || ligne >= grille.length) return "hors_limite";
    if (colonne < 0 || colonne >= grille[0].length) return "hors_limite";

    const valeur = grille[ligne][colonne];
    if (valeur === 2 || valeur === 3) return "deja_tire";
    if (valeur === 1) {
        grille[ligne][colonne] = 2; // Touche
        return "touche";
    }
    grille[ligne][colonne] = 3; // Rate
    return "rate";
}

/**
 * Verifie si tous les bateaux sont coules.
 * @param {number[][]} grille Grille a verifier.
 * @returns {boolean} True si tous les bateaux coules.
 */
function tousCoules(grille) {
    for (let ligne = 0; ligne < grille.length; ligne++) {
        for (let colonne = 0; colonne < grille[ligne].length; colonne++) {
            if (grille[ligne][colonne] === 1) return false;
        }
    }
    return true;
}

// ==================== TESTS ====================

console.log("=== Tests Bataille_navale ===\n");

// Tests creerGrille
assert("creerGrille dimensions correctes", (() => {
    const grille = creerGrille(5, 5);
    return grille.length === 5 && grille[0].length === 5;
})());

assert("creerGrille initialisee a zero", (() => {
    const grille = creerGrille(3, 3);
    return grille[1][1] === 0;
})());

// Tests placerBateau
assert("placerBateau horizontal reussi", (() => {
    const grille = creerGrille(5, 5);
    return placerBateau(grille, 0, 0, 3, false);
})());

assert("placerBateau vertical reussi", (() => {
    const grille = creerGrille(5, 5);
    return placerBateau(grille, 0, 0, 3, true);
})());

assert("placerBateau hors limites echoue", (() => {
    const grille = creerGrille(5, 5);
    return !placerBateau(grille, 0, 4, 3, false);
})());

assert("placerBateau collision echoue", (() => {
    const grille = creerGrille(5, 5);
    placerBateau(grille, 0, 0, 3, false);
    return !placerBateau(grille, 0, 1, 3, true);
})());

// Tests tirer
assert("tirer touche", (() => {
    const grille = creerGrille(5, 5);
    placerBateau(grille, 0, 0, 3, false);
    return tirer(grille, 0, 1) === "touche";
})());

assert("tirer rate", (() => {
    const grille = creerGrille(5, 5);
    return tirer(grille, 2, 2) === "rate";
})());

assert("tirer deja tire", (() => {
    const grille = creerGrille(5, 5);
    tirer(grille, 2, 2);
    return tirer(grille, 2, 2) === "deja_tire";
})());

// Tests tousCoules
assert("tousCoules faux avec bateaux", (() => {
    const grille = creerGrille(5, 5);
    placerBateau(grille, 0, 0, 2, false);
    return !tousCoules(grille);
})());

assert("tousCoules vrai apres destruction", (() => {
    const grille = creerGrille(5, 5);
    placerBateau(grille, 0, 0, 2, false);
    tirer(grille, 0, 0);
    tirer(grille, 0, 1);
    return tousCoules(grille);
})());

// ==================== RESUME ====================

console.log("\n=== Resume ===");
console.log(`Tests passes: ${TESTS_RESULTS.passed}`);
console.log(`Tests echoues: ${TESTS_RESULTS.failed}`);
console.log(`Total: ${TESTS_RESULTS.passed + TESTS_RESULTS.failed}`);

if (TESTS_RESULTS.failed > 0) {
    process.exit(1);
}
