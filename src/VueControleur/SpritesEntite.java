package VueControleur;

import modele.deplacements.Direction;

import javax.swing.*;
import java.util.Iterator;
import java.util.ListIterator;

public abstract class SpritesEntite implements Iterable<ListIterator<ImageIcon>> {

    protected ListIterator<ImageIcon> attente;

    @Override
    public Iterator<ListIterator<ImageIcon>> iterator() {
        return null;
    }
}
