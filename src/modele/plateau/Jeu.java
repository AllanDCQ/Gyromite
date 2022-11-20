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


    /* Time Variables */
        /* Variable of secondes left */
        private int TimeLeft;
        /* Variable of time sleep in ms between each loop */
        private int Slepp_ms;
        /* Variable used to calculate the loop */
        private int Time_wait = 0; //

    /* Hero's score */
    private int Score;

    /* Stock the old entity on which the hero is */
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

    }

    /**
     * Create a new entity with given coordinates
     * @param e Class of the new entity
     * @param x pos x of the new entity
     * @param y pos y of the new entity
     */
    private void addEntite(Entite e, int x, int y) {
        grilleEntites[x][y] = e;
        map.put(e, new Point(x, y));
    }


    /**
     * Permet par exemple a une entité de percevoir sont environnement proche et de définir sa stratégie de déplacement
    * @param e Selected entity
    * @param d Direction in which to look
     */
    public Entite regarderDansLaDirection(Entite e, Direction d) {
        Point positionEntite = map.get(e);
        return objetALaPosition(calculerPointCible(positionEntite, d));
    }


    /**
     * Make the move if the entity is allowed
     * @param e Selected entity
     * @param d Direction in which to move
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
                    /* S'il n'a pas déjà avancé ce tour */
                    if (cmptDeplV.get(e) == null) {
                        /* Si la prochaine case est vide */
                        if (next_entite == null) {
                            retour = true;
                        }
                        /* Si la prochaine case n'est pas vide */
                        else {
                            /* Si la prochaine case est une entité qui peut être écrasée */
                            if (next_entite.peutEtreEcrase()) {
                                retour = true;
                                /* Si la prochaine case est une bombe */
                                if (next_entite.get_class_string().equals("Bombe")) {
                                    increase_Score();
                                }
                            }

                            /* Si la prochaine case est une entité qui peut permettre de descendre */
                            if (next_entite.peutPermettreDeMonterDescendre()) {
                                retour = true;
                            }
                        }

                        cmptDeplV.put(e, 1);
                    }
                    break;
                case gauche:
                case droite:
                    if (cmptDeplH.get(e) == null) {
                        /* Si la prochaine case est vide */
                        if (next_entite == null) {
                            retour = true;
                        }
                        /* Si la prochaine case est une entité */
                        else {
                            /* Si la prochaine case est une entité qui peut être écrasée */
                            if (next_entite.peutEtreEcrase()) {
                                retour = true;
                                /* Si la prochaine case est une bombe */
                                if (next_entite.get_class_string().equals("Bombe")) {
                                    increase_Score();
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


    /**
     * Function who return the next in function of the Direction
     * @param pCourant Current point
     * @param d Direction in which to calculate
     * @return the next point in function of the Direction
     */
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


    /**
     * Move an entity from a current point to a target point. Replaces in the old point the entity(ancienne_entite)
     * that was before except for the bomb
     * @param pCourant Current point
     * @param pCible Point where to move
     * @param e Entity to be moved
     */
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


    private boolean contenuDansGrille(Point p) {
        return p.x >= 0 && p.x < SIZE_X && p.y >= 0 && p.y < SIZE_Y;
    }


    /**
     * Function that returns the entity at the given point
     * @param p the point to be analyzed
     * @return The entity at the given point
     */
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
    public int GetScore() {
        return Score;
    }


    /**
     * Increase 100 the Score variable
     */
    public void increase_Score() {
        Score += 100;
    }


    /**
     * Get the time
     * @return the player's time left in secondes
     */
    public int GetTimeLeft() {
        return TimeLeft;
    }


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
