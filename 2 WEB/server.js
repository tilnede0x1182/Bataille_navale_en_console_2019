// ==============================================================================
// Serveur Node.js - Bataille Navale Web
// ==============================================================================

/**
	Serveur HTTP minimaliste pour servir l'application bataille navale.
	Port configurable, detection de port occupe, pas de dependances externes.
*/

// ==============================================================================
// Donnees
// ==============================================================================

const http = require("http");
const fs = require("fs");
const path = require("path");

const PORT = 4511;
let MODE_TRICHE = false;

const MIME_TYPES = {
	".html": "text/html; charset=utf-8",
	".css": "text/css; charset=utf-8",
	".js": "application/javascript; charset=utf-8",
	".json": "application/json",
	".png": "image/png",
	".jpg": "image/jpeg",
	".svg": "image/svg+xml"
};

// ==============================================================================
// Fonctions utilitaires - Logs
// ==============================================================================

/**
	Affiche un log avec timestamp.

	@param message Message a afficher
*/
function log(message) {
	const timestamp = new Date().toISOString().substr(11, 8);
	console.log("[" + timestamp + "] " + message);
}

// ==============================================================================
// Fonctions utilitaires - HTTP
// ==============================================================================

/**
	Retourne le type MIME d un fichier selon son extension.

	@param cheminFichier Chemin du fichier
	@return Type MIME ou application/octet-stream par defaut
*/
function obtenirTypeMime(cheminFichier) {
	const extension = path.extname(cheminFichier).toLowerCase();
	return MIME_TYPES[extension] || "application/octet-stream";
}

/**
	Envoie une reponse d erreur au client.

	@param reponse Objet reponse HTTP
	@param codeStatus Code HTTP (404, 500, etc.)
	@param message Message d erreur
	@param urlRequete URL de la requete (pour log)
*/
function envoyerErreur(reponse, codeStatus, message, urlRequete) {
	log("ERR " + codeStatus + " " + urlRequete + " - " + message);
	reponse.writeHead(codeStatus, { "Content-Type": "text/plain; charset=utf-8" });
	reponse.end(message);
}

/**
	Envoie un fichier au client.

	@param reponse Objet reponse HTTP
	@param cheminFichier Chemin du fichier a envoyer
	@param urlRequete URL de la requete (pour log)
*/
function envoyerFichier(reponse, cheminFichier, urlRequete) {
	log("REQ " + urlRequete + " -> " + cheminFichier);
	const existe = fs.existsSync(cheminFichier);
	log("    Fichier existe: " + existe);
	if (!existe) {
		envoyerErreur(reponse, 404, "Fichier non trouve: " + cheminFichier, urlRequete);
		return;
	}
	const typeMime = obtenirTypeMime(cheminFichier);
	fs.readFile(cheminFichier, function(erreur, contenu) {
		if (erreur) {
			envoyerErreur(reponse, 500, "Erreur lecture: " + erreur.message, urlRequete);
			return;
		}
		log("OK  200 " + urlRequete + " (" + contenu.length + " bytes)");
		reponse.writeHead(200, { "Content-Type": typeMime });
		reponse.end(contenu);
	});
}

// ==============================================================================
// Fonctions principales
// ==============================================================================

/**
	Resout le chemin du fichier demande.

	@param urlRequete URL de la requete
	@return Chemin absolu du fichier
*/
function resoudreCheminFichier(urlRequete) {
	var cheminRelatif = urlRequete === "/" ? "/index.html" : urlRequete;
	return path.join(__dirname, "assets", cheminRelatif);
}

/**
	Gere une requete HTTP entrante.

	@param requete Objet requete HTTP
	@param reponse Objet reponse HTTP
*/
function gererRequete(requete, reponse) {
	log("--- Nouvelle requete: " + requete.method + " " + requete.url);
	if (requete.url === "/api/config") {
		envoyerConfig(reponse);
		return;
	}
	const cheminFichier = resoudreCheminFichier(requete.url);
	envoyerFichier(reponse, cheminFichier, requete.url);
}

/**
	Envoie la configuration au client (mode triche, etc.).

	@param reponse Objet reponse HTTP
*/
function envoyerConfig(reponse) {
	const config = { triche: MODE_TRICHE };
	log("API /api/config -> " + JSON.stringify(config));
	reponse.writeHead(200, { "Content-Type": "application/json" });
	reponse.end(JSON.stringify(config));
}

/**
	Demarre le serveur HTTP.
*/
function demarrerServeur() {
	const serveur = http.createServer(gererRequete);
	serveur.on("error", gererErreurServeur);
	serveur.listen(PORT, afficherMessageDemarrage);
}

/**
	Gere les erreurs du serveur (port occupe, etc.).

	@param erreur Objet erreur
*/
function gererErreurServeur(erreur) {
	if (erreur.code === "EADDRINUSE") {
		console.log("❌ Le port " + PORT + " est déjà occupé.");
		process.exit(1);
	}
	process.exit(1);
}

/**
	Affiche le message de demarrage du serveur.
*/
function afficherMessageDemarrage() {
	if (MODE_TRICHE) {
		console.log("Lancement en mode triche (pour débugage) : les bateaux ennemis seront visibles.");
	}
	console.log("🚀 Serveur lancé sur http://localhost:" + PORT);
}

// ==============================================================================
// Fonctions principales - Arguments CLI
// ==============================================================================

/**
	Affiche l aide du programme.
*/
function afficherAide() {
	console.log("Usage: node server.js [options]");
	console.log("");
	console.log("Options:");
	console.log("  -h, --help    Affiche cette aide");
	console.log("  --triche      Affiche les bateaux ennemis pour debuggage");
	console.log("");
	console.log("Exemple:");
	console.log("  node server.js --triche");
	process.exit(0);
}

/**
	Analyse les arguments de la ligne de commande.
*/
function analyserArguments() {
	const args = process.argv.slice(2);
	for (let idx = 0; idx < args.length; idx++) {
		const arg = args[idx];
		if (arg === "-h" || arg === "--help") {
			afficherAide();
		} else if (arg === "--triche") {
			MODE_TRICHE = true;
		}
	}
}

// ==============================================================================
// Main
// ==============================================================================

/**
	Point d entree du programme.
*/
function main() {
	analyserArguments();
	demarrerServeur();
}

// ==============================================================================
// Lancement du programme
// ==============================================================================

main();
