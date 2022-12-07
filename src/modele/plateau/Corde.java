package modele.plateau;

import modele.deplacements.Direction;

public class Corde extends EntiteStatique {
    public Corde(Jeu _jeu) { super(_jeu); }
    
    @Override
    public boolean peutPermettreDeMonterDescendre() { return true; }

    @Override
    public boolean ecrase(Entite e) {
        return false;
    }

    @Override
    public boolean peutEtreEcrase() { return false; }

}
