/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modele.plateau;

import modele.deplacements.Direction;
import modele.deplacements.Controle4Directions;
import modele.deplacements.Gravite;

/**
 * Héros du jeu
 */
public class Heros extends EntiteDynamique {
    public Heros(Jeu _jeu) {
        super(_jeu);
        speed = 1;
    }

    public boolean peutEtreEcrase() { return true; }
    public boolean peutServirDeSupport() { return false; }
    public boolean peutPermettreDeMonterDescendre() { return false; }

    public boolean ecrase(Entite e) {
        jeu.ecraseEntite(this);
        Controle4Directions.getInstance().removeEntiteDynamique(this);
        Gravite.getInstance().removeEntiteDynamique(this);
        jeu.heroDead = true;
        return true;
    }

}
