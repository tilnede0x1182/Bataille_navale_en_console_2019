# Bataille Navale en Java (Touché-Coulé)

- Ce projet implémente le jeu de la bataille navale (touché-coulé) en console.
- Il propose un affrontement entre plusieurs joueurs humains ou intelligences artificielles.
- Le jeu est basé sur une grille personnalisable et des bateaux placés manuellement ou automatiquement.
- Chaque joueur dispose de sa propre grille, et attaque les grilles adverses à tour de rôle.
- L’IA est capable de placer ses bateaux de manière stratégique et de cibler efficacement.
- Un affichage en console permet de suivre l’évolution des cartes (attaque et défense).
- Le jeu se termine quand un joueur conserve seul des bateaux non coulés.
- Les classes sont organisées pour respecter la séparation des responsabilités (grille, affichage, menu, logique de jeu, IA).
- Le projet inclut aussi des fonctions utilitaires pour le placement, l’attaque et la vérification des coups.
- Ce projet sert d'exercice structuré en Java orienté objet pour simuler un jeu complet.

## Technologies utilisées

| Technologie | Version  |
|-------------|----------|
| Java        | 11+      |
| JDK         | OpenJDK 11 ou supérieur |
| Environnement | Compatible console, OS indépendant (testé sous WSL/Windows 11) |

## Fonctionnalités complètes

- Lancement du jeu en ligne de commande (`main()` dans `Jeu.java`)
- Menu principal : choisir entre humain vs IA ou IA vs IA
- Grille personnalisable (taille configurable)
- Placement des bateaux :
  - Manuel (en saisissant les coordonnées)
  - Automatique (aléatoire ou via IA)
- Attaque de cases avec retour sur l'état : dans l’eau, touché, coulé
- Affichage console :
  - Grille d’attaque d’un joueur
  - Grille de défense d’un joueur (optionnel)
- Système d’IA pour :
  - Placement stratégique
  - Sélection des cases à attaquer
- Gestion des états de jeu :
  - Bateaux touchés ou coulés
  - Joueurs éliminés
  - Fin de partie automatique
- Visualisation des grilles en fin de partie
- Code structuré en classes (Jeu, Joueur, IA, Affichage, Bateau, Case, etc.)
