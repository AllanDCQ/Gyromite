package modele.plateau;

import modele.deplacements.Direction;;

public class Colonne extends EntiteDynamique {
    public Colonne(Jeu _jeu, int _taille) { 
        super(_jeu); 
        this.taille = _taille;
    }

    public int taille;

    public boolean peutEtreEcrase() { return false; }
    public boolean peutServirDeSupport() { return true; }
    public boolean peutPermettreDeMonterDescendre() { return false;}

    @Override
    public boolean avancerDirectionChoisie(Direction d) {
        return jeu.deplacerColonne(this, d);
    }

    public void ecrase() {
        System.out.println("Colonne ecrase"); // Jamais sense de produire
    }

}
