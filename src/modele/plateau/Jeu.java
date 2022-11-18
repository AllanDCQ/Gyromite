/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modele.plateau;

import modele.deplacements.Controle4Directions;
import modele.deplacements.Direction;
import modele.deplacements.Gravite;
import modele.deplacements.Ordonnanceur;

import javax.swing.*;
import java.awt.Point;
import java.util.HashMap;

import java.util.Scanner;

/** Actuellement, cette classe gère les postions
 * (ajouter conditions de victoire, chargement du plateau, etc.)
 */
public class Jeu {

    public static final int SIZE_X = 25;
    public static final int SIZE_Y = 25;

    // compteur de déplacements horizontal et vertical (1 max par défaut, à chaque pas de temps)
    private HashMap<Entite, Integer> cmptDeplH = new HashMap<Entite, Integer>();
    private HashMap<Entite, Integer> cmptDeplV = new HashMap<Entite, Integer>();

    private Heros hector;

    private HashMap<Entite, Point> map = new  HashMap<Entite, Point>(); // permet de récupérer la position d'une entité à partir de sa référence
    private Entite[][] grilleEntites = new Entite[SIZE_X][SIZE_Y]; // permet de récupérer une entité à partir de ses coordonnées

    private Ordonnanceur ordonnanceur = new Ordonnanceur(this);


    /** Time Variables */
    private int TimeLeft; // Variable of secondes left
    private int Slepp_ms; // time slepp in ms between each loop
    private int Time_wait = 0; //
    private int Score;

    public Entite ancienne_entite = null;


    public Jeu() {

        initialisationDesEntites();
        TimeLeft = 1000;
        Score = 0;

    }

    public void resetCmptDepl() {
        cmptDeplH.clear();
        cmptDeplV.clear();
    }

    public void start(long _pause) {
        Slepp_ms = (int)_pause;
        ordonnanceur.start(_pause);
    }
    
    public Entite[][] getGrille() {
        return grilleEntites;
    }
    
    public Heros getHector() {
        return hector;
    }

    private void initialisationDesEntites() {
        hector = new Heros(this);
        addEntite(hector, 2, 2);


        Gravite g = new Gravite();
        g.addEntiteDynamique(hector);
        ordonnanceur.add(g);

        Controle4Directions.getInstance().addEntiteDynamique(hector);
        ordonnanceur.add(Controle4Directions.getInstance());

        // murs extérieurs horizontaux
        for (int x = 0; x < SIZE_X; x++) {
            addEntite(new Mur(this), x, SIZE_Y-1);
        }

        addEntite(new Platform(this), 2, 4);
        addEntite(new Platform(this), 2, 6);
        addEntite(new Platform(this), 3, 6);
        addEntite(new Corde(this), 4, 5);
        addEntite(new Corde(this), 4, 6);
        addEntite(new Corde(this), 4, 7);
        addEntite(new Corde(this), 4, 8);
        addEntite(new Corde(this), 4, 9);
        addEntite(new Corde(this), 4, 10);

        addEntite(new Platform(this), 4, 10);
        addEntite(new Platform(this), 5, 10);
        addEntite(new Bombe(this),10,23);


        /** J'ai documenté ton code parce qu'il crée une erreur à la ligne 97 , je pense que tu ne l'avais pas fini.

        Scanner level = new Scanner("../../Levels/00.txt"); //whatever file is being read

        for (int i=0; i<30; i++)
        {
            for (int j=0; j<30; j++)
            {
                char nextTile = level.next().charAt(0); //reads the next char
                switch (nextTile){
<<<<<<< HEAD
                    case 'M': {
                        addEntite(new Mur(this), i, j);
                        break;
                    }
                    case 'P': {
                        addEntite(new Platform(this), i, j);
                        break;
                    }
                    case 'C': {
                        addEntite(new Corde(this), i, j);
                        break;
                    }
=======

>>>>>>> 208d313bb0cbaf19dd3067e95361691a0ba7f3bd
                }
            }
        }

        level.close(); // close the file

         **/
    }

    private void addEntite(Entite e, int x, int y) {
        grilleEntites[x][y] = e;
        map.put(e, new Point(x, y));
    }
    
    /** Permet par exemple a une entité de percevoir sont environnement proche et de définir sa stratégie de déplacement
     *
     */
    public Entite regarderDansLaDirection(Entite e, Direction d) {
        Point positionEntite = map.get(e);
        return objetALaPosition(calculerPointCible(positionEntite, d));
    }
    
    /** Si le déplacement de l'entité est autorisé (pas de mur ou autre entité), il est réalisé
     * Sinon, rien n'est fait.
     */
    public boolean deplacerEntite(Entite e, Direction d) {
            boolean retour = false;

            Point pCourant = map.get(e);

            Point pCible = calculerPointCible(pCourant, d);

            Entite next_entite = objetALaPosition(pCible);


            if (contenuDansGrille(pCible)) {
                switch (d) {
                    case bas:
                    case haut:
                        if (cmptDeplV.get(e) == null) {
                            if (next_entite == null) {
                                retour = true;
                            } else {
                                if (next_entite.peutEtreEcrase()) {
                                    retour = true;
                                    if (next_entite.get_class_string().equals("Bombe")) {
                                        Score += 100;
                                    }
                                }
                            }
                            cmptDeplV.put(e, 1);
                        } else {
                            if (next_entite != null) {
                                if (next_entite.peutPermettreDeMonterDescendre()) {
                                    System.out.println("peutPermettreDeMonterDescendre = true");
                                    retour = true;
                                    cmptDeplV.put(e, 1);
                                }
                            }
                        }
                        break;
                    case gauche:
                    case droite:
                        if (cmptDeplH.get(e) == null) {
                            if (next_entite == null) {
                                retour = true;
                            } else {
                                if (next_entite.peutEtreEcrase()) {
                                    retour = true;
                                    if (next_entite.get_class_string().equals("Bombe")) {
                                        Score += 100;
                                    }
                                }
                            }
                            cmptDeplH.put(e, 1);
                        }
                        break;

                }
            }




            if (retour) {
                deplacerEntite(pCourant, pCible, e);
            }

            return retour;
    }

    
    private Point calculerPointCible(Point pCourant, Direction d) {
        Point pCible = null;
        
        switch(d) {
            case haut: pCible = new Point(pCourant.x, pCourant.y - 1); break;
            case bas : pCible = new Point(pCourant.x, pCourant.y + 1); break;
            case gauche : pCible = new Point(pCourant.x - 1, pCourant.y); break;
            case droite : pCible = new Point(pCourant.x + 1, pCourant.y); break;     
            
        }
        
        return pCible;
    }
    
    private void deplacerEntite(Point pCourant, Point pCible, Entite e) {

        if (ancienne_entite == null) {
            grilleEntites[pCourant.x][pCourant.y] = null;
        } else {
            if (!ancienne_entite.get_class_string().equals("Bombe")) {
                grilleEntites[pCourant.x][pCourant.y] = ancienne_entite;
            } else {
                grilleEntites[pCourant.x][pCourant.y] = null;
            }
        }

        ancienne_entite = objetALaPosition(pCible);

        grilleEntites[pCible.x][pCible.y] = e;
        map.put(e, pCible);

    }
    
    /** Indique si p est contenu dans la grille
     */
    private boolean contenuDansGrille(Point p) {
        return p.x >= 0 && p.x < SIZE_X && p.y >= 0 && p.y < SIZE_Y;
    }
    
    private Entite objetALaPosition(Point p) {
        Entite retour = null;
        
        if (contenuDansGrille(p)) {
            retour = grilleEntites[p.x][p.y];
        }
        
        return retour;
    }

    public Ordonnanceur getOrdonnanceur() {
        return ordonnanceur;
    }


    /**
     * Get the score
     * @return the player's score
     */
    public int GetScore() { return Score; }

    /**
     * Get the time
     * @return the player's time left in secondes
     */
    public int GetTimeLeft() { return TimeLeft; }

    /**
     * Increase the variable of the time (TimeLeft) each seconde
     */
    public void increase_TimeLeft() {
        Time_wait += 1;

        if (Time_wait >= 1000/Slepp_ms) {
            TimeLeft = TimeLeft - 1;
            Time_wait = 0;
        }
    }



}
