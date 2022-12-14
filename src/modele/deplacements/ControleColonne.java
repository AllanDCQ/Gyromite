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

    private Direction directionCourante = Direction.haut;
    private static ControleColonne c3d;
    private ArrayList<EntiteDynamique> lstEntitesDynamiquesInverse = new ArrayList<EntiteDynamique>();

    public static ControleColonne getInstance() {
        if (c3d == null) {
            c3d = new ControleColonne();
        }
        return c3d;
    }

    public static ControleColonne reset(){
        c3d = new ControleColonne();
        return c3d;
    }

    public void setDirectionCourante() {
        switch(directionCourante){
            case gauche:
            case droite:
                break;
            case bas:
                directionCourante = Direction.haut;
                break;
            case haut:
                directionCourante = Direction.bas;
                break;
        }
        for (EntiteDynamique e : lstEntitesDynamiques){
            ((Colonne)e).restant = ((Colonne)e).taille - ((Colonne)e).restant;
        }
    }

    protected boolean realiserDeplacement() { 
        boolean ret = false;

        for (EntiteDynamique e : directionCourante == Direction.haut ? lstEntitesDynamiquesInverse : lstEntitesDynamiques) {
            if (directionCourante != null){
                switch (directionCourante) {
                    case gauche:
                    case droite:
                        ret = false;
                        break;

                    case haut:
                        // tant que la colonne lui reste un mouvement vers le haut on avance la colonne vers le haut
                        if (((Colonne)e).restant > 0){
                            if (e.avancerDirectionChoisie(Direction.haut)){
                                ret = true;
                                // reste -1 case a bouge vers la direction
                                ((Colonne)e).restant -= 1;
                            }
                        }
                        break;
                    case bas:
                        // tant que la colonne lui reste un mouvement vers le bas on avance la colonne vers le haut
                        if (((Colonne)e).restant > 0){
                            if (e.avancerDirectionChoisie(Direction.bas)){
                                ret = true;
                                // reste -1 case a bouge vers la direction
                                ((Colonne)e).restant -= 1;
                            }
                        }
                    break;
                }
            }
        }
        return ret;
    }

    @Override
    // possibilite de reduire le temps de cette fonction en ajoutant la nouvelle entite dans l'ordre inverse
    // [ nouvelleEntite , ...] [ ... , nouvelleEntite ]
    public void addEntiteDynamique(EntiteDynamique ed) {
        lstEntitesDynamiques.add(ed);
        lstEntitesDynamiquesInverse.clear();
        lstEntitesDynamiquesInverse.addAll(lstEntitesDynamiques);
        // Stocker linverse de la liste des colone pour bouger les colone de bas en haut dans lordre inverse que de haut en bas
        Collections.reverse(lstEntitesDynamiquesInverse);
    };
}
