package VueControleur;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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

    private JFrame menuPrincipal;

    private JLabel levelOneHighScore;
    private JLabel levelTwoHighScore;
    private JLabel levelThreeHighScore;
    private JLabel levelFourHighScore;

    private JLabel levelOneScore;
    private JLabel levelTwoScore;
    private JLabel levelThreeScore;
    private JLabel levelFourScore;

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
    private ImageIcon icoLevel1;
    private ImageIcon icoLevel2;
    private ImageIcon icoLevel3;
    private ImageIcon icoLevel4;

    private ImageIcon icoRadis;
    private ImageIcon icoSleep;


    private ImageIcon[] icoBombe = new ImageIcon[4];
    private int current_sprite_bomb;


    private JLabel[][] tabJLabel; // cases graphiques (au moment du rafraichissement, chaque case va être associée à une icône, suivant ce qui est présent dans le modèle)

    private JLabel menu_label; // Label en haut
    private JLabel time_label; // Label droite du menu
    private JLabel score_label; // Label gauche du menu

    private ListIterator<SpritesEntiteDynamique> sprite_bot;
    private SpritesEntiteDynamique sprite_hero;
    private ListIterator<SpritesBomb> sprite_bomb;

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
                    case KeyEvent.VK_ESCAPE : jeu.SetTimeLeft(0); break;

                    /* J'ai volontairement choisis de ne pas faire de classe Controle Radis, et de ne pas faire de getinstance ...
                    * Car rien ne sert de compliquer le code. Lorsque le hero prend le radis il se supprime et un autre radis
                    * est créée dès qu'il le repose. Les radis sont tous les mêmes et n'ont aucune perspectives d'évolutions
                    * Ils n'ont également aucun déplacement. ce sont des objets fixe */
                    case KeyEvent.VK_E : jeu.pose_radis(Direction.gauche); break;
                    case KeyEvent.VK_R : jeu.pose_radis(Direction.droite); break;
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
        hero_monter.add(chargerIcone("Images/player_ca_gauche.png", 0, 88, 32, 46));
        hero_monter.add(chargerIcone("Images/player_ca_gauche.png", 32, 88, 32, 46));

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
        
        // Icon Level 1
        try {
            icoLevel1 = new ImageIcon(ImageIO.read(new File("Images/Level1.png")));
            icoLevel2 = new ImageIcon(ImageIO.read(new File("Images/Level2.png")));
            icoLevel3 = new ImageIcon(ImageIO.read(new File("Images/Level3.png")));
            icoLevel4 = new ImageIcon(ImageIO.read(new File("Images/Level4.png")));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        //Image img = icoLevel1.getImage();  
        //Image newimg = img.getScaledInstance(450, 300, java.awt.Image.SCALE_SMOOTH);
        //icoLevel1 = new ImageIcon(newimg);

        /* SpriteSheet de la bombe */
        ArrayList<SpritesBomb> sprites_bomb_list = new ArrayList<SpritesBomb>();
        int size_bomb = 7;
        ArrayList<ImageIcon> bomb_attente = new ArrayList<ImageIcon>();
        bomb_attente.add(chargerIcone("Images/bomb_ca.png", 8, 0, 48, 48));
        bomb_attente.add(chargerIcone("Images/bomb_ca.png", 72, 0, 48, 48));
        bomb_attente.add(chargerIcone("Images/bomb_ca.png", 136, 0, 48, 48));
        bomb_attente.add(chargerIcone( "Images/bomb_ca.png", 200, 0, 48, 48));

        ArrayList<ImageIcon> bomb_explose = new ArrayList<ImageIcon>();
        bomb_explose.add(chargerIcone("Images/bomb_ca.png", 8, 64, 48, 48));
        bomb_explose.add(chargerIcone("Images/bomb_ca.png", 72, 64, 48, 48));
        bomb_explose.add(chargerIcone("Images/bomb_ca.png", 136, 64, 48, 48));
        bomb_explose.add(chargerIcone( "Images/bomb_ca.png", 200, 64, 48, 48));
        for (int i = 0; i < size_bomb ; i++) {
            SpritesBomb b = new SpritesBomb(bomb_attente,bomb_explose);
            sprites_bomb_list.add(b);
        }
        sprite_bomb = sprites_bomb_list.listIterator();

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

        icoRadis = chargerIcone("Images/sprites.png", 72, 254, 16, 16);
        icoSleep = chargerIcone("Images/sprites.png", 70, 115, 19, 19);

    }

    private void placerLesComposantsGraphiques() {
        setTitle("Gyromite");
        setSize(1200, 600+60);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // permet de terminer l'application à la fermeture de la fenêtre

        menuPrincipal = new JFrame();
        menuPrincipal.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // permet de terminer
        menuPrincipal.setTitle("Menu");
        menuPrincipal.setSize(1200, 860);

        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new GridLayout(2, 2));

        addButtons(menuPanel);

        menuPrincipal.add(menuPanel);

        /* CentralPanel of the game */
        JPanel centralPanel = new JPanel();
        centralPanel.setLayout(new BorderLayout());

        /* BarMenu (Score + Time) and GameGrid (Hero + smicks + ...) */
        JComponent barMenu = component_barMenu();
        JComponent grilleJLabels = component_gameBoard(); // grilleJLabels va contenir les cases graphiques et les positionner sous la forme d'une grille

        /* Add the BarMenu and the GameGrid to the centralPanel */
        centralPanel.add(barMenu, BorderLayout.NORTH);
        centralPanel.add(grilleJLabels, BorderLayout.CENTER);
        add(centralPanel);

    }


    /**
     * Il y a une grille du côté du modèle ( jeu.getGrille() ) et une grille du côté de la vue (tabJLabel)
     */
    private void mettreAJourAffichageJeu() {
        ArrayList<Point> ico_sleep_pos = new ArrayList<Point>();

        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {

                if (jeu.getGrille()[x][y] instanceof Heros) { // si la grille du modèle contient un Pacman, on associe l'icône Pacman du côté de la vue
                    EntiteDynamique e = jeu.getHector();
                    Direction d = Controle4Directions.getInstance().getDirection();

                    if (d == Direction.haut || d == Direction.bas) {
                        Entite entite = e.regarderDansLaDirection(Direction.bas);
                        if (entite != null && entite.peutServirDeSupport()) {
                            d = null;
                        }
                    }
                    if (e.getAncienne_entite() != null && e.getAncienne_entite().peutPermettreDeMonterDescendre()) d = Direction.haut;
                    if (e.regarderDansLaDirection(Direction.bas) == null) d = Direction.bas;
                    tabJLabel[x][y].setIcon(sprite_hero.get_sprite(d));

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
                    if(((Bot) e).isDistrait()) {
                        tabJLabel[x][y].setIcon(sprite_bot.next().get_sprite(null));
                        ico_sleep_pos.add(new Point(x,y-1));
                    } else {
                        tabJLabel[x][y].setIcon(sprite_bot.next().get_sprite(d));
                    }

                } else if (jeu.getGrille()[x][y] instanceof Mur) {
                    tabJLabel[x][y].setIcon(icoBedrock);
                } else if (jeu.getGrille()[x][y] instanceof Colonne) {
                    tabJLabel[x][y].setIcon(icoColonne);
                    // Si la case au dessus de x,y different de colonne afficher l'icon du haut de la colonne
                    if ((y-1>=0) && !(jeu.getGrille()[x][y - 1] instanceof Colonne)) {
                        if ((x-1>0) && jeu.getGrille()[x-1][y] instanceof Platform || jeu.getGrille()[x+1][y] instanceof Platform){
                            tabJLabel[x][y].setIcon(icoColonneHautAttacher);
                        }else tabJLabel[x][y].setIcon(icoColonneHaut);
                        // Si la case au dessous de x,y different de colonne afficher l'icon du bas de la colonne
                    } else if ((y+1<Jeu.SIZE_Y) && !(jeu.getGrille()[x][y + 1] instanceof Colonne)) {
                        if (jeu.getGrille()[x-1][y] instanceof Platform || jeu.getGrille()[x+1][y] instanceof Platform){
                            tabJLabel[x][y].setIcon(icoColonneBasAttacher);
                        }else tabJLabel[x][y].setIcon(icoColonneBas);
                        // Sinon afficher l'icon standard de la colonne
                    } else {
                        tabJLabel[x][y].setIcon(icoColonne);
                    }
                } else if (jeu.getGrille()[x][y] instanceof Platform) {
                    if ((x-1>0) && (y-1>0) && (y+1<Jeu.SIZE_Y) && jeu.getGrille()[x-1][y] instanceof Colonne && (!(jeu.getGrille()[x-1][y+1] instanceof Colonne) || !(jeu.getGrille()[x-1][y-1] instanceof Colonne)))
                    {tabJLabel[x][y].setIcon(icoPlatformColoneDroite);}
                    else if ((x+1<Jeu.SIZE_X) && (y-1>0) && (y+1 < Jeu.SIZE_Y) && jeu.getGrille()[x+1][y] instanceof Colonne && (!(jeu.getGrille()[x+1][y + 1] instanceof Colonne) || !(jeu.getGrille()[x+1][y-1] instanceof Colonne)))
                    {tabJLabel[x][y].setIcon(icoPlatformColoneGauche);}
                    else tabJLabel[x][y].setIcon(icoPlatform);
                } else if (jeu.getGrille()[x][y] instanceof PlatformV) {
                    tabJLabel[x][y].setIcon(icoPlatformV);
                } else if (jeu.getGrille()[x][y] instanceof Bombe) {
                    // get_sprite() : par défaut attente
                    if (sprite_bomb.hasNext() == false) {
                        while (sprite_bomb.hasPrevious()) {
                            sprite_bomb.previous();
                        }
                    }
                    tabJLabel[x][y].setIcon(sprite_bomb.next().get_sprite());
                } else if (jeu.getGrille()[x][y] instanceof Corde) {
                    tabJLabel[x][y].setIcon(icoCorde);
                }else if (jeu.getGrille()[x][y] instanceof Radis) {
                        tabJLabel[x][y].setIcon(icoRadis);
                } else {
                    tabJLabel[x][y].setIcon(icoVide);
                    for (Point i : ico_sleep_pos) {
                        tabJLabel[i.x][i.y].setIcon(icoSleep);
                    }
                }
            }
        }
    }

    /**
     * Update the menu layout
     */
    private void mettreAJourAffichageMenu() {
        time_label.setText(String.valueOf(jeu.GetTimeLeft()));
        score_label.setText(String.valueOf(jeu.GetScore()) + "\tBest: " +  getHighScore(jeu.level));
    }

    private void mettreAJourAffichageMenuPrincipal() {
        levelOneHighScore.setText(getHighScore(1));
        levelTwoHighScore.setText(getHighScore(2));
        levelThreeHighScore.setText(getHighScore(3));
        levelFourHighScore.setText(getHighScore(4));
        switch(jeu.level) {
            case 1:
                levelOneScore.setText(String.valueOf(jeu.getLastScore()));
                break;
            case 2:
                levelTwoScore.setText(String.valueOf(jeu.getLastScore()));
                break;
            case 3:
                levelThreeScore.setText(String.valueOf(jeu.getLastScore()));
                break;
            case 4:
                levelFourScore.setText(String.valueOf(jeu.getLastScore()));
                break;
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        //mettreAJourAffichage();

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {

                if(jeu.checkEnd()){
                    System.out.println("Game End");
                    jeu.reset();
                    mettreAJourAffichageMenuPrincipal();
                    menuPrincipal.setVisible(true);
                }
                else {
                    if (jeu.isOn){
                        /* Increase time */
                        jeu.increase_TimeLeft();

                        /* Initialization and Add father Panel (father layout) */
                        mettreAJourAffichageJeu();
                        mettreAJourAffichageMenu();

                    }
                }
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
        String Score_And_HScore = String.valueOf(jeu.GetScore()) + "\tBest: " +  getHighScore(jeu.level);
        score_label = new JLabel(Score_And_HScore);

        score_label.setForeground(Color.white);
        score_label.setFont(new Font(Font.MONOSPACED, score_label.getFont().getStyle(), 22));
        score_label.setBorder(BorderFactory.createEmptyBorder(0, 290, 0, 0));
        menu_label.add(score_label, BorderLayout.CENTER);

        /* Initialize the Time label */
        time_label = new JLabel(String.valueOf(jeu.GetTimeLeft()));
        time_label.setFont(new Font(Font.MONOSPACED, score_label.getFont().getStyle(), 22));
        time_label.setForeground(Color.green);
        time_label.setBorder(BorderFactory.createEmptyBorder(2, 300, 0, 0));
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


    public void setMenuVisible(boolean visible) {
        menuPrincipal.setVisible(visible);
    }

    private void addButtons(JPanel panel){

                // Level 1 Button
                JButton levelOneButton = new JButton(icoLevel1);
                levelOneButton.setBackground(Color.BLACK);
                levelOneButton.setFocusable(false);
                levelOneButton.setBorder(BorderFactory.createEtchedBorder());
                levelOneButton.addActionListener(new ActionListener(){
                    @Override
                    public void actionPerformed(ActionEvent e){
                        menuPrincipal.setVisible(false);
                        jeu.loadLevel("Levels/01.csv");
                        jeu.level = 1;
                        setVisible(true);
                    }
                });

                levelOneButton.addMouseListener(new MouseAdapter(){
                    public void mouseEntered(MouseEvent e){
                        levelOneButton.setBackground(Color.RED);
                    }
                    public void mouseExited(MouseEvent e){
                        levelOneButton.setBackground(Color.BLACK);
                    }
                });
                // Level 1 High Score
                levelOneHighScore = new JLabel(getHighScore(1));
                levelOneHighScore.setForeground(Color.WHITE);
                levelOneHighScore.setFont(new Font(Font.MONOSPACED, levelOneHighScore.getFont().getStyle(), 25));
                levelOneHighScore.setBorder(BorderFactory.createEmptyBorder(280, 440, 0, 0));
                levelOneButton.add(levelOneHighScore);
                // Level 1 Score
                levelOneScore = new JLabel(" ");
                levelOneScore.setForeground(Color.WHITE);
                levelOneScore.setFont(new Font(Font.MONOSPACED, levelOneScore.getFont().getStyle(), 25));
                levelOneScore.setBorder(BorderFactory.createEmptyBorder(280, 200, 0, 0));
                levelOneButton.add(levelOneScore);
                // Add level 1 to panel
                panel.add(levelOneButton);
                
                // Level 2 Button
                JButton levelTwoButton = new JButton(icoLevel2);
                levelTwoButton.setBackground(Color.BLACK);
                levelTwoButton.setFocusable(false);
                levelTwoButton.setBorder(BorderFactory.createEtchedBorder());
                levelTwoButton.addActionListener(new ActionListener(){
                    @Override
                    public void actionPerformed(ActionEvent e){
                        menuPrincipal.setVisible(false);
                        jeu.loadLevel("Levels/02.csv");
                        jeu.level = 2;
                        setVisible(true);
                    }
                });

                levelTwoButton.addMouseListener(new MouseAdapter(){
                    public void mouseEntered(MouseEvent e){
                        levelTwoButton.setBackground(Color.RED);
                    }
                    public void mouseExited(MouseEvent e){
                        levelTwoButton.setBackground(Color.BLACK);
                    }
                });
                // Level 2 High Score
                levelTwoHighScore = new JLabel(getHighScore(2));
                levelTwoHighScore.setForeground(Color.WHITE);
                levelTwoHighScore.setFont(new Font(Font.MONOSPACED, levelTwoHighScore.getFont().getStyle(), 25));
                levelTwoHighScore.setBorder(BorderFactory.createEmptyBorder(280, 440, 0, 0));
                levelTwoButton.add(levelTwoHighScore);
                // Level 2 Score
                levelTwoScore = new JLabel(" ");
                levelTwoScore.setForeground(Color.WHITE);
                levelTwoScore.setFont(new Font(Font.MONOSPACED, levelTwoScore.getFont().getStyle(), 25));
                levelTwoScore.setBorder(BorderFactory.createEmptyBorder(280, 200, 0, 0));
                levelTwoButton.add(levelTwoScore);
                // Add level 2 button to panel
                panel.add(levelTwoButton);
                
                // Level 3 Button
                JButton levelThreeButton = new JButton(icoLevel3);
                levelThreeButton.setBackground(Color.BLACK);
                levelThreeButton.setForeground(Color.BLACK);
                levelThreeButton.setFocusable(false);
                levelThreeButton.setBorder(BorderFactory.createEtchedBorder());
                levelThreeButton.addActionListener(new ActionListener(){
                    @Override
                    public void actionPerformed(ActionEvent e){
                        menuPrincipal.setVisible(false);
                        jeu.loadLevel("Levels/03.csv");
                        jeu.level = 3;
                        setVisible(true);
                    }
                });

                levelThreeButton.addMouseListener(new MouseAdapter(){
                    public void mouseEntered(MouseEvent e){
                        levelThreeButton.setBackground(Color.RED);
                    }
                    public void mouseExited(MouseEvent e){
                        levelThreeButton.setBackground(Color.BLACK);
                    }
                });
                // Level 3 High Score
                levelThreeHighScore = new JLabel(getHighScore(3));
                levelThreeHighScore.setForeground(Color.WHITE);
                levelThreeHighScore.setFont(new Font(Font.MONOSPACED, levelThreeHighScore.getFont().getStyle(), 25));
                levelThreeHighScore.setBorder(BorderFactory.createEmptyBorder(280, 440, 0, 0));
                levelThreeButton.add(levelThreeHighScore);
                // Level 3 Score
                levelThreeScore = new JLabel(" ");
                levelThreeScore.setForeground(Color.WHITE);
                levelThreeScore.setFont(new Font(Font.MONOSPACED, levelThreeScore.getFont().getStyle(), 25));
                levelThreeScore.setBorder(BorderFactory.createEmptyBorder(280, 200, 0, 0));
                levelThreeButton.add(levelThreeScore);
                // Add level 3 button to panel
                panel.add(levelThreeButton);
        
                // Level 4 Button
                JButton levelFourButton = new JButton(icoLevel4);
                levelFourButton.setBackground(Color.BLACK);
                levelFourButton.setForeground(Color.BLACK);
                levelFourButton.setFocusable(false);
                levelFourButton.setBorder(BorderFactory.createEtchedBorder());
                levelFourButton.addActionListener(new ActionListener(){
                    @Override
                    public void actionPerformed(ActionEvent e){
                        menuPrincipal.setVisible(false);
                        jeu.loadLevel("Levels/04.csv");
                        jeu.level = 4;
                        setVisible(true);
                    }
                });

                levelFourButton.addMouseListener(new MouseAdapter(){
                    public void mouseEntered(MouseEvent e){
                        levelFourButton.setBackground(Color.RED);
                    }
                    public void mouseExited(MouseEvent e){
                        levelFourButton.setBackground(Color.BLACK);
                    }
                });

                // Level 4 High Score
                levelFourHighScore = new JLabel(getHighScore(4));
                levelFourHighScore.setForeground(Color.WHITE);
                levelFourHighScore.setFont(new Font(Font.MONOSPACED, levelFourHighScore.getFont().getStyle(), 25));
                levelFourHighScore.setBorder(BorderFactory.createEmptyBorder(280, 440, 0, 0));
                levelFourButton.add(levelFourHighScore);
                // Level 4 Score
                levelFourScore = new JLabel(" ");
                levelFourScore.setForeground(Color.WHITE);
                levelFourScore.setFont(new Font(Font.MONOSPACED, levelFourScore.getFont().getStyle(), 25));
                levelFourScore.setBorder(BorderFactory.createEmptyBorder(280, 200, 0, 0));
                levelFourButton.add(levelFourScore);
                // Add button four to panel
                panel.add(levelFourButton);
    }

    private String getHighScore(int level){
        String score = "0";
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader("data/scores/score" + String.valueOf(level) + ".txt"));
            score = reader.readLine();
            reader.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return score;
    }

}
