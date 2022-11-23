package modele.deplacements;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import modele.plateau.Entite;
import modele.plateau.EntiteDynamique;


/**
 * A la reception d'une commande, toutes les cases (EntitesDynamiques) des colonnes se déplacent dans la direction définie
 * (vérifier "collisions" avec le héros)
 */
public class ControleColonne extends RealisateurDeDeplacement {

    private Direction directionCourante = null;
    private static ControleColonne c3d;
    private boolean estEnHaut = false;
    private ArrayList<EntiteDynamique> lstEntitesDynamiquesInverse = new ArrayList<EntiteDynamique>();

    public static ControleColonne getInstance() {
        if (c3d == null) {
            c3d = new ControleColonne();
        }
        return c3d;
    }

    public void setDirectionCourante() {
        directionCourante = estEnHaut ? Direction.bas : Direction.haut;
    }


    protected boolean realiserDeplacement() { 
        boolean ret = false;
        
        for (EntiteDynamique e : estEnHaut ? lstEntitesDynamiquesInverse : lstEntitesDynamiques) {
            if (directionCourante != null){
                Entite eSurChemin = e.regarderDansLaDirection(directionCourante);
                switch (directionCourante) {
                    case gauche:
                    case droite:
                        ret = false;
                        break;

                    case haut:
                        if (!estEnHaut && (eSurChemin == null || eSurChemin.peutEtreEcrase())) {
                            if (e.avancerDirectionChoisie(Direction.haut)){
                                ret = true;
                            }
                        }
                        break;
                    case bas:
                        if (estEnHaut && (eSurChemin == null || eSurChemin.peutEtreEcrase())) {
                            if (e.avancerDirectionChoisie(Direction.bas)){
                                ret = true;
                            }
                        }
                        break;

                }
            }
        }
        if (ret) estEnHaut = !estEnHaut;
        return ret;
    }

    @Override
    public void addEntiteDynamique(EntiteDynamique ed) {
        lstEntitesDynamiques.add(ed);
        lstEntitesDynamiquesInverse.addAll(lstEntitesDynamiques);
        Collections.reverse(lstEntitesDynamiquesInverse);
    };
}
