package modele.deplacements;

import modele.plateau.Entite;
import modele.plateau.EntiteDynamique;

/**
 * Controle4Directions permet d'appliquer une direction (connexion avec le clavier) à un ensemble d'entités dynamiques
 */
public class Controle4Directions extends RealisateurDeDeplacement {
    private Direction directionCourante;
    private Direction lastDirection = Direction.droite;
    // Design pattern singleton
    private static Controle4Directions c3d;

    public static Controle4Directions getInstance() {
        if (c3d == null) {
            c3d = new Controle4Directions();
        }
        return c3d;
    }

    public static Controle4Directions reset(){
        c3d = new Controle4Directions();
        return c3d;
    }

    public void setDirectionCourante(Direction _directionCourante) {
        directionCourante = _directionCourante;
        lastDirection = directionCourante;
    }

    public boolean realiserDeplacement() {
        boolean ret = false;
        for (EntiteDynamique e : lstEntitesDynamiques) {
            Entite eBas = e.regarderDansLaDirection(Direction.bas);
            if (directionCourante != null)
                switch (directionCourante) {
                    case gauche:
                    case droite:
                        if (e.avancerDirectionChoisie(directionCourante))
                            ret = true;
                        break;

                    case haut:
                        // on ne peut pas sauter sans prendre appui
                        // (attention, test d'appui réalisé à partir de la position courante, si la gravité à été appliquée, il ne s'agit pas de la position affichée, amélioration possible)
                        if (eBas != null && eBas.peutServirDeSupport()) {
                            if (e.avancerDirectionChoisie(Direction.haut))
                                ret = true;
                        }
                        break;
                    case bas:
                        /* Peut descendre si la case du bas a true peutPermettreDeMonterDescendre */
                        if (eBas != null && eBas.peutPermettreDeMonterDescendre()) {
                            e.avancerDirectionChoisie(Direction.bas);
                            ret = true;
                        }
                        break;

                }
        }

        return ret;

    }

    public void resetDirection() {
        directionCourante = null;
    }

    public Direction getDirection() {return lastDirection;}

    public Direction getDirectionCourante() {return directionCourante;}
}
