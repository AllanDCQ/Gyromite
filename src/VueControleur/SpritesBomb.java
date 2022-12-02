package VueControleur;

import javax.swing.*;
import java.util.ArrayList;
import java.util.ListIterator;


public class SpritesBomb extends SpritesEntite {

    public ListIterator<ImageIcon> explose;

    public SpritesBomb(ArrayList<ImageIcon> attend, ArrayList<ImageIcon> explosion) {
        explose = explosion.listIterator();
        attente = attend.listIterator();
    }

    public ImageIcon get_sprite() {
        resetIt(attente);
        return attente.next();

    }

    public void resetIt(ListIterator<ImageIcon> attente) {
        if(attente.hasNext() == false) {
            while (attente.hasPrevious()) {
                attente.previous();
            }
        }
    }

}