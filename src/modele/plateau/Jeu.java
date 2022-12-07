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
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/** Actuellement, cette classe gère les postions
 * (ajouter conditions de victoire, chargement du plateau, etc.)
 */
public class Jeu {

    public static final int SIZE_X = 24;
    public static final int SIZE_Y = 12;

    // compteur de déplacements horizontal et vertical (1 max par défaut, à chaque pas de temps)
    private HashMap<Entite, Integer> cmptDeplH = new HashMap<Entite, Integer>();
    private HashMap<Entite, Integer> cmptDeplV = new HashMap<Entite, Integer>();

    public boolean isOn = false; 
    private Heros hector;
    public boolean heroDead = false;
    private ArrayList<Bot> smicks = new ArrayList<Bot>();

    protected HashMap<Entite, Point> map = new  HashMap<Entite, Point>(); // permet de récupérer la position d'une entité à partir de sa référence
    private Entite[][] grilleEntites = new Entite[SIZE_X][SIZE_Y]; // permet de récupérer une entité à partir de ses coordonnées

    private Ordonnanceur ordonnanceur = new Ordonnanceur(this);


    /* Time Variables */
        /* Variable of secondes left */
        private int TimeLeft;
        /* Variable of time sleep in ms between each loop */
        private int Slepp_ms;
        /* Variable used to calculate the loop */
        public int Time_wait = 0; //

    /* Hero's score */
    private int Score;

    public int level = 1;


    public Jeu() {
        //initialisationDesEntites();
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

    private void resetGrille(){
        for (int index = 0; index < SIZE_X; index++) {
            for (int inner = 0; inner < SIZE_Y; inner++) {
                grilleEntites[index][inner] = null;
            }
        }
    }


    public Heros getHector() {
        return hector;
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

    public void ecraseEntite(EntiteDynamique e){
        Point position = map.get(e);
        map.remove(e);
        grilleEntites[position.x][position.y] = null;
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
                        if (next_entite == null) {retour = true;}
                        /* Si la prochaine case n'est pas vide */
                        else {
                            /* Si la prochaine case est une entité qui peut être écrasée ou permet d'escalader */
                            if (next_entite.peutEtreEcrase()){
                                ((EntiteDynamique) next_entite).ecrase(e);
                                retour = true;
                            }
                            else if (next_entite.peutPermettreDeMonterDescendre()) {retour = true;}
                        }
                        cmptDeplV.put(e, 1);
                    }
                    break;
                case gauche:
                case droite:
                    if (cmptDeplH.get(e) == null) {
                        /* Si la prochaine case est vide */
                        if (next_entite == null) {retour = true;}
                        /* Si la prochaine case est une entité */
                        else {
                            /* Si la prochaine case est une entité qui peut être écrasée ou permet d'escalader */
                            if (next_entite.peutEtreEcrase()){
                                ((EntiteDynamique) next_entite).ecrase(e);
                                retour = true;
                            }
                            else if (next_entite.peutPermettreDeMonterDescendre()) {retour = true;}

                        }
                        cmptDeplH.put(e, 1);
                    }
                    break;
            }
        }
    
        if (retour) {
            deplacerEntite(pCourant, pCible, (EntiteDynamique) e);
        }
        return retour;
    }

        /**
     * Make the move if the entity is allowed
     * @param e Selected entity
     * @param d Direction in which to move
     */
    public boolean deplacerColonne(Colonne e, Direction d) {
        boolean retour = false;
        Point pCourant = map.get(e);
        Point pCible = calculerPointCible(pCourant, d);
        Entite next_entite = objetALaPosition(pCible);

        if (contenuDansGrille(pCible)) {
            switch (d) {
                case gauche:
                case droite:
                    retour = false;
                    break;

                case bas:
                case haut:
                    /* Si la prochaine case est vide */
                    if (next_entite == null) {retour = true;}
                    /* Si la prochaine case n'est pas vide */
                    else {
                        /* Si la prochaine case est une entité qui peut être écrasée ou permet d'escalader */
                        if (next_entite.peutEtreEcrase()) {
                            if ( objetALaPosition(calculerPointCibleDistance(pCourant, d, e.taille)) == null && d == Direction.haut ){
                                deplacerEntite(map.get(next_entite), calculerPointCibleDistance(pCourant, d, e.taille), (EntiteDynamique)next_entite);
                                retour = true;
                            }
                            else {
                                ((EntiteDynamique) next_entite).ecrase(e);
                                retour = true;
                            }
                        }

                    }
                    cmptDeplV.put(e, 1);
                    break;
            }
        }

        if (retour) {
            deplacerEntite(pCourant, pCible, (EntiteDynamique) e);
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
    private void deplacerEntite(Point pCourant, Point pCible, EntiteDynamique e) {

        grilleEntites[pCourant.x][pCourant.y] = e.getAncienne_entite();

        e.setAncienne_entite(objetALaPosition(pCible));

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
    public Entite objetALaPosition(Point p) {
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

    public boolean checkEnd(){

        // TODO
        if (heroDead){
            return true;
        }
        if (GetScore() == 600){
            if (Integer.valueOf(getHighScore()) < GetTimeLeft()){
                File scoreFile = new File("data/scores/score" + String.valueOf(level) + ".txt");
                FileWriter scoreWriter;
                try {
                    scoreWriter = new FileWriter(scoreFile, false);
                    scoreWriter.write(String.valueOf(GetTimeLeft()));
                    scoreWriter.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            return true;
        }
        return false;
    }


    // reset all the game
    public void reset(){    
        System.out.println("Reset Game");
        resetGrille();
        smicks.clear();
        map.clear();
        Controle4Directions.reset();
        ControleColonne.reset();
        Gravite.reset();
        IA.reset();
        ordonnanceur.reset();
        Score = 0;
        heroDead = false;
        isOn = false;
        TimeLeft = 1000;
    }


    public void loadLevel(String fileName){

        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(fileName));
            String line = reader.readLine();
            while (line != null) {
                String[] line_info = line.split(",");
                switch(line_info[0]) {
                    case "H":
                        hector = new Heros(this);
                        addEntite(hector, Integer.parseInt(line_info[1]), Integer.parseInt(line_info[2]));
                        Gravite.getInstance().addEntiteDynamique(hector);
                        ordonnanceur.add(Gravite.getInstance());

                        Controle4Directions.getInstance().addEntiteDynamique(hector);
                        ordonnanceur.add(Controle4Directions.getInstance());
                        break;
                    case "P":
                        addEntite(new Platform(this), Integer.parseInt(line_info[1]), Integer.parseInt(line_info[2]));
                        break;
                    case "V":
                        addEntite(new PlatformV(this), Integer.parseInt(line_info[1]), Integer.parseInt(line_info[2]));
                        break;
                    case "M":
                        addEntite(new Mur(this), Integer.parseInt(line_info[1]), Integer.parseInt(line_info[2]));
                        break;
                    case "B":
                        addEntite(new Bombe(this), Integer.parseInt(line_info[1]), Integer.parseInt(line_info[2]));
                        break;
                    case "R":
                        addEntite(new Corde(this), Integer.parseInt(line_info[1]), Integer.parseInt(line_info[2]));
                        break;
                    case "S":
                        EntiteDynamique smick = (new Bot(this));
                        smicks.add(0,(Bot)smick);
                        addEntite(smicks.get(0), Integer.parseInt(line_info[1]), Integer.parseInt(line_info[2]));
                        IA.getInstance().addEntiteDynamique(smicks.get(0));
                        ordonnanceur.add(IA.getInstance());
                        Gravite.getInstance().addEntiteDynamique(smicks.get(0));
                        ordonnanceur.add(Gravite.getInstance());

                        break;
                    case "C":
                        Colonne tmp_colonne = new Colonne(this, Integer.parseInt(line_info[3]));
                        addEntite(tmp_colonne, Integer.parseInt(line_info[1]), Integer.parseInt(line_info[2]));
                        ControleColonne.getInstance().addEntiteDynamique(tmp_colonne);
                        //if (!ordonnanceur.contains(ControleColonne.getInstance())){
                            ordonnanceur.add(ControleColonne.getInstance());
                        //}
                        break;
                }
                // read next line
                line = reader.readLine();
            }
            isOn = true;
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private String getHighScore(){
        String score = "0";
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader("data/scores/score" + String.valueOf(level) + ".txt"));
            score = reader.readLine();
            reader.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return score;
    }

}

