package modele.plateau;

public class Radis extends EntiteStatique{

    public Radis(Jeu _jeu) {
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
            jeu.recupere_radis();
        }
        if (e instanceof Bot ){
            ((Bot) e).setDistrait(true);
        }
        jeu.ecraseEntite(this);
        return true;
    }

}