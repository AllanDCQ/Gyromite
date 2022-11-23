package modele.plateau;

import modele.deplacements.Direction;

/**
 * Entités amenées à bouger (colonnes, ennemis)
 */
public abstract class EntiteDynamique extends Entite {

    /* Stock the old entity on which the hero is */
    public Entite ancienne_entite = null;


    public EntiteDynamique(Jeu _jeu) { super(_jeu); }

    public boolean avancerDirectionChoisie(Direction d) {
        return jeu.deplacerEntite(this, d);
    }
    public Entite regarderDansLaDirection(Direction d) {return jeu.regarderDansLaDirection(this, d);}

    public Entite regarderDansLaDirectionDistance(Direction d, int distance) {
        return jeu.regarderDansLaDirectionDistance(this, d, distance);
    }

}
