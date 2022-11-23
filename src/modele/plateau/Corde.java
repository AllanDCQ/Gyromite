package modele.plateau;

public class Corde extends EntiteStatique {
    public Corde(Jeu _jeu) { super(_jeu); }

    @Override
	public boolean peutServirDeSupport() { return true; }
    @Override
    public boolean peutPermettreDeMonterDescendre() { return true; }

    @Override
    public boolean deplacementAction(Entite next_entite) {
        return false;
    }

    @Override
    public boolean peutEtreEcrase() { return false; }

}
