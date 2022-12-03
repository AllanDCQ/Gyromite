package modele.plateau;

public class Bombe extends EntiteDynamique{

    public Bombe(Jeu _jeu) {
        super(_jeu);
        speed = 0;
    }

    @Override
    public boolean peutEtreEcrase() { return true; }

    @Override
    public boolean peutServirDeSupport() { return false; }

    @Override
    public boolean peutPermettreDeMonterDescendre() { return false; }

    public boolean ecrase(Entite e ) {
        if (e == jeu.getHector()){
            jeu.ecraseEntite(this);
            jeu.increase_Score();
            return true;
        }
        return false;
    }

}
