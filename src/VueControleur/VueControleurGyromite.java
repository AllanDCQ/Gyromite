package VueControleur;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.*;

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
    private ImageIcon icoVide;
    private ImageIcon icoColonne;
    private ImageIcon icoColonneHautAttacher;
    private ImageIcon icoColonneBasAttacher;
    private ImageIcon icoBedrock;
    private ImageIcon icoPlatform;
    private ImageIcon icoCorde;
    private ImageIcon icoColonneHaut;
    private ImageIcon icoColonneBas;
    private ImageIcon icoPlatformV;
    private ImageIcon icoPlatformColoneGauche;
    private ImageIcon icoPlatformColoneDroite;

    private ImageIcon[] icoBombe = new ImageIcon[4];
    private int current_sprite_bomb;


    private JLabel[][] tabJLabel; // cases graphiques (au moment du rafraichissement, chaque case va être associée à une icône, suivant ce qui est présent dans le modèle)

    private JLabel menu_label; // Label en haut
    private JLabel time_label; // Label droite du menu
    private JLabel score_label; // Label gauche du menu

    private ListIterator<SpritesEntiteDynamique> sprite_bot;
    private SpritesEntiteDynamique sprite_hero;

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

        /* SpriteSheet du hero smicks */
        ArrayList<ImageIcon> hero_marche_gauche = new ArrayList<ImageIcon>();
        hero_marche_gauche.add(chargerIcone("Images/player_ca_gauche.png", 96, 0, 32, 46));
        hero_marche_gauche.add(chargerIcone("Images/player_ca_gauche.png", 128, 0, 32, 46));

        ArrayList<ImageIcon> hero_marche_droite = new ArrayList<ImageIcon>();
        hero_marche_droite.add(chargerIcone("Images/player_ca_droite.png", 32, 0, 32, 46));
        hero_marche_droite.add(chargerIcone("Images/player_ca_droite.png", 64, 0, 32, 46));

        ArrayList<ImageIcon> hero_monter = new ArrayList<ImageIcon>();
        hero_monter.add(chargerIcone("Images/player_ca_gauche.png", 0, 96, 32, 46));
        hero_monter.add(chargerIcone("Images/player_ca_gauche.png", 32, 96, 32, 46));

        ArrayList<ImageIcon> hero_tomber = new ArrayList<ImageIcon>();
        hero_tomber.add(chargerIcone("Images/player_ca_gauche.png", 0, 137, 32, 46));

        ArrayList<ImageIcon> hero_attend = new ArrayList<ImageIcon>();
        hero_attend.add(chargerIcone("Images/player_ca_droite.png", 0, 0, 32, 46));

        SpritesEntiteDynamique hero = new SpritesEntiteDynamique(hero_marche_droite, hero_marche_gauche, hero_monter, hero_tomber,hero_attend);
        sprite_hero = hero;


        icoVide = chargerIcone("Images/Mur.png");
        icoBedrock = chargerIcone("Images/tileset.png", 32, 0, 16, 16);

        // Icon de colonne, colonne haut et du bas et colonne attcher
        icoColonne = chargerIcone("Images/tileset.png", 16, 3*16, 16, 16);
        icoColonneHaut = chargerIcone("Images/tileset.png", 0, 3*16, 16, 16);
        icoColonneBas = chargerIcone("Images/tileset.png", 32, 3*16, 16, 16);
        icoColonneHautAttacher = chargerIcone("Images/tileset.png", 0, 2*16, 16, 16);
        icoColonneBasAttacher = chargerIcone("Images/tileset.png", 32, 2*16, 16, 16);
        // Icon Platform Horizontal et vertical
        icoPlatform = chargerIcone("Images/tileset.png", 0, 0, 16, 16);
        icoPlatformV = chargerIcone("Images/tileset.png", 0, 16, 16, 16);
        icoPlatformColoneGauche = chargerIcone("Images/tileset.png", 16, 16, 16, 16);
        icoPlatformColoneDroite = chargerIcone("Images/tileset.png", 32, 16, 16, 16);
        // Icon corde
        icoCorde = chargerIcone("Images/tileset.png", 16, 0, 16, 16);
        /* SpriteSheet de la bombe */
        icoBombe[0] = chargerIcone("Images/bomb_ca.png", 0, 0, 64, 64);
        icoBombe[1] = chargerIcone("Images/bomb_ca.png", 64, 0, 64, 64);
        icoBombe[2] = chargerIcone("Images/bomb_ca.png", 128, 0, 64, 64);
        icoBombe[3] = chargerIcone( "Images/bomb_ca.png", 192, 0, 64, 64);

        /* SpriteSheet des smicks */
        ArrayList<SpritesEntiteDynamique> sprites_bot_list = new ArrayList<SpritesEntiteDynamique>();
        int size_bot = 2;
        ArrayList<ImageIcon> bot_marche_gauche = new ArrayList<ImageIcon>();
        bot_marche_gauche.add(chargerIcone("Images/smick_ca.png", 32, 0, 32, 32));
        bot_marche_gauche.add(chargerIcone("Images/smick_ca.png", 0, 0, 32, 32));

        ArrayList<ImageIcon> bot_marche_droite = new ArrayList<ImageIcon>();
        bot_marche_droite.add(chargerIcone("Images/smick_ca.png", 32, 34, 32, 32));
        bot_marche_droite.add(chargerIcone("Images/smick_ca.png", 0, 34, 32, 32));

        ArrayList<ImageIcon> bot_monter = new ArrayList<ImageIcon>();
        bot_monter.add(chargerIcone("Images/smick_ca.png", 0, 96, 32, 32));
        bot_monter.add(chargerIcone("Images/smick_ca.png", 32, 96, 32, 32));

        ArrayList<ImageIcon> bot_tomber = new ArrayList<ImageIcon>();
        bot_tomber.add(chargerIcone("Images/smick_ca.png", 64, 96, 32, 32));
        bot_tomber.add(chargerIcone("Images/smick_ca.png", 96, 96, 32, 32));

        ArrayList<ImageIcon> bot_attend = new ArrayList<ImageIcon>();
        bot_attend.add(chargerIcone("Images/smick_ca.png", 0, 64, 32, 32));
        bot_attend.add(chargerIcone("Images/smick_ca.png", 32, 64, 32, 32));

        for (int j = 0; j < size_bot ; j++) {
            SpritesEntiteDynamique s = new SpritesEntiteDynamique(bot_marche_droite,bot_marche_gauche, bot_monter, bot_tomber, bot_attend);
            sprites_bot_list.add(s);
        }
        sprite_bot = sprites_bot_list.listIterator();


    }

    private void placerLesComposantsGraphiques() {
        setTitle("Gyromite");
        setSize(1200, 600+60);
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
                    EntiteDynamique e = jeu.getHector();
                    Direction d = Controle4Directions.getInstance().getDirection();

                    if (e.getAncienne_entite() != null && e.getAncienne_entite().peutPermettreDeMonterDescendre()) d = Direction.haut;
                    if (e.regarderDansLaDirection(Direction.bas) == null) d = Direction.bas;
                    if (d == Direction.haut && e.regarderDansLaDirection(Direction.bas).peutServirDeSupport()) d = null;
                    tabJLabel[x][y].setIcon(sprite_hero.get_sprite(d));
                    /** J'ai modifié ton code et j'ai utilisé les sprites je te laisse voir si c'est bon pour toi pour supprimer ton code
                    if ((jeu.getGrille()[x][y - 1] instanceof Corde) || (jeu.getGrille()[x][y + 1] instanceof Corde)) tabJLabel[x][y].setIcon(icoHeroCorde);
                    else {
                        if (d == Direction.gauche) tabJLabel[x][y].setIcon(icoHeroGauche);
                        else if (d == Direction.droite) tabJLabel[x][y].setIcon(icoHeroDroite);
                        else tabJLabel[x][y].setIcon(icoHeroNeutre);
                    }

                    // si transparence : images avec canal alpha + dessins manuels (voir ci-dessous + créer composant qui redéfinie paint(Graphics g)), se documenter
                    BufferedImage bi = getImage("Images/smick.png", 0, 0, 20, 20);
                    tabJLabel[x][y].getGraphics().drawImage(bi, 0, 0, null); */

                } else if (jeu.getGrille()[x][y] instanceof Bot) {
                    EntiteDynamique e = (EntiteDynamique) jeu.objetALaPosition(new Point(x,y));

                    Direction d = e.getDirectionCourante();
                    if (e.getAncienne_entite() != null && e.getAncienne_entite().peutPermettreDeMonterDescendre()) d = Direction.haut;

                    // Si vide en dessous du sprites alors icone tomber
                    if (e.regarderDansLaDirection(Direction.bas) == null) d = Direction.bas;

                    if (sprite_bot.hasNext() == false) {
                        while (sprite_bot.hasPrevious()) {
                            sprite_bot.previous();
                        }
                    }
                    tabJLabel[x][y].setIcon(sprite_bot.next().get_sprite(d));

                } else if (jeu.getGrille()[x][y] instanceof Mur) {
                    tabJLabel[x][y].setIcon(icoBedrock);
                } else if (jeu.getGrille()[x][y] instanceof Colonne) {
                    tabJLabel[x][y].setIcon(icoColonne);
                    // Si la case au dessus de x,y different de colonne afficher l'icon du haut de la colonne
                    if (!(jeu.getGrille()[x][y - 1] instanceof Colonne)) {
                        if (jeu.getGrille()[x-1][y] instanceof Platform || jeu.getGrille()[x+1][y] instanceof Platform){
                            tabJLabel[x][y].setIcon(icoColonneHautAttacher);
                        }else tabJLabel[x][y].setIcon(icoColonneHaut);
                        // Si la case au dessous de x,y different de colonne afficher l'icon du bas de la colonne
                    } else if (!(jeu.getGrille()[x][y + 1] instanceof Colonne)) {
                        if (jeu.getGrille()[x-1][y] instanceof Platform || jeu.getGrille()[x+1][y] instanceof Platform){
                            tabJLabel[x][y].setIcon(icoColonneBasAttacher);
                        }else tabJLabel[x][y].setIcon(icoColonneBas);
                        // Sinon afficher l'icon standard de la colonne
                    } else {
                        tabJLabel[x][y].setIcon(icoColonne);
                    }
                } else if (jeu.getGrille()[x][y] instanceof Platform) {
                    if ((x-1>0) && (y-1>0) && (y+1<jeu.SIZE_Y) && jeu.getGrille()[x-1][y] instanceof Colonne && (!(jeu.getGrille()[x-1][y+1] instanceof Colonne) || !(jeu.getGrille()[x-1][y-1] instanceof Colonne)))
                    {tabJLabel[x][y].setIcon(icoPlatformColoneDroite);}
                    else if ((x+1<jeu.SIZE_X) && (y-1>0) && (y+1 < jeu.SIZE_Y) && jeu.getGrille()[x+1][y] instanceof Colonne && (!(jeu.getGrille()[x+1][y + 1] instanceof Colonne) || !(jeu.getGrille()[x+1][y-1] instanceof Colonne)))
                    {tabJLabel[x][y].setIcon(icoPlatformColoneGauche);}
                    else tabJLabel[x][y].setIcon(icoPlatform);
                } else if (jeu.getGrille()[x][y] instanceof PlatformV) {
                    tabJLabel[x][y].setIcon(icoPlatformV);
                } else if (jeu.getGrille()[x][y] instanceof Bombe) {
                    tabJLabel[x][y].setIcon(next_sprite_Bomb(current_sprite_bomb));
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

        return new ImageIcon(image.getScaledInstance(1200/sizeX, 600/sizeY, java.awt.Image.SCALE_SMOOTH));
    }

    // chargement d'une sous partie de l'image
    private ImageIcon chargerIcone(String urlIcone, int x, int y, int w, int h) {
        // charger une sous partie de l'image à partir de ses coordonnées dans urlIcone
        BufferedImage bi = getSubImage(urlIcone, x, y, w, h);
        // adapter la taille de l'image a la taille du composant (ici : 20x20)
        return new ImageIcon(bi.getScaledInstance(1200/sizeX, 600/sizeY, java.awt.Image.SCALE_SMOOTH));
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
        JPanel grille = new JPanel(new GridLayout(sizeY, sizeX));
        grille.setPreferredSize(new Dimension(sizeX, 900));

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


    private ImageIcon next_sprite_Bomb(int current_sprite) {
        ImageIcon next;

        switch (current_sprite) {
            case 3 -> current_sprite = 0;
            default -> {
                current_sprite += 1;
            }
        }
        current_sprite_bomb = current_sprite;
        next = icoBombe[current_sprite_bomb];

        return next;
    }

}
