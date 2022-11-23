/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modele.plateau;

import modele.deplacements.Controle4Directions;
import modele.deplacements.ControleColonne;
import modele.deplacements.Direction;
import modele.deplacements.Gravite;
import modele.deplacements.Ordonnanceur;
import modele.deplacements.IA;

import javax.swing.*;
import java.awt.Point;
import java.util.HashMap;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Arrays;

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
    private Bot smicks[] = new Bot[10];

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

        // Fonction pour load un niveau à partir d'un fichier text
        loadLevel("Levels/00.txt");

        smicks[0] = new Bot(this);
        addEntite(smicks[0], 15,21);
        IA.getInstance().addEntiteDynamique(smicks[0]);
        ordonnanceur.add(IA.getInstance());

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
     * Permet par exemple a une entité de percevoir son environnement proche et de définir sa stratégie de déplacement
    * @param e Selected entity
    * @param d Direction in which to look
     */
    public Entite regarderDansLaDirection(Entite e, Direction d) {
        Point positionEntite = map.get(e);
        return objetALaPosition(calculerPointCible(positionEntite, d));
    }


    /**
     * Permet par exemple a une entité de percevoir sont environnement proche et de définir sa stratégie de déplacement
     * @param e Selected entity
     * @param d Direction in which to look
     * @param distance Distance from the current point
     */
    public Entite regarderDansLaDirectionDistance(Entite e, Direction d, int distance) {
        Point positionEntite = map.get(e);
        return objetALaPosition(calculerPointCibleDistance(positionEntite, d, distance));
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
                            /* Si la prochaine case est une entité qui peut être écrasée ou permet d'escalader */
                            if (next_entite.peutEtreEcrase() || next_entite.peutPermettreDeMonterDescendre()) {
                                retour = true;
                            }

                            if (e.deplacementAction(next_entite)){
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
                            /* Si la prochaine case est une entité qui peut être écrasée ou permet d'escalader */
                            if (next_entite.peutEtreEcrase() || next_entite.peutPermettreDeMonterDescendre()) {
                                retour = true;
                            }

                            if (e.deplacementAction(next_entite)){
                                retour = true;
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
     * Function who return the point in function of the Direction
     * @param pCourant Current point
     * @param d Direction in which to calculate
     * @return the point in function of the Direction
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
     * Function who return the point in function of the Direction
     * @param pCourant Current point
     * @param d Direction in which to calculate
     * @param distance Distance from the current point
     * @return the point in function of the Direction
     */
    private Point calculerPointCibleDistance(Point pCourant, Direction d, int distance) {
        Point pCible = null;

        switch(d) {
            case haut: pCible = new Point(pCourant.x, pCourant.y - 1 - distance); break;
            case bas : pCible = new Point(pCourant.x, pCourant.y + 1  + distance); break;
            case gauche : pCible = new Point(pCourant.x - 1 - distance, pCourant.y); break;
            case droite : pCible = new Point(pCourant.x + 1 + distance, pCourant.y); break;

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
            if (!ancienne_entite.getClass().getName().equals("modele.plateau.Bombe")) {
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


    public void loadLevel(String fileName){

        char[][] array = new char[1][];

        //reads all lines of the level file  
        File file = new File(fileName);

        // open the file 
        try (FileReader fr = new FileReader(file)) {
            BufferedReader br = new BufferedReader(fr);
            
            // Get the size of the board
            String[] size = br.readLine().split(" ");
            array = new char[Integer.parseInt(size[0])][Integer.parseInt(size[1])];
            
            // Get the map into char 2D Array 
            String line;
            int i = 0;
            while((line = br.readLine()) != null){
                //process the line
                array[i] = line.toCharArray();
                i++;
            }
        } catch (NumberFormatException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        // Create each entite for the corresponding char in the array we got from the txt file
        for(int row = 0; row < array.length; row++){
            for (int col = 0; col < array[row].length; col++) {

                switch(array[row][col]) {
                    case 'H':
                        hector = new Heros(this);
                        addEntite(hector, col, row);
                
                        Gravite g = new Gravite();
                        g.addEntiteDynamique(hector);
                        ordonnanceur.add(g);
                
                        Controle4Directions.getInstance().addEntiteDynamique(hector);
                        ordonnanceur.add(Controle4Directions.getInstance());
                    case 'P':
                        addEntite(new Platform(this), col, row);
                        break;
                    case 'M':
                        addEntite(new Mur(this), col, row);
                        break;
                    case 'B':
                        addEntite(new Bombe(this), col, row);
                        break;
                    case 'R':
                        addEntite(new Corde(this), col, row);
                        break;
                    case 'C':
                        Colonne tmp_colonne = new Colonne(this);
                        addEntite(tmp_colonne, col, row);
                        ControleColonne.getInstance().addEntiteDynamique(tmp_colonne);
                        ordonnanceur.add(ControleColonne.getInstance());
                        
                }
            }
        }

    }

}
