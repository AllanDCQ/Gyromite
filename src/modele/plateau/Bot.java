/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modele.plateau;

import modele.deplacements.Direction;
import modele.deplacements.IA;
import modele.deplacements.Gravite;

import java.util.Random;

/**
 * Ennemis (Smicks)
 */
public class Bot extends EntiteDynamique {
    private Random r = new Random();

    private boolean distrait;
    public int time_sleep = 0;

    public Bot(Jeu _jeu) {
        super(_jeu);
        directionCourante = Direction.droite;
        speed = 10;
    }

    public boolean peutEtreEcrase() {
        return true;
    }

    public boolean peutServirDeSupport() {
        return true;
    }

    public boolean peutPermettreDeMonterDescendre() {
        return false;
    }

    public boolean ecrase(Entite e) {
        // Si le Bot et ecrase par hero on ecrasse le hero a la place
        if (e == jeu.getHector()){
            jeu.getHector().ecrase(this);
            return false;
        }else{
            // Sinon le Bot et surement ecrase par une colonne et donc il meurt.
            // Ecrase le Bot et le retire des realisateur de deplacement IA et gravite
            jeu.ecraseEntite(this);
            IA.getInstance().removeEntiteDynamique(this);
            Gravite.getInstance().removeEntiteDynamique(this);
            return true;
        }
    }

    public boolean isDistrait() {
        return distrait;
    }

    public void setDistrait(boolean distrait) {
        time_sleep = 30;
        this.distrait = distrait;
    }

    public void decreaseSleepTime() {
        if (time_sleep > 0) {
            time_sleep -= 1;
            distrait = true;
        } else {
            distrait = false;
        }
    }
}
