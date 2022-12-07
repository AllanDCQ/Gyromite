package modele.deplacements;
import modele.plateau.Bot;
import modele.plateau.Entite;
import modele.plateau.EntiteDynamique;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class IA extends RealisateurDeDeplacement {

    private int distanceVision = 3;

    private static IA iad;

    public static IA getInstance() {
        if (iad == null) {
            iad = new IA();
        }
        return iad;
    }

    public static IA reset(){
        iad = new IA();
        return iad;
    }

    protected boolean realiserDeplacement() {

        boolean ret = true;
        boolean hero_located = false;
        Direction ancienneDirection;

        for (EntiteDynamique e : lstEntitesDynamiques) {
            // e instanceof Bot utilisé simplement pour la sécurité en cas de modification des classes
            if (e instanceof Bot && ((Bot) e).isDistrait()) {
                ((Bot) e).decreaseSleepTime();
                ret = false;
            } else {
                ancienneDirection = e.getDirectionCourante();

                /* Champs de vision autour : Devant / Derriere / Haut / Bas */
                ArrayList<Direction> lstEntitesArroundDirection = new ArrayList<Direction>();
                lstEntitesArroundDirection.addAll((Arrays.asList(ancienneDirection, directionInverse(ancienneDirection), Direction.haut, Direction.bas)));


                ArrayList<Entite> lstEntitesArround = new ArrayList<Entite>();
                for (int i = 0; i < 4; i++) {
                    lstEntitesArround.add(e.regarderDansLaDirection(lstEntitesArroundDirection.get(i)));
                }

                /* Champs de vision à l'avant de l'IA */
                ArrayList<Entite> lstEntitesCibles = new ArrayList<Entite>();
                for (int i = 1; i < distanceVision; i++) {
                    lstEntitesCibles.add(e.regarderDansLaDirectionDistance(ancienneDirection, i));
                }


                /* Recherche de Hector dans le champs vision autour de l'IA (devant et derriere) */
                for (int i = 0; i < 2; i++) {
                    Entite entiteArround = lstEntitesArround.get(i);
                    Direction entiteArroundDirection = lstEntitesArroundDirection.get(i);

                    if (entiteArround != null && entiteArround.equals(e.getHero())) {
                        if (ancienneDirection != entiteArroundDirection) {
                            ancienneDirection = entiteArroundDirection;
                            // Ne fait que se retourner et n'avance pas
                        } else {
                            e.avancerDirectionChoisie(ancienneDirection);
                        }
                        hero_located = true;
                    }
                }

                /* Recherche de Hector dans le champs de vision à l'avant de l'IA */
                if (lstEntitesArround.get(0) == null || lstEntitesArround.get(0).peutEtreEcrase()) { // Si l'avant peut être écrasé
                    for (Entite entitesCibles : lstEntitesCibles) {
                        if (entitesCibles != null && entitesCibles.equals(e.getHero())) {
                            Entite entiteBasDevantDerriere = e.regarderDansLaDirectionDouble(Direction.bas, lstEntitesArroundDirection.get(0));
                            if(entiteBasDevantDerriere != null && entiteBasDevantDerriere.peutServirDeSupport()) {
                                e.avancerDirectionChoisie(ancienneDirection);
                                hero_located = true;
                            }

                        }
                    }
                }

                e.setDirectionCourante(ancienneDirection);

                /* Si le hero n'a pas été repéré alors déplacement */
                if (!hero_located) {
                    deplacementIA(e, lstEntitesArroundDirection, lstEntitesArround);
                }

            }
        }
        return ret;
    }

    private Direction directionInverse(Direction d){
        return switch (d) {
            case gauche -> Direction.droite;
            case droite -> Direction.gauche;
            case bas -> Direction.haut;
            case haut -> Direction.bas;
        };
    }

    private void deplacementIA(EntiteDynamique bot, ArrayList<Direction> lstEntitesArroundDirection, ArrayList<Entite> lstEntitesArround) {

        // Rappel : Champs de vision autour : Devant(0) / Derriere(1) / Haut(2) / Bas(3)
        // Priorité à la montée.
        List<Entite> entiteBasDevantDerriere = new ArrayList<Entite>();
        entiteBasDevantDerriere.add(bot.regarderDansLaDirectionDouble(Direction.bas, lstEntitesArroundDirection.get(0)));
        entiteBasDevantDerriere.add(bot.regarderDansLaDirectionDouble(Direction.bas, lstEntitesArroundDirection.get(1)));

        List<Integer> lstPriorities = new ArrayList<Integer>();

        if(bot.getDirectionUpward()) {
            lstPriorities.addAll((Arrays.asList(0, 2, 1, 3)));
        } else if(bot.getDirectionDescent()) {
            lstPriorities.addAll((Arrays.asList(0, 3, 1, 2)));
        } else {
            lstPriorities.addAll((Arrays.asList(2, 3, 0, 1)));
        }

        boolean endBoucle = false;
        for (Integer i :lstPriorities ) {
            switch (i) {
                case 0:
                case 1:
                    if(lstEntitesArround.get(i) != null && lstEntitesArround.get(i).peutPermettreDeMonterDescendre()) {
                        if(bot.avancerDirectionChoisie(lstEntitesArroundDirection.get(i))){
                            bot.setDirectionDescent(false);
                            bot.setDirectionUpward(false);
                            bot.setDirectionCourante(lstEntitesArroundDirection.get(i));
                        }

                        endBoucle = true;

                    } else if(entiteBasDevantDerriere.get(i) != null && entiteBasDevantDerriere.get(i).peutServirDeSupport()) {
                        if(lstEntitesArround.get(i) == null || (lstEntitesArround.get(i) != null && lstEntitesArround.get(i).peutEtreEcrase()) ) {
                            if(bot.avancerDirectionChoisie(lstEntitesArroundDirection.get(i))) {
                                bot.setDirectionDescent(false);
                                bot.setDirectionUpward(false);
                                bot.setDirectionCourante(lstEntitesArroundDirection.get(i));
                            }

                            endBoucle = true;
                        }
                    }
                    break;
                case 2:
                    if (lstEntitesArround.get(2) != null && lstEntitesArround.get(2).peutPermettreDeMonterDescendre()) {
                        if(bot.avancerDirectionChoisie(lstEntitesArroundDirection.get(2))) {
                            bot.setDirectionDescent(false);
                            bot.setDirectionUpward(true);
                        }
                        endBoucle = true;
                    }
                    break;
                case 3:
                    if (lstEntitesArround.get(3) != null && lstEntitesArround.get(3).peutPermettreDeMonterDescendre()) {
                        if (bot.avancerDirectionChoisie(lstEntitesArroundDirection.get(3))){
                            bot.setDirectionDescent(true);
                            bot.setDirectionUpward(false);
                        }
                        endBoucle = true;
                    }
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + i);
            }
            if(endBoucle) {
                break;
            }
        }
    }

}
