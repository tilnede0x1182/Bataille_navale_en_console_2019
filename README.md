# Bataille Navale (jeu)

## Description du projet
- Jeu de bataille navale console en Java orienté objet.
- Menu interactif pour sélectionner le nombre de joueurs et la taille du plateau.
- Placement des bateaux manuel pour l’humain ou automatique aléatoire pour l’IA.
- Tour par tour, chaque joueur attaque une case adverse avec retour « Touché », « Coulé » ou « À l’eau ».
- Affichage simultané de la carte d’attaque et du plateau propre.
- IA basique choisissant des coups aléatoires parmi les cases non attaquées.
- Détection automatique de la destruction complète d’un bateau et fin de partie.
- Architecture modulaire séparant interface (`Affichage`, `Menu`), logique de jeu (`Jeu`, `Utilitaire`) et IA.
- Méthodes utilitaires pour validation de format, conversion de coordonnées et gestion de tableaux de cases.
- Prise en compte des évolutions futures : tests unitaires, stratégie IA améliorée, build automatisé.

## Règles du jeu
- Chaque joueur dispose d’une grille et place en secret ses bateaux selon leur taille.
- À chaque tour, un joueur choisit une case à attaquer et l’adversaire annonce « Touché », « Coulé » ou « À l’eau ».
- La partie se termine lorsqu’un joueur a perdu tous ses bateaux, et l’adversaire est déclaré vainqueur.

## Technologies utilisées
- Java SE 11
- Compilation via `javac` et scripts BAT pour lancement
- Aucun framework ni gestionnaire de dépendances externe
- Structure de dossiers classique (`src/` pour sources, `BAT/` pour scripts)

## Fonctionnalités
- **Configuration initiale**
  - Menu principal avec 5 options (nouvelle partie, nombre de joueurs, taille plateau, instructions, quitter)
  - Saisie et validation de la taille du plateau (hauteur, largeur)
- **Placement des bateaux**
  - Manuel : l’utilisateur entre des coordonnées de début et de fin, format `A5` validé (`verifie_format_case`)
  - Automatique IA : répartition aléatoire avec espacement minimal (distance 1)
- **Boucle de jeu**
  - Alternance des tours entre joueurs
  - Saisie de la case à attaquer (format et répétition contrôlés)
  - Application de l’attaque, mise à jour de l’état de la case (`attaquee`, `coule`)
  - Affichage du résultat (`Dans l'eau`, `Touché`, `Coulé`) via `Utilitaire.afficher_evenement_coup`
- **Affichage console**
  - Grille de l’attaquant (`afficher_carte_attaque_joueur`)
  - Grille du défenseur (`afficher_carte_joueur`)
  - Légendes et alignements dynamiques selon largeur (gestion des espaces et sauts de ligne)
- **IA**
  - Placement des bateaux (`place_bateaux_IA`) calculant nombre et taille des bateaux selon dimensions
  - Choix aléatoire d’attaques dans les cases non encore testées (`verifie_case_tentee`)
- **Logique de fin de partie**
  - Vérification de la destruction totale de chaque bateau (`est_coule`)
  - Recherche du gagnant avec `recherche_gagnant`
- **Fonctions utilitaires**
  - Validation de chaînes numériques (`is_integer`)
  - Génération d’entiers aléatoires bornés (`randInt`, `randInt_trie_valeurs`)
  - Conversion coordonnées ↔ indices (`convertit_coordonnees_str`)
  - Affichage divers (`aff`, `affnn`, `aff_tab`, `aff_cases`, `aff_cases_touchee`)
- **Tests**
  - Classe de test basique pour tri d’un tableau d’entiers (`Tests/Test.java`)
  - Infrastructure prête à accueillir JUnit ou similaire

## Stratégies de jeu
- Adopter un tir en damier pour maximiser la couverture de la grille et localiser les bateaux plus rapidement
- Cibler systématiquement les cases adjacentes après avoir touché un navire pour en assurer le coulage
- Varier la dynamique de placement en éloignant certains bateaux pour désorienter l’adversaire
- Utiliser une distribution équilibrée entre zones centrales et périphériques pour éviter les clusters visibles
- Observer les zones déjà ratées pour affiner progressivement une zone de chasse optimale
