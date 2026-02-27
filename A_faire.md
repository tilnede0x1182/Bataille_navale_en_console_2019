# À faire

## 1. Compréhension rapide

-   La version `1.2 (Jeu finit)` contient le moteur complet : `Menu` gère les interactions, `Grille` définit la taille du plateau et `Joueur` orchestre les bateaux et les tirs.
-   `IA.java` permet un placement et des tirs automatiques avec choix aléatoire de cases encore vierges, utile pour les parties solo.
-   `Affichage.java` construit les cartes ascii (plateau propre + plateau d’attaque) pour chaque joueur et affiche aussi les événements (« Touché », « Coulé », etc.).
-   `Utilitaire.java` centralise la validation (format des coordonnées, conversions lettre/indice, vérification d’un gagnant).
-   Les dossiers `1` et `1.4` gardent des itérations précédentes (base et version en cours d’amélioration IA), ce qui sert de référence.
-   `script.sh` concatène toutes les sources dans `projet.txt` pour remise ou archivage.

## 2. Bugs à corriger

-   `Joueur.tenter_une_case` (ligne ~200 de `1.2/src/Joueur.java`) ajoute les cases jouées du joueur humain via `ajoute_une_case_tentee(joueur_attaque, ...)`; on alimente donc l’historique de l’adversaire au lieu de celui de l’attaquant. Il faut passer `this` comme premier argument pour conserver la trace des tirs côté joueur courant.
-   La boucle de jeu (`Jeu.java`, méthode `jeu`) incrémente `nombre_de_joueurs_ayant_perdu` chaque fois qu’elle recroise un joueur déjà perdant. À plus de deux joueurs le compteur atteint rapidement `nombre_de_joueurs-1` et termine la partie trop tôt. Il faut mémoriser quels joueurs ont déjà fait l’objet de l’incrément ou recalculer ce total à chaque tour.

## 3. DRY en priorité

-   Les dossiers `1`, `1.2` et `1.4` possèdent chacun leurs copies de `Menu`, `Joueur`, `Utilitaire`, etc. Une seule base de code (packages Java + Gradle/Maven) éviterait la divergence actuelle.
-   Toutes les lectures utilisateur recréent un `Scanner` sur `System.in` à chaque appel (`Menu.entrer_entier`, `Joueur.tenter_une_case`, etc.). Instancier un scanner unique partagé permettrait de mutualiser la validation et l’echo utilisateur.

## 4. Sécurité

-   La taille du plateau (`new Grille(hauteur, largeur)`) vient directement du menu sans borne supérieure. Un joueur peut saisir 500×500, ce qui alloue des tableaux énormes et provoque un déni de service. Il faut plafonner à une taille raisonnable et refuser les dimensions aberrantes.
-   Les scripts d’export (`script.sh`) lisent/concatènent tout le dépôt sans filtrer les chemins. En cas d’exécution dans un environnement partagé, limiter la recherche à des répertoires connus évite d’exposer des fichiers sensibles.
