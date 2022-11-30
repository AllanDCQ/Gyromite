package modele.plateau;

import modele.deplacements.Direction;

/**
 * Entités amenées à bouger (colonnes, ennemis)
 */
public abstract class EntiteDynamique extends Entite {

    /* Stock the old entity on which the hero is */
    private Entite ancienne_entite;

    protected int speed;

    protected Direction directionCourante;

    protected boolean directionUpward;
    protected boolean directionDescent;

    public EntiteDynamique(Jeu _jeu) {
        super(_jeu);
        ancienne_entite = null;
    }

    public boolean avancerDirectionChoisie(Direction d) {
        if(jeu.Time_wait%speed == 0) {
            return jeu.deplacerEntite(this, d);
        } else return false;
    }

    public Entite regarderDansLaDirection(Direction d) {return jeu.regarderDansLaDirection(this, d);}
    public Entite regarderDansLaDirectionDouble(Direction d1, Direction d2) {
        Entite prevision =  jeu.regarderDansLaDirection(this, d1);
        if(prevision != null) {
            prevision = jeu.regarderDansLaDirection(prevision, d2);
            return prevision;
        }
        else {
            return null;
        }
    }
    public Entite regarderDansLaDirectionDistance(Direction d, int distance) {
        return jeu.regarderDansLaDirectionDistance(this, d, distance);
    }

    public Direction getDirectionCourante() {
        return directionCourante;
    }
    public void setDirectionCourante(Direction directionCourante) {
        if(jeu.Time_wait%speed == 0) {
            this.directionCourante = directionCourante;
        }
    }

    public Entite getAncienne_entite() {
        return ancienne_entite;
    }
    public void setAncienne_entite(Entite ancienne_entite) {
        this.ancienne_entite = ancienne_entite;
    }

    public Boolean getDirectionUpward() { return directionUpward; }
    public void setDirectionUpward(boolean directionUpward) { this.directionUpward = directionUpward;}

    public Boolean getDirectionDescent() { return directionDescent; }
    public void setDirectionDescent(boolean directionDescent) { this.directionDescent = directionDescent;}


    public Entite getHero() {return jeu.getHector();}

    public abstract void ecrase();

}
