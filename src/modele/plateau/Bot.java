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

    /*
     * La direction va de gauche à droite ou de droite à gauche.
     * Lorsqu'il rencontre un obstacle le sens change ou qu'il perd de vue le hero
     */

    public boolean sleep_avance;

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
        if (e == jeu.getHector()){
            jeu.getHector().ecrase(this);
            return false;
        }else{
            jeu.ecraseEntite(this);
            IA.getInstance().removeEntiteDynamique(this);
            Gravite.getInstance().removeEntiteDynamique(this);
            return true;
        }
    }

}
