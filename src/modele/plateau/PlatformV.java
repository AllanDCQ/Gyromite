package modele.plateau;

public class PlatformV extends EntiteStatique {
    public PlatformV(Jeu _jeu) { super(_jeu); }

    @Override
    public boolean ecrase(Entite e) {
        return false;
    }
}
