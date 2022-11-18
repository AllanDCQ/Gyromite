# Gyromite
Projet LIFPOO

## Liste de choses à faire :

#### Généralités :
- Gestion des collisions (Prof. / Smicks, Smicks / Murs, Smicks / Smicks)
- Système de points (ALLAN)
- Règles du jeu (mort, fin du niveau...)

#### les Smicks :
- Créer les Smicks (IA)
- Améliorer la vision des Smicks (ex: vision a 5 blocs et se dirige vers le Prof)

#### Les bombes :
- Créer les bombes (ENZO)
- Capacité de ramasser les objets (Bombes, Bonus)

#### Environnement :
- Capacité de monter/descendre aux cordes lorsque le Prof. est sur une corde (ENZO)
- Capacité de faire se déplacer les Piliers + collisions Piliers (une entité écrasée par un pilier est tuée) Bonus : le Prof. doit être déplacé en même temps s’il se situe au sommet d’un Pilier (ENZO)



## Diagramme de classes :
![Diagramme de classes](/Images/DiagClasses.png)



## Au choix 2 extentions minimum parmi :
- Une extension que vous proposez vous-même ;
- Plusieurs niveaux
- Éditeur/Générateur de Niveau ;
- Meilleurs scores ;
- Capacité de ramasser et déposer des Radis ; un Radis distrait un Smick en cas de collision pendant quelques
  secondes ;
  3/4
- Niveau « scrollables » (plus grands que l’écran) ;
- Jeu collaboratif sur un même plateau ou en réseau : vous êtes libres d’avoir deux professeurs Hector, d’avoir
  un PC qui contrôle les déplacements du professeur et un autre PC qui contrôle le déplacement des colonnes,
  amusez-vous ;
- Ajouter des Colonnes avec déplacement horizontal : faites attention de bien gérer tous les déplacements et
  toutes les collisions associés ;
- Gestion de la transparence (canal alpha, dessiner le composant case), reprise des annimations (sprites
  disponibles)