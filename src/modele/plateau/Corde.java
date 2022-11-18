package modele.plateau;

public class Corde extends EntiteStatique {
    public Corde(Jeu _jeu) { super(_jeu); }

    @Override
	public boolean peutServirDeSupport() { return true; }
    @Override
    public boolean peutPermettreDeMonterDescendre() { return true; }

    @Override
    public boolean peutEtreEcrase() { return true; }// l'entité peut être traverse (par exemple par heros ou smicks ...)

    @Override
    public String get_class_string() {
        return "Corde";
    }

}
