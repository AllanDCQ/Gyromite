package modele.plateau;

import modele.deplacements.Direction;

public class Corde extends EntiteStatique {
    public Corde(Jeu _jeu) { super(_jeu); }

    @Override
	public boolean peutServirDeSupport() { return true; }
    @Override
    public boolean peutPermettreDeMonterDescendre() { return true; }

    @Override
    public boolean peutEtreEcrase() { return false; }

}
