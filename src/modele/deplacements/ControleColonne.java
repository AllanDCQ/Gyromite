package modele.deplacements;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import modele.plateau.Entite;
import modele.plateau.EntiteDynamique;
import modele.plateau.Colonne;


/**
 * A la reception d'une commande, toutes les cases (EntitesDynamiques) des colonnes se déplacent dans la direction définie
 * (vérifier "collisions" avec le héros)
 */
public class ControleColonne extends RealisateurDeDeplacement {

    private Direction directionCourante = null;
    private static ControleColonne c3d;
    private boolean estEnHaut = true;
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
                        // repeter N fois avec N la taille de la colonne a laquelle cette entite apartient
                        for (int i = 0; i < ((Colonne)e).taille; i++){
                            if (!estEnHaut && (eSurChemin == null || eSurChemin.peutEtreEcrase())) {
                                // la colonne et EN BAS, et lentite apres et null ou ecrasable
                                if (e.avancerDirectionChoisie(Direction.haut)){
                                    ret = true;
                                }
                            }
                        }
                        break;
                    case bas:
                        // repeter N fois avec N la taille de la colonne a laquelle cette entite apartient
                        for (int i = 0; i < ((Colonne)e).taille; i++){
                            if (estEnHaut && (eSurChemin == null || eSurChemin.peutEtreEcrase())) {
                                // la colonne et EN HAUT, et lentite apres et null ou ecrasable
                                if (e.avancerDirectionChoisie(Direction.bas)){
                                    ret = true;
                                }
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
    // possibilite de reduire le temps de cette fonction en ajoutant la nouvelle entite dans l'ordre inverse
    // [ nouvelleEntite , ...] [ ... , nouvelleEntite ]
    public void addEntiteDynamique(EntiteDynamique ed) {
        lstEntitesDynamiques.add(ed);
        lstEntitesDynamiquesInverse.addAll(lstEntitesDynamiques);
        // Stocker linverse de la liste des colone pour bouger les colone de bas en haut dans lordre inverse que de haut en bas
        Collections.reverse(lstEntitesDynamiquesInverse);
    };
}
