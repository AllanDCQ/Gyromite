package modele.plateau;

import modele.deplacements.Direction;

public class Bombe extends EntiteStatique {

    public Bombe(Jeu _jeu) { super(_jeu); }

    @Override
    public boolean peutEtreEcrase() { return true; }

    @Override
    public boolean peutServirDeSupport() { return false; }

}
