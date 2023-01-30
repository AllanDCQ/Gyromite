# GYROMITE

## Liste des fonctionnalité et extensions :

### Fonctionnalité de base :

- Création du hero
- Capacité à se déplacer dans les 4 directions
- Gravité

### Fonctionnalité ajoutés :

- [Création de plusieurs niveaux](#1)
- [Création des colonnes + capacité de se déplacer avec](#2)
- [Règles du jeu (mort. fin du niveau ...)](#3)
- [Système de points](#4)
- [Meilleurs scores + Sauvegarde du score réalisé de chaque niveau dans la session courante](#5)
- [Gestion des fenêtres](#6)
- [Création des smicks et de l'IA](#7)
- [Création des bombes](#8)
- [Création des sprites](#9)
- [Création des radis](#10)
- [Création de la musique](#11)
- [Capacité à monter et descendre sur une corde](#12)



<h3 id="1">1. Création de plusieurs niveaux</h3>

Chaque niveau est représenté grâce à un fichier csv.

Schéma du fichier: $A  \mid  x \mid  y \mid n \quad (\operatorname{si} A=C)$

Ou pour un char A l'objet correspondant va être créé à la position $(x,y)$.

Pour un char C qui correspond à une colonne on a une valeur en plus n qui va venir indiquer la taille de la colonne (petite nuance entre la taille d'une colonne et combien elle pourra se déplacer: taille-1)

Un switch sur A sera fait pour savoir quelle type d'entité créée. 

<h3 id="2">2. Création des colonnes + capacité de se déplacer avec</h3>

Colonne une class qui hérite d'EntitésDynamique. (etant donné que c'est une entité qui se deplace)

3 attributs: une taille:entier, une nombre restant de déplacement possible: entier, une speed: entier. Noter que la taille est un des paramètres à passer dans le constructeur. Une colonne ne peut ni être écrasée ni permettre de monter et descendre. En revanche, celle-ci sert de support puisqu'elle bloque bien la gravité. Pour déplacer une colonne une nouvelle fonction (deplacercolonne ()) que je développerai plus tard a été implémenté dans jeu va être appelé à chaque appelle de avancerDirectionchoisie () puisque le déplacement d'une colonne n'est pas identique à n'importe quelle autre EntitéDynamique.

Pour traiter le déplacement de chaque colonne dans la grille de jeu, à la création d'une colonne on se doit de l'ajouter à l'instance de controlecolonne.

Controlecolonne une autre class héritant de RealisateurDeDeplacement, possède en plus de lstentitesDynamiques la memelistmais inverse lstentitesDynamiques Inverse pour pouvoir traiter les colonne mais dans l'ordre non pas du haut vers le bas mais plutôt du bas vers le haut. Cette nouvelle Array sera utile pour distinguer l'ordre dans lequel les colonnes bouge dépendamment de la direction. Le reste du code de controlecolonne est inspiré de Controle4Directions.

deplacercolonne () est la fonction qui prend en charge tout déplacement de colonne. L'algorithme qu'elle implémente et le suivant:

Pour une direction gauche ou droite: scénario impossible on ne fait rien Pour une direction haut:

- Si l'entité sur le chemin de la colonne est nul on bouge la colonne vers le haut
- Si l'entité sur le chemin de la colonne non-nul:
	- Si l'entité peut être écrasée, on vérifie si l'entité peut être placée au-dessus de la colonne. Si oui on déplace celle-ci suivit de la colonne, sinon on écrase l'entité et bouge la colonne.
- Entité sur le chemin ne peut pas être écrasé: scénario impossible (Incohérence avec la création du niveau)

<h3 id="3">3. Règles du jeu (mort, fin de niveau...)</h3> 

Une fonction checkEnd () a été créé dans jeu, et est appelé à chaque appelle d'update dans VueControlleurgyromite. Cette fonction permet à la fois de vérifier si:
- Toutes les bombes on était récolté (6 au total pour tous les niveaux)
- Hector est mort ( quand la fonction écrasé à partire de Hector est appelé le valeur de jeu.herodead passe à true)
- Le temps s'est écoulé ( TimeLeft = 0 ).  

Si l'une de ces conditions est vérifiée elle enclenchera l'arrêt du jeu et appelle j eu . reset (), la fonction mettreAjouraffichageMenuPrincipal () et enfin remet l'affichage du menu principal a visible menuPrincipal. setvisible (true) .

jeu.reset() et une façon de réinitialiser le jeu, ou plutôt d'effacer le niveau qui est achevé. La fin d'une partie est marquée donc par l'arrêt du jeu en cours, suivis de l'affichage du menu principal qui va rester à l'écoute de tout autre interaction pour lancer un nouveau niveau.

<h3 id="4">4. Système de points </h3>

Le systèmes de point est pas très compliqué, calculer par une formule simple:

Score = temps restant + nombre de bombes récoltes * 100

Quand 6 bombes sont récolté le joueur a "gagné" et obtient un score. Donc vous I'avez compris, le but est de récolter les 6 bombes le plus rapidement possible. Moins le temps s'écoule, plus votre score est élevé.

<h3 id="5">5. Meilleurs scores + Sauvegarde du score réalisé de chaque niveau dans la session courante </h3>

Le meilleurs scores est stocker dans data/ pour chaque niveaux dans un fichier .txt.

Quand une partie se finit avec les 6 bombe récolter le score actuelle est sauvegardée dans la variable last_score on utilisera cette variable pour tout d'abord voir si l'on doit remplacer le high score dans le fichier texte par le score actuelle, mais aussi pour afficher sur le menu principal le score effectuer même si il ne bat pas le meilleur score.

<h3 id="6">6. Gestion des fenêtres </h3>

2 JFrame existe dans notre programme. VueControl leur puisqu' elle hérite de JFrame, mais aussi menuprincipale. Au début du programme quand on rend visible VueControl leur on a décidé de override en affichant à la place le menupri n c ipa le.

Puis suivant cette logique affiche les fenêtre:

menuprincipal -> click par l'utilisateur sur un de bouton de niveau -> load le bon niveau dans jeu affiche le jeu, et arrête d'afficher le menu principal.

A la fin d'un jeu, le jeu arrête de se mettre à jour, et le menu principal est affiché avec une mise à jour s'il faut du high score est une mise à jour du dernier score du niveau fini.

![](https://cdn.mathpix.com/cropped/2023_01_26_55fe582d349c3855a0e5g-04.jpg?height=1066&width=1468&top_left_y=318&top_left_x=244)

menu principal

![](https://cdn.mathpix.com/cropped/2023_01_26_55fe582d349c3855a0e5g-04.jpg?height=1059&width=1456&top_left_y=1515&top_left_x=243)

interface de jeu 

<h3 id="7">7. Création des smicks et de l'IA </h3>

Pour la création des smicks, j'ai créé une classe Bot qui hérite de En i te Dynami que.

Pour la création de la classe IA, j'ai repris le code de Dep lacement4 D, cette classe hérite de Realisateurdedeplacement.

Pour la fonction reali iserdeplacement () , je cherche dans un premier temps à savoir si le bot est distrait à cause d'un radis (voir section 9). Puis s'il n'est pas distrait je récupère son ancienne direction.

1. Je crée une liste de directions (IstentitesArroundDirection) de direction : Devant; Derrière ; Haut; Bas

2. Je crée une seconde liste d'entités (IstEntitesArround) qui va stocker toutes les entités autour du smick grâce à la première liste.

3. Je crée une 3e liste qui cette fois-ci regardera devant le smicks (IstEntitesCibles) jusqu'à la distanceVision grâce à une nouvelle fonction qui est capable de regarder à plusieurs entités en fonction de la direction (regarderDansLaDirectionDistance)

4. Ensuite je recherche si Hector n'est pas dans mes listes lstentitesArround et Istentitescibles et si il est repérer j'avance en sa direction et la fonction realiserdeplacement () s'arrête là, si il ne peut pas se déplacer à cause du vide par exemple, il va rester où il est et attendre Hector le temps qu'il le voit.. Sinon je rentre dans la fonction deplacementiA ()

Pour la fonction deplacementIA () :

1. Je vais dans un premier temps créer 2 listes qui stockent les entités ce qui signifie l'entité sur laquelle le smick sera après son déplacement avant ou arrière.

(entiteBasDevantDerriere) et (entiteBasDevantDerriere)

2. Je crée ensuite une liste de priorité de direction. $\mathrm{s}$

a. Si le smick etait en train de monter, il va chercher avancer sinon monter sinon reculer sinon redescendre

b. Si le smick etait en train de descendre, il va chercher avancer sinon descendre sinon reculer sinon monter

c. Sinon le smick va avancer sinon reculer sinon monter sinon descendre

Tous les déplacements sont testés. Pour la montée et descente la prochaine entitée doit servir à monter et descendre. Sinon pour avancer et reculer la futur support de l'entité doit pouvoir être servi comme support.

La fonction deplacerEntite () à elle même été modifié et perfectionné pour empecher aux entités de se déplacer 2 fois en même temps ou d'activer la fonction ecrase () .

<h3 id="8">8. Création des bombes </h3>

Pour les bombes, j'ai créé une classe Bombe qui hérite d'En ti testati que qui elle peut être écraser. Si elle est écrasée, la fonction commune à toute les entités ecrase () se déclenche. Et si c'est le héros qui l'écrase, elle se supprime et le score augmente. Elle disparaîtra visuellement à la prochaine actualisation car elle sera remplacer par : nul 1. 

<h3 id="9">9. Création des sprites </h3>

Pour créer des animations, j'ai créer 1 classe mère sprites Enti te et 2 classes filles:

Spritesentitedynamique et Spritesentite

1. SpritesEntite dispose d'une liste itérable d'Image I con commune à tous : attente

2. Spritesentitedynamique dispose de 4 listes itérables, marche_gauche, marche_droite, monter, tomber

3. SpritesBomb dispose lui d'une liste itérable explose qui n'est malheureusement pas utilisé

Les listes d' images sont initialisées à la création de la classe (dans son constructeur). Lorsque l'on souhaite récupérer un sprite, on appelle la fonction get_sprite () qui prend en paramètre la direction choisie. Avant de retourner l'Image I con la fonction reset It () est appelée, elle permet de replacer l'index au début s'il n'a plus de suivant et donc recommencer le sprite.

D'un point de vue MVC, je ne pouvais pas relier un sprite à une entitée directement. La solution que j'ai trouvé c'est de créer plusieurs autres listes itérable de spr i tes En t i te qui stock pour chaque entité un Sprite un SpritesEntite. qui lui sera rattaché par son index: sprite_bot et sprite_bomb, (sprite_hero lui est seul car il y a qu'un héros mais si on veut rajouter plusieurs héros, il faudra simplement la aussi créer une liste itérable)

A chaque fois qu'une entité est trouvée dans mettreAJourAf fi chageJeu () on vérifie si la liste appelée à un suivant (donc une autre entitée), sinon on replace l'index au début, ce qui arrive à chaque début de la boucle for, pour simplifier j'aurais pu tous les réinitialiser avant la boucle je n'y ai pas pensé sur le moment. Puis on appelle la fonction définie dans la classe SpritesEntite pour afficher l'icône: get_sprite()

Pour les entités dynamiques, si ils sont sur une entitée qui permet de monter ou descendre, alors on appellera la liste monter et si l'entité sous l'entité dynamique est nul I alors on appellera la liste tomber.

Si le Bot est distrait alors j'appelle la liste attente et j'affiche l'icon icosleep au dessus de lui

<h3 id="10">10. Création des radis: </h3>

La classe Radis hérite de la classe Enti testati que. Elle peut donc être écrasé avec la fonction ecrase () .

- Si l'entité qui l'écrase est un bot alors on active la fonction recupere_radis () met a true haveRadis puis supprime le radis. Ce qui "stockera" le fait que le hero à récupérer un radis.

- Si l'entité est un Bot alors l'entité est distraite. (sprite attente, ne peux plus se déplacer ...) puis le Radis est supprimé

Puis le Radis est supprimé.

Si haveRadis est true il est possible d'appuyer sur la touche E pour placer le radis à gauche et la touche $\mathrm{R}$ pour placer le radis à droite. 

<h3 id="11">11. Création de la musique: </h3>

Pour importer une musique, j'ai créé une classe M et en utilisant les classes de javax. sound. sampled j'ai créé un simple clip de la classe C l ip et en utilisant la fonction open () qui prend un paramètre un Audiosystem. getAudio onputStream (file) avec file qui représente l'adresse du fichier audio en format wav

<h3 id="12">12. Capacité à monter et descendre sur une corde: </h3>

Lorsqu'une entité dynamique se déplace sur entité qui à peut PermettreDeMonterDescendre à True. L'entrée dynamique stocke dans sa classe l'ancienne_entite qui au prochain déplacement replacera l'entité stockée à son point d'origine (x et y) et supprimera l'entité dynamique. Concernant le déplacement, une tentative de déplacement vers le haut ou le bas renverras simplement true ce qui déplacera l'entité et comme toutes les entités statiques peuvent servir de support peutservirDesupport () , la gravité ne le fera pas tomber 

<h3 id="13">13. Documentation UML: </h3>

![](https://cdn.mathpix.com/cropped/2023_01_26_55fe582d349c3855a0e5g-08.jpg?height=717&width=1889&top_left_y=390&top_left_x=129)

Diagramme de class (rempli):

![](https://cdn.mathpix.com/cropped/2023_01_26_55fe582d349c3855a0e5g-08.jpg?height=663&width=768&top_left_y=1165&top_left_x=1135)



<h3 id="14">14. Copie d'écrans: </h3>

![](https://cdn.mathpix.com/cropped/2023_01_26_55fe582d349c3855a0e5g-09.jpg?height=2088&width=1596&top_left_y=446&top_left_x=230)
![](https://cdn.mathpix.com/cropped/2023_01_26_55fe582d349c3855a0e5g-10.jpg?height=2520&width=1586&top_left_y=276&top_left_x=243)
![](https://cdn.mathpix.com/cropped/2023_01_26_55fe582d349c3855a0e5g-11.jpg?height=2648&width=1586&top_left_y=274&top_left_x=240)
