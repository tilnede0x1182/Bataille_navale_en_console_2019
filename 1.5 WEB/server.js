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

const http = require('http');
const fs = require('fs');
const path = require('path');

const PORT = 4511;

const MIME_TYPES = {
	'.html': 'text/html',
	'.css': 'text/css',
	'.js': 'application/javascript',
	'.json': 'application/json',
	'.png': 'image/png',
	'.jpg': 'image/jpeg',
	'.svg': 'image/svg+xml'
};

// ==============================================================================
// Fonctions utilitaires
// ==============================================================================

/**
	Retourne le type MIME d'un fichier selon son extension.

	@param cheminFichier Chemin du fichier
	@return Type MIME ou 'application/octet-stream' par defaut
*/
function obtenirTypeMime(cheminFichier) {
	const extension = path.extname(cheminFichier).toLowerCase();
	return MIME_TYPES[extension] || 'application/octet-stream';
}

/**
	Envoie une reponse d'erreur au client.

	@param reponse Objet reponse HTTP
	@param codeStatus Code HTTP (404, 500, etc.)
	@param message Message d'erreur
*/
function envoyerErreur(reponse, codeStatus, message) {
	reponse.writeHead(codeStatus, { 'Content-Type': 'text/plain; charset=utf-8' });
	reponse.end(message);
}

/**
	Envoie un fichier au client.

	@param reponse Objet reponse HTTP
	@param cheminFichier Chemin du fichier a envoyer
*/
function envoyerFichier(reponse, cheminFichier) {
	const typeMime = obtenirTypeMime(cheminFichier);
	fs.readFile(cheminFichier, (erreur, contenu) => {
		if (erreur) {
			envoyerErreur(reponse, 404, 'Fichier non trouve');
			return;
		}
		reponse.writeHead(200, { 'Content-Type': typeMime });
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
	let cheminRelatif = urlRequete === '/' ? '/index.html' : urlRequete;
	return path.join(__dirname, 'assets', cheminRelatif);
}

/**
	Gere une requete HTTP entrante.

	@param requete Objet requete HTTP
	@param reponse Objet reponse HTTP
*/
function gererRequete(requete, reponse) {
	const cheminFichier = resoudreCheminFichier(requete.url);
	envoyerFichier(reponse, cheminFichier);
}

/**
	Demarre le serveur HTTP.
*/
function demarrerServeur() {
	const serveur = http.createServer(gererRequete);
	serveur.on('error', gererErreurServeur);
	serveur.listen(PORT, afficherMessageDemarrage);
}

/**
	Gere les erreurs du serveur (port occupe, etc.).

	@param erreur Objet erreur
*/
function gererErreurServeur(erreur) {
	if (erreur.code === 'EADDRINUSE') {
		console.error(`❌ Le port ${PORT} est deja occupe.`);
		process.exit(1);
	}
	console.error('Erreur serveur:', erreur);
	process.exit(1);
}

/**
	Affiche le message de demarrage du serveur.
*/
function afficherMessageDemarrage() {
	console.log(`🚀 Serveur lance sur le port ${PORT}`);
	console.log(`   http://localhost:${PORT}`);
}

// ==============================================================================
// Main
// ==============================================================================

/**
	Point d'entree du programme.
*/
function main() {
	demarrerServeur();
}

// ==============================================================================
// Lancement du programme
// ==============================================================================

main();
