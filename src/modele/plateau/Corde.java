package modele.plateau;

public class Corde extends EntiteStatique {
    public Corde(Jeu _jeu) { super(_jeu); }

	public boolean peutServirDeSupport() { return true; }
    public boolean peutPermettreDeMonterDescendre() { return true; };
	public boolean peutEtreTraverser() { return true; } // l'entité peut être traverse (par exemple par heros ou smicks ...)
}
