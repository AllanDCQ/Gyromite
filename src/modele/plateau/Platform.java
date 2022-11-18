package modele.plateau;

public class Platform extends EntiteStatique {
    public Platform(Jeu _jeu) { super(_jeu); }

    @Override
    public String get_class_string() {
        return "Platform";
    }
}
