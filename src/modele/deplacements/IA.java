package modele.deplacements;

import modele.plateau.Entite;
import modele.plateau.EntiteDynamique;

public class IA extends RealisateurDeDeplacement {
    private Direction directionCourante = Direction.droite;
    private static IA iad;
    private boolean sleep_avance;

    public static IA getInstance() {
        if (iad == null) {
            iad = new IA();
        }
        return iad;
    }

    protected boolean realiserDeplacement() {
        boolean ret = false;
        for (EntiteDynamique e : lstEntitesDynamiques) {

            if (sleep_avance) {
                for (int dist = 0; dist < 5; dist++) {
                    Entite egauche = e.regarderDansLaDirectionDistance(Direction.gauche,dist);
                    if (egauche != null) {
                        if(egauche.getClass().getName().equals("modele.plateau.Heros")) {
                            directionCourante = Direction.gauche;
                            System.out.println(directionCourante);
                        }
                    }

                    Entite edroit = e.regarderDansLaDirectionDistance(Direction.droite,dist);
                    if (edroit != null) {
                        if(edroit.getClass().getName().equals("modele.plateau.Heros")) {
                            directionCourante = Direction.droite;
                            System.out.println(directionCourante);
                        }
                    }

                }


                if (directionCourante != null)
                    switch (directionCourante) {
                        case gauche:
                        case droite:
                            if(e.avancerDirectionChoisie(directionCourante)) {
                                directionCourante = null;
                                ret = true;
                            }
                            break;
                    }
                sleep_avance = false;
            } else {
                sleep_avance = true;
            }
        }
        return ret;
    } // TODO
}
