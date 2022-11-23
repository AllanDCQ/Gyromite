/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modele.plateau;

/**
 * Héros du jeu
 */
public class Heros extends EntiteDynamique {
    public Heros(Jeu _jeu) {
        super(_jeu);
    }

    public boolean peutEtreEcrase() { return true; }
    public boolean peutServirDeSupport() { return true; }
    public boolean peutPermettreDeMonterDescendre() { return false; }

    @Override
    public boolean deplacementAction(Entite next_entite) {
        /* Si la prochaine case est une bombe */
        if (next_entite instanceof Bombe) {
            jeu.increase_Score();
            return true;
        } else {
            return false;
        }

    }


}
