# Bataille Navale (jeu)

## Description

Implémentation complète du jeu de bataille navale, développée initialement en Java pour terminal puis portée vers une version web moderne. Le projet propose une expérience de jeu contre une intelligence artificielle procédurale, avec placement manuel ou automatique des bateaux. Deux versions coexistent : une version console fidèle au projet universitaire de 2019, et une version web responsive au design Frutiger Aero.

## Versions disponibles

- **1 - En console/** : Version Java originale (2019), jouable en terminal
- **2 WEB/** : Version web moderne (HTML/CSS/JavaScript)

---

## Version Console (Java)

### Description du projet
- Jeu de bataille navale console en Java orienté objet.
- Menu interactif pour sélectionner le nombre de joueurs et la taille du plateau.
- Placement des bateaux manuel pour l'humain ou automatique aléatoire pour l'IA.
- Tour par tour, chaque joueur attaque une case adverse avec retour « Touché », « Coulé » ou « À l'eau ».
- Affichage simultané de la carte d'attaque et du plateau propre.
- IA basique choisissant des coups aléatoires parmi les cases non attaquées.
- Détection automatique de la destruction complète d'un bateau et fin de partie.
- Architecture modulaire séparant interface (`Affichage`, `Menu`), logique de jeu (`Jeu`, `Utilitaire`) et IA.
- Méthodes utilitaires pour validation de format, conversion de coordonnées et gestion de tableaux de cases.
- Prise en compte des évolutions futures : tests unitaires, stratégie IA améliorée, build automatisé.

### Règles du jeu
- Chaque joueur dispose d'une grille et place en secret ses bateaux selon leur taille.
- À chaque tour, un joueur choisit une case à attaquer et l'adversaire annonce « Touché », « Coulé » ou « À l'eau ».
- La partie se termine lorsqu'un joueur a perdu tous ses bateaux, et l'adversaire est déclaré vainqueur.

### Technologies utilisées
- Java SE 11
- Compilation via `javac` et scripts BAT pour lancement
- Aucun framework ni gestionnaire de dépendances externe
- Structure de dossiers classique (`src/` pour sources, `BAT/` pour scripts)

### Fonctionnalités
- **Configuration initiale**
  - Menu principal avec 5 options (nouvelle partie, nombre de joueurs, taille plateau, instructions, quitter)
  - Saisie et validation de la taille du plateau (hauteur, largeur)
- **Placement des bateaux**
  - Manuel : l'utilisateur entre des coordonnées de début et de fin, format `A5` validé (`verifie_format_case`)
  - Automatique IA : répartition aléatoire avec espacement minimal (distance 1)
- **Boucle de jeu**
  - Alternance des tours entre joueurs
  - Saisie de la case à attaquer (format et répétition contrôlés)
  - Application de l'attaque, mise à jour de l'état de la case (`attaquee`, `coule`)
  - Affichage du résultat (`Dans l'eau`, `Touché`, `Coulé`) via `Utilitaire.afficher_evenement_coup`
- **Affichage console**
  - Grille de l'attaquant (`afficher_carte_attaque_joueur`)
  - Grille du défenseur (`afficher_carte_joueur`)
  - Légendes et alignements dynamiques selon largeur (gestion des espaces et sauts de ligne)
- **IA**
  - Placement des bateaux (`place_bateaux_IA`) calculant nombre et taille des bateaux selon dimensions
  - Choix aléatoire d'attaques dans les cases non encore testées (`verifie_case_tentee`)
- **Logique de fin de partie**
  - Vérification de la destruction totale de chaque bateau (`est_coule`)
  - Recherche du gagnant avec `recherche_gagnant`
- **Fonctions utilitaires**
  - Validation de chaînes numériques (`is_integer`)
  - Génération d'entiers aléatoires bornés (`randInt`, `randInt_trie_valeurs`)
  - Conversion coordonnées ↔ indices (`convertit_coordonnees_str`)
  - Affichage divers (`aff`, `affnn`, `aff_tab`, `aff_cases`, `aff_cases_touchee`)
- **Tests**
  - Classe de test basique pour tri d'un tableau d'entiers (`Tests/Test.java`)
  - Infrastructure prête à accueillir JUnit ou similaire

### Stratégies de jeu
- Adopter un tir en damier pour maximiser la couverture de la grille et localiser les bateaux plus rapidement
- Cibler systématiquement les cases adjacentes après avoir touché un navire pour en assurer le coulage
- Varier la dynamique de placement en éloignant certains bateaux pour désorienter l'adversaire
- Utiliser une distribution équilibrée entre zones centrales et périphériques pour éviter les clusters visibles
- Observer les zones déjà ratées pour affiner progressivement une zone de chasse optimale

---

## Version Web

### Description du projet
- Jeu de bataille navale jouable dans le navigateur avec interface graphique responsive.
- Menu de configuration pour choisir la taille de grille (1 à 63) et le mode de placement (manuel ou automatique).
- Placement manuel avec prévisualisation au survol et rotation du bateau (touche R).
- Tour par tour contre une IA procédurale utilisant une stratégie de recherche puis ponçage.
- Affichage simultané de la grille du joueur et de la grille adverse.
- Retour visuel immédiat : animations CSS pour les tirs (secousse pour touché, naufrage pour coulé).
- Modale de fin de partie avec indication victoire/défaite et relance rapide.
- Persistance des préférences utilisateur via localStorage (taille grille, mode placement).
- Mode triche optionnel (argument `--triche`) pour afficher les bateaux adverses (débogage).

### Technologies utilisées
- HTML5 / CSS3 / JavaScript (vanilla, sans framework)
- Node.js pour le serveur HTTP minimaliste
- Design Frutiger Aero : dégradés, transparences, effets glassmorphism, ombres douces
- Aucune dépendance externe (ni npm, ni bundler)

### Lancement
`node "2 WEB/server.js"` puis accéder à `http://localhost:4511`

Options CLI :
- `-h, --help` : Affiche l'aide
- `--triche` : Affiche les bateaux adverses (mode débogage)

### Fonctionnalités
- **Configuration initiale**
  - Panneau de configuration avec saisie de la taille de grille
  - Switch pour activer/désactiver le placement automatique des bateaux
  - Sauvegarde automatique des préférences dans le navigateur
- **Placement des bateaux**
  - Mode automatique : répartition aléatoire des bateaux selon la surface de la grille (~17% de couverture)
  - Mode manuel : survol pour prévisualiser, clic pour placer, touche R pour pivoter horizontal/vertical
  - Panneau indiquant les bateaux restants à placer (nombre et tailles)
  - Validation des positions (pas de chevauchement, pas de débordement)
- **Boucle de jeu**
  - Clic sur la grille adverse pour attaquer
  - Affichage du résultat (« Dans l'eau », « Touché », « Coulé »)
  - Tour de l'IA automatique après 800ms
  - Vérification de fin de partie après chaque tir
- **Affichage graphique**
  - Deux grilles côte à côte (votre flotte / flotte adverse)
  - Cases colorées selon l'état : vide, bateau, eau, touché, coulé
  - Taille des cases calculée dynamiquement selon la largeur d'écran
  - Animations CSS : secousse au toucher, effet de naufrage au coulé
- **IA procédurale**
  - Mode recherche : tir en damier pour maximiser la couverture
  - Mode ponçage : après un touché, exploration des 4 directions adjacentes
  - Inversion de direction si blocage (bord de grille ou eau)
  - Reset automatique après avoir coulé un bateau
- **Logique de fin de partie**
  - Détection automatique quand tous les bateaux d'un camp sont coulés
  - Modale de victoire (verte) ou défaite (rouge) avec bouton de relance
  - Fermeture par clic sur × ou touche Échap, relance par Entrée
- **Fonctions utilitaires**
  - Calcul dynamique du nombre et de la taille des bateaux selon la grille
  - Validation des coordonnées et des positions de placement
  - Création d'éléments DOM sans innerHTML (sécurité)
  - Logs console détaillés pour le débogage

### Architecture serveur
- Serveur HTTP Node.js sans dépendances (module `http` natif)
- Résolution automatique des chemins vers le dossier `assets/`
- Types MIME configurés pour HTML, CSS, JS, JSON, images
- Route `/api/config` pour transmettre la configuration (mode triche)
- Gestion des erreurs (fichier non trouvé, port occupé)
- Logs horodatés pour chaque requête
