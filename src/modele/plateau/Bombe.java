package modele.plateau;

public class Bombe extends EntiteStatique {

    public Bombe(Jeu _jeu) { super(_jeu); }

    @Override
    public boolean peutEtreEcrase() { return true; }

    @Override
    public boolean peutServirDeSupport() { return false; }

    @Override
    public boolean deplacementAction(Entite next_entite) {
        return false;
    }

}
