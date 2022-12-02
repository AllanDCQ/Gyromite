package VueControleur;

import modele.deplacements.Direction;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.*;


public class SpritesEntiteDynamique extends SpritesEntite {

    public ListIterator<ImageIcon> marche_gauche;
    public ListIterator<ImageIcon> marche_droite;
    public ListIterator<ImageIcon> monter;
    public ListIterator<ImageIcon> tomber;

    public SpritesEntiteDynamique(ArrayList<ImageIcon> droite,
                                  ArrayList<ImageIcon> gauche,
                                  ArrayList<ImageIcon> monte,
                                  ArrayList<ImageIcon> tombe,
                                  ArrayList<ImageIcon> attend) {
        marche_droite = droite.listIterator();
        marche_gauche = gauche.listIterator();
        monter = monte.listIterator();
        tomber = tombe.listIterator();
        attente = attend.listIterator();
    }

    public ImageIcon get_sprite(Direction direction) {
        resetIt(direction);
        if(direction != null) {
            return switch (direction) {
                case droite -> marche_droite.next();
                case gauche -> marche_gauche.next();
                case bas -> tomber.next();
                case haut -> monter.next();
            };
        } else {
            return attente.next();
        }
    }



    public void resetIt(Direction d) {
        if(d != null) {
            switch (d) {
                case droite -> {
                    if(marche_droite.hasNext() == false) {
                        while (marche_droite.hasPrevious()) {
                            marche_droite.previous();
                        }
                    }
                }
                case gauche -> {
                    if(marche_gauche.hasNext() == false) {
                        while (marche_gauche.hasPrevious()) {
                            marche_gauche.previous();
                        }
                    }
                }
                case bas -> {
                    if(tomber.hasNext() == false) {
                        while (tomber.hasPrevious()) {
                            tomber.previous();
                        }
                    }

                }
                case haut -> {
                    if(monter.hasNext() == false) {
                        while (monter.hasPrevious()) {
                            monter.previous();
                        }
                    }

                }
            }
        } else {
            if(attente.hasNext() == false) {
                while (attente.hasPrevious()) {
                    attente.previous();
                }
            }
        }
    }


}

