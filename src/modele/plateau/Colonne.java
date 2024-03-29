package modele.plateau;
import modele.deplacements.Direction;

public class Colonne extends EntiteDynamique {
    public Colonne(Jeu _jeu, int _taille) { 
        super(_jeu); 
        taille = _taille;
        restant = 0;
        speed = 1;
    }

    public int taille;
    public int restant;

    public boolean peutEtreEcrase() { return false; }
    public boolean peutServirDeSupport() { return true; }
    public boolean peutPermettreDeMonterDescendre() { return false;}

    @Override
    public boolean avancerDirectionChoisie(Direction d) {
        return jeu.deplacerColonne(this, d);
    }

    public boolean ecrase(Entite e) {
        System.out.println("Colonne ecrase"); // Jamais sense de produire
        return false;
    }


}
