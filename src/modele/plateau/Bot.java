/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modele.plateau;

import modele.deplacements.Direction;

import java.util.Random;

/**
 * Ennemis (Smicks)
 */
public class Bot extends EntiteDynamique {
    private Random r = new Random();

    /*
     * La direction va de gauche à droite ou de droite à gauche.
     * Lorsqu'il rencontre un obstacle le sens change ou qu'il perd de vue le hero
     */

    public boolean sleep_avance;

    public Bot(Jeu _jeu) {
        super(_jeu);
        directionCourante = Direction.droite;
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

    public void ecrase() {
        System.out.println("Bot ecrase");
    }

}
