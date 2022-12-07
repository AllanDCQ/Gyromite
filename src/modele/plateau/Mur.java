package modele.plateau;

import modele.deplacements.Direction;

public class Mur extends EntiteStatique {
    public Mur(Jeu _jeu) { super(_jeu); }

    @Override
    public boolean ecrase(Entite e) {
        return false;
    }

}
