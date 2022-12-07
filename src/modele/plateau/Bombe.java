package modele.plateau;

public class Bombe extends EntiteStatique{

    public Bombe(Jeu _jeu) {
        super(_jeu);
    }

    @Override
    public boolean peutEtreEcrase() { return true; }

    @Override
    public boolean peutServirDeSupport() { return false; }

    @Override
    public boolean peutPermettreDeMonterDescendre() { return false; }

    @Override
    public boolean ecrase(Entite e ) {
        if (e == jeu.getHector()){
            jeu.ecraseEntite(this);
            jeu.increase_Score();
            return true;
        }
        return false;
    }

}
