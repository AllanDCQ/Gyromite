package modele.plateau;

public class Mur extends EntiteStatique {
    public Mur(Jeu _jeu) { super(_jeu); }

    @Override
    public String get_class_string() {
        return "Mur";
    }
}
