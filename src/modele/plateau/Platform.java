package modele.plateau;

import modele.deplacements.Direction;

public class Platform extends EntiteStatique {
    public Platform(Jeu _jeu) { super(_jeu); }

    @Override
    public boolean ecrase(Entite e) {
        return false;
    }

}
