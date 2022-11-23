package modele.plateau;

public class Colonne extends EntiteDynamique {
    public Colonne(Jeu _jeu) { super(_jeu); }

    public boolean peutEtreEcrase() { return false; }
    public boolean peutServirDeSupport() { return true; }
    public boolean peutPermettreDeMonterDescendre() { return false; }

    @Override
    public boolean deplacementAction(Entite next_entite) {
        //if(this.getClass().getName().equals("modele.plateau.Colonne")) {
        //    if (next_entite.getClass().getName().equals("modele.plateau.Colonne")) return true;
        //    else return false;
        //} else return false;
        return false;
    }

}
