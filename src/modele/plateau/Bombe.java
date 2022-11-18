package modele.plateau;

public class Bombe extends EntiteStatique {

    public Bombe(Jeu _jeu) { super(_jeu); }

    @Override
    public boolean peutEtreEcrase() { return true; }

    @Override
    public String get_class_string() {
        return "Bombe";
    }

}
