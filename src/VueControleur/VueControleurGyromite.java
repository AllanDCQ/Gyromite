package VueControleur;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;

import modele.deplacements.Controle4Directions;
import modele.deplacements.ControleColonne;
import modele.deplacements.Direction;
import modele.plateau.*;


/** Cette classe a deux fonctions :
 *  (1) Vue : proposer une représentation graphique de l'application (cases graphiques, etc.)
 *  (2) Controleur : écouter les évènements clavier et déclencher le traitement adapté sur le modèle (flèches direction Pacman, etc.))
 *
 */
public class VueControleurGyromite extends JFrame implements Observer {
    private Jeu jeu; // référence sur une classe de modèle : permet d'accéder aux données du modèle pour le rafraichissement, permet de communiquer les actions clavier (ou souris)

    private int sizeX; // taille de la grille affichée
    private int sizeY;

    // icones affichées dans la grille
    private ImageIcon icoHero;
    private ImageIcon icoVide;
    private ImageIcon icoColonne;
    private ImageIcon icoBedrock;
    private ImageIcon icoPlatform;
    private ImageIcon icoCorde;
    private ImageIcon icoColonneHaut;
    private ImageIcon icoColonneBas;

    private ImageIcon[] icoBombe = new ImageIcon[4];
    private int current_sprite_bomb;

    private ImageIcon[] icoBot = new ImageIcon[4];
    private int current_sprite_bot;

    private JLabel[][] tabJLabel; // cases graphiques (au moment du rafraichissement, chaque case va être associée à une icône, suivant ce qui est présent dans le modèle)

    private JLabel menu_label; // Label en haut
    private JLabel time_label; // Label droite du menu
    private JLabel score_label; // Label gauche du menu


    public VueControleurGyromite(Jeu _jeu) {
        sizeX = _jeu.SIZE_X;
        sizeY = _jeu.SIZE_Y;
        jeu = _jeu;

        chargerLesIcones();
        placerLesComposantsGraphiques();
        ajouterEcouteurClavier();
    }

    private void ajouterEcouteurClavier() {
        addKeyListener(new KeyAdapter() { // new KeyAdapter() { ... } est une instance de classe anonyme, il s'agit d'un objet qui correspond au controleur dans MVC
            @Override
            public void keyPressed(KeyEvent e) {
                switch(e.getKeyCode()) {  // on regarde quelle touche a été pressée
                    case KeyEvent.VK_LEFT : Controle4Directions.getInstance().setDirectionCourante(Direction.gauche); break;
                    case KeyEvent.VK_RIGHT : Controle4Directions.getInstance().setDirectionCourante(Direction.droite); break;
                    case KeyEvent.VK_DOWN : Controle4Directions.getInstance().setDirectionCourante(Direction.bas); break;
                    case KeyEvent.VK_UP : Controle4Directions.getInstance().setDirectionCourante(Direction.haut); break;
                    case KeyEvent.VK_Q : ControleColonne.getInstance().setDirectionCourante(); break;
                }
            }
        });
    }


    private void chargerLesIcones() {
        icoHero = chargerIcone("Images/player_ca.png", 0, 0, 35, 40);//chargerIcone("Images/Pacman.png");
    
        //icoBot = chargerIcone("Images/smick.png", 0, 0, 20, 20);//chargerIcone("Images/Pacman.png");

        icoVide = chargerIcone("Images/Mur.png");
        icoColonne = chargerIcone("Images/tileset.png", 16, 9*16, 16, 16);
        // Icon de colonne du haut et du bas
        icoColonneHaut = chargerIcone("Images/tileset.png", 0, 9*16, 16, 16);
        icoColonneBas = chargerIcone("Images/tileset.png", 32, 9*16, 16, 16);
        icoBedrock = chargerIcone("Images/tileset.png", 32, 0, 16, 16);
        icoPlatform = chargerIcone("Images/tileset.png", 0, 0, 16, 16);
        icoCorde = chargerIcone("Images/tileset.png", 16, 0, 16, 16);

        /* SpriteSheet de la bombe */
        icoBombe[0] = chargerIcone("Images/bomb_ca.png", 0, 0, 64, 64);
        icoBombe[1] = chargerIcone("Images/bomb_ca.png", 64, 0, 64, 64);
        icoBombe[2] = chargerIcone("Images/bomb_ca.png", 128, 0, 64, 64);
        icoBombe[3] = chargerIcone( "Images/bomb_ca.png", 192, 0, 64, 64);

        /* SpriteSheet des smicks */
        icoBot[0] = chargerIcone("Images/smick_ca.png", 0, 0, 32, 32);
        icoBot[1] = chargerIcone("Images/smick_ca.png", 32, 0, 32, 32);
        icoBot[2] = chargerIcone("Images/smick_ca.png", 0, 0, 32, 32);
        icoBot[3] = chargerIcone( "Images/smick_ca.png", 32, 0, 32, 32);
    }

    private void placerLesComposantsGraphiques() {
        setTitle("Gyromite");
        setSize(900, 960);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // permet de terminer l'application à la fermeture de la fenêtre

        /* Initialization and Add father Panel (father layout) */
        JPanel centralPanel = new JPanel();
        add(centralPanel);
        centralPanel.setLayout(new BorderLayout());

        /* Initialization of son layouts */
        JComponent barMenu = component_barMenu();
        JComponent grilleJLabels = component_gameBoard(); // grilleJLabels va contenir les cases graphiques et les positionner sous la forme d'une grille

        /* Add son layouts to the father layout */
        centralPanel.add(barMenu, BorderLayout.NORTH);
        centralPanel.add(grilleJLabels, BorderLayout.CENTER);

    }

    
    /**
     * Il y a une grille du côté du modèle ( jeu.getGrille() ) et une grille du côté de la vue (tabJLabel)
     */
    private void mettreAJourAffichageJeu() {

        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {
                if (jeu.getGrille()[x][y] instanceof Heros) { // si la grille du modèle contient un Pacman, on associe l'icône Pacman du côté de la vue

                    tabJLabel[x][y].setIcon(icoHero);

                    // si transparence : images avec canal alpha + dessins manuels (voir ci-dessous + créer composant qui redéfinie paint(Graphics g)), se documenter
                    //BufferedImage bi = getImage("Images/smick.png", 0, 0, 20, 20);
                    //tabJLabel[x][y].getGraphics().drawImage(bi, 0, 0, null);

                } else if (jeu.getGrille()[x][y] instanceof Bot) {
                    tabJLabel[x][y].setIcon(next_sprite(current_sprite_bot, Bot.class.getName()));
                } else if (jeu.getGrille()[x][y] instanceof Mur) {
                    tabJLabel[x][y].setIcon(icoBedrock);
                } else if (jeu.getGrille()[x][y] instanceof Colonne) {
                    tabJLabel[x][y].setIcon(icoColonne);
                    // Si la case au dessus de x,y different de colonne afficher l'icon du haut de la colonne
                    if (!(jeu.getGrille()[x][y - 1] instanceof Colonne)) {
                        tabJLabel[x][y].setIcon(icoColonneHaut);
                    // Si la case au dessous de x,y different de colonne afficher l'icon du bas de la colonne
                    } else if (!(jeu.getGrille()[x][y + 1] instanceof Colonne)) {
                        tabJLabel[x][y].setIcon(icoColonneBas);
                    // Sinon afficher l'icon standard de la colonne
                    } else {
                        tabJLabel[x][y].setIcon(icoColonne);
                    }
                } else if (jeu.getGrille()[x][y] instanceof Platform) {
                    tabJLabel[x][y].setIcon(icoPlatform);
                } else if (jeu.getGrille()[x][y] instanceof Bombe) {
                    tabJLabel[x][y].setIcon(next_sprite(current_sprite_bomb, Bombe.class.getName()));
                } else if (jeu.getGrille()[x][y] instanceof Corde) {
                    tabJLabel[x][y].setIcon(icoCorde);
                } else {
                    tabJLabel[x][y].setIcon(icoVide);
                }
            }
        }
    }

    /**
     * Update the menu layout
     */
    private void mettreAJourAffichageMenu() {
        time_label.setText(String.valueOf(jeu.GetTimeLeft()));
        score_label.setText(String.valueOf(jeu.GetScore()));
    }

    @Override
    public void update(Observable o, Object arg) {
        //mettreAJourAffichage();

        SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        /* Increase time */
                        jeu.increase_TimeLeft();

                        /* Initialization and Add father Panel (father layout) */
                        mettreAJourAffichageJeu();
                        mettreAJourAffichageMenu();

                    }
                });
    }


    // chargement de l'image entière comme icone
    private ImageIcon chargerIcone(String urlIcone) {
        BufferedImage image = null;

        try {
            image = ImageIO.read(new File(urlIcone));
        } catch (IOException ex) {
            Logger.getLogger(VueControleurGyromite.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

        return new ImageIcon(image.getScaledInstance(900/sizeX, 900/sizeY, java.awt.Image.SCALE_SMOOTH));
    }

    // chargement d'une sous partie de l'image
    private ImageIcon chargerIcone(String urlIcone, int x, int y, int w, int h) {
        // charger une sous partie de l'image à partir de ses coordonnées dans urlIcone
        BufferedImage bi = getSubImage(urlIcone, x, y, w, h);
        // adapter la taille de l'image a la taille du composant (ici : 20x20)
        return new ImageIcon(bi.getScaledInstance(900/sizeX, 900/sizeY,java.awt.Image.SCALE_SMOOTH));
    }

    private ImageIcon chargerIconeNumber(String urlIcone, int x, int y, int w, int h) {
        BufferedImage bi = getSubImage(urlIcone, x, y, w, h);
        return new ImageIcon(bi.getScaledInstance(350/sizeX, 350/sizeY,java.awt.Image.SCALE_SMOOTH));
    }

    private BufferedImage getSubImage(String urlIcone, int x, int y, int w, int h) {
        BufferedImage image = null;

        try {
            image = ImageIO.read(new File(urlIcone));
        } catch (IOException ex) {
            Logger.getLogger(VueControleurGyromite.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        BufferedImage bi = image.getSubimage(x, y, w, h);
        return bi;
    }




    /**
     * Initialize the menu
     * @return the JLabel composed of time left and score
     */
    private JLabel component_barMenu() {
        /* Initialize the Menu label */
        ImageIcon icon = new ImageIcon("Images/menu.png");
        menu_label = new JLabel(icon);
        menu_label.setPreferredSize(new Dimension(sizeX,60));

        /* Set the menu layout to a GridBagLayout with constraints */
        menu_label.setLayout(new GridLayout());

        /* Initialize the Score label */
        score_label = new JLabel(String.valueOf(jeu.GetScore()));
        score_label.setForeground(Color.white);
        score_label.setFont(new Font(Font.MONOSPACED, score_label.getFont().getStyle(), 22));
        score_label.setBorder(BorderFactory.createEmptyBorder(0, 220, 0, 0));
        menu_label.add(score_label, BorderLayout.CENTER);

        /* Initialize the Time label */
        time_label = new JLabel(String.valueOf(jeu.GetTimeLeft()));
        time_label.setFont(new Font(Font.MONOSPACED, score_label.getFont().getStyle(), 22));
        time_label.setForeground(Color.green);
        time_label.setBorder(BorderFactory.createEmptyBorder(2, 210, 0, 0));
        menu_label.add(time_label);

        return menu_label;
    }


    /**
     * Initialize the game board, grid s
     * @return the GridLayout composed of JLabel
     */
    private JPanel component_gameBoard() {
        JPanel grille = new JPanel(new GridLayout(sizeX, sizeY));
        grille.setPreferredSize(new Dimension(sizeX,900));

        tabJLabel = new JLabel[sizeX][sizeY];
        for (int y = 0; y < sizeY; y++) {
            for (int x = 0; x < sizeX; x++) {
                JLabel jlab = new JLabel();

                tabJLabel[x][y] = jlab; // on conserve les cases graphiques dans tabJLabel pour avoir un accès pratique à celles-ci (voir mettreAJourAffichage() )
                grille.add(jlab);
            }
        }

        return grille;
    }


    private ImageIcon next_sprite(int current_sprite, String entite ) {
        ImageIcon next;
        int current_time = jeu.GetTimeLeft();


        switch (current_sprite) {
            case 3 -> current_sprite = 0;
            default -> {
                current_sprite += 1;
            }
        }

        switch (entite) {
            case "modele.plateau.Bombe" -> {
                current_sprite_bomb = current_sprite;
                next = icoBombe[current_sprite_bomb];
            }
            case "modele.plateau.Bot" -> {
                current_sprite_bot = current_sprite;
                next = icoBot[current_sprite_bot];
            }

            default -> throw new IllegalStateException("Unexpected value: " + entite);
        }

        return next;
    }

}
