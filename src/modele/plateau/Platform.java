package modele.plateau;

public class Platform extends EntiteStatique {
    public Platform(Jeu _jeu) { super(_jeu); }

    @Override
    public boolean deplacementAction(Entite next_entite) {
        return false;
    }
}
