package modele.plateau;

import modele.deplacements.Direction;

public class Colonne extends EntiteDynamique {
    public Colonne(Jeu _jeu) { super(_jeu); }

    public boolean peutEtreEcrase() { return false; }
    public boolean peutServirDeSupport() { return true; }
    public boolean peutPermettreDeMonterDescendre() { return false; }

}
