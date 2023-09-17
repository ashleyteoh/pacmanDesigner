package src.mapeditor.editor;

import ch.aplu.jgamegrid.Location;
import src.mapeditor.grid.Grid;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class LevelCheckerAdapter implements CheckerAdapter{

    MapCallback log = new MapCallback();
    ArrayList<Location> white ;
    ArrayList<Location> yellow ;
    ArrayList<Location> darkGray;
    ArrayList<Location> darkGold;
    Grid model;
    File file;
    public LevelCheckerAdapter(Grid model, File file) {
        white = new ArrayList<>();
        yellow = new ArrayList<>();
        darkGray = new ArrayList<>();
        darkGold = new ArrayList<>();
        this.model = model;
        this.file = file;

//        MapCallback log = new MapCallback();
    }

    @Override
    public boolean checkFile() {
        Location pacLoc = checkNumPacman();
        boolean portalCheck = checkNumPortals();

        if (pacLoc == null || !portalCheck) return false; // don't continue checking if before fails
        boolean numItem = checkGoldandPill();
        boolean access = checkItemAccess(pacLoc);

        return (numItem && access);

    }

    public boolean checkItemAccess(Location pacLoc) {

        boolean access = true;
//        char[][] mapCopy = new char[model.getWidth()][model.getHeight()];
        char mapCopy[][] = {{'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N'},
                {'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N'},
                {'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N'},
                {'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N'},
                {'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N'},
                {'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N'},
                {'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N'},
                {'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N'},
                {'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N'},
                {'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N'},
                {'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N'}
        };
        fillGrid(model, mapCopy, pacLoc.getX(), pacLoc.getY());
//        System.out.println(mapCopy);
//        for (int i = 0; i < 11; i++) {
//            for (int j = 0; j < 20; j++) {
//                System.out.print(mapCopy[i][j]);
//            }
//            System.out.println();
//        }
//        for (int i = 0; i < 11; i++) {
//            for (int j = 0; j < 20; j++) {
//                System.out.print(model.getTile(j, i));
//            }
//            System.out.println();
//        }
        ArrayList<Location> goldNotReach = new ArrayList<>();
        ArrayList<Location> pillNotReach = new ArrayList<>();
        for (int i = 0; i < 11; i++) {
            for (int j = 0; j < 20; j++) {
//                System.out.printf("%d, %d\n", i, j);
//                System.out.print(model.getTile(j , i));
//                System.out.println(mapCopy[i][j]);
                if (Objects.equals('N', mapCopy[i][j])) {
                    if (model.getTile(j, i) == 'c') {
                        pillNotReach.add(new Location(j + 1, i +1));
                    } else if (model.getTile(j, i) == 'd') {
                        goldNotReach.add(new Location(j+1, i+1));
                    }
                }
            }
        }
        if (pillNotReach.size() > 0) {
            log.itemNotReach(file.getName(), "Pill", pillNotReach);
            access = false;
        }

        if (goldNotReach.size() > 0) {
            log.itemNotReach(file.getName(), "Gold", goldNotReach);
            access = false;
        }
        return access;
    }

    public void fillGrid(Grid model, char[][] mapCopy, int x, int y) {
        if (x -1 < 0 || x > model.getWidth() || y -1 < 0 || y > model.getHeight())
            return;

        if (mapCopy[y - 1][x - 1] == 'Y') {
            return;
        }

        if (model.getTile(x - 1, y -1) != 'b') {
//            System.out.println(model.getTile(x- 1, y- 1));
//            System.out.printf("%d %d\n",x - 1, y - 1);

            mapCopy[y - 1][x- 1] = 'Y';

            char tile = model.getTile(x -1 , y - 1);
            Location tileLoc = new Location(x, y);
//            System.out.println("tileloc " + tileLoc);
            int pairIndex;
            if (tile == 'i') { //white portal
                if (tileLoc.equals(white.get(0))) {
                    pairIndex = 1;
                } else {
                    pairIndex = 0;
                }
//                System.out.println("white fills" + white.get(1));
                fillGrid(model, mapCopy, white.get(pairIndex).getX() , white.get(pairIndex).getY());
            }
            else if (tile == 'j') { // yellow portal
                if (tileLoc.equals(yellow.get(0))) {
                    pairIndex = 1;
                } else {
                    pairIndex = 0;
                }
                fillGrid(model, mapCopy, yellow.get(pairIndex).getX() , yellow.get(pairIndex).getY() );
            }
            else if (tile == 'k') {
                if (tileLoc.equals(darkGold.get(0))) {
                    pairIndex = 1;
                } else {
                    pairIndex = 0;
                }
                fillGrid(model, mapCopy, darkGold.get(pairIndex).getX() , darkGold.get(pairIndex).getY() );
            }
            else if (tile == 'l') {// dark gray
                if (tileLoc.equals(darkGray.get(0))) {
                    pairIndex = 1;
                } else {
                    pairIndex = 0;
                }
                fillGrid(model, mapCopy, darkGray.get(pairIndex).getX() , darkGray.get(pairIndex).getY() );
            }

            fillGrid(model, mapCopy, x + 1, y);
            fillGrid(model, mapCopy, x - 1, y);
            fillGrid(model, mapCopy, x, y + 1);
            fillGrid(model, mapCopy, x, y - 1);
        }
    }

    public boolean checkGoldandPill() {
        int numItem = 0;
        for (int x = 0; x < model.getWidth(); x++) {
            for (int y = 0; y < model.getHeight(); y++) {
                char tile = model.getTile(x, y);
                if (tile == 'd' || tile == 'c') {  //gold or pill
                    numItem++;
                    if (numItem > 2) return true;
                }
            }
        }

        if (numItem < 2) {
            log.numItemInvalid(file.getName());
            return false;
        }

        return true;
    }

    public boolean checkNumPortals() {
//        ArrayList<Location> white = new ArrayList<>();
//        ArrayList<Location> yellow = new ArrayList<>();
//        ArrayList<Location> darkGray = new ArrayList<>();
//        ArrayList<Location> darkGold = new ArrayList<>();
        boolean portalCheck = true;

        for (int x = 0; x < model.getWidth(); x++) {
            for (int y = 0; y < model.getHeight(); y++) {
                char tile = model.getTile(x, y);
                if (tile == 'i') {  //white tile
                    white.add(new Location(x + 1, y+1));
                }
                else if (tile == 'j') {
                    yellow.add(new Location(x+1, y+1));
                }
                else if (tile == 'k') {
                    darkGold.add(new Location(x+1, y+1));
                }
                else if (tile == 'l') {
                    darkGray.add(new Location(x+1, y+1));
                }
            }
        }

        if (white.size() != 2 && white.size() != 0) {
            log.portalInvalid(file.getName(), "White", white);
            portalCheck = false;
        }
        if (yellow.size() != 2 && yellow.size() != 0) {
            log.portalInvalid(file.getName(), "Yellow", yellow);
            portalCheck = false;
        }
        if (darkGold.size() != 2 && darkGold.size() != 0) {
            log.portalInvalid(file.getName(), "Dark Gold", darkGold);
            portalCheck = false;
        }
        if (darkGray.size() != 2 && darkGray.size() != 0) {
            log.portalInvalid(file.getName(), "Dark Gray", darkGray);
            portalCheck = false;
        }
        return portalCheck;

    }

    public Location checkNumPacman() {
        int numPacman = 0;
        ArrayList<Location> locations = new ArrayList<>();

        for (int x = 0; x < model.getWidth(); x++) {
            for (int y = 0; y < model.getHeight(); y++) {

                if (model.getTile(x, y) == 'f') {  //pacman
                    numPacman++;
                    locations.add(new Location(x+1, y+1));
                    if (numPacman > 1) {
                        log.pacmanInvalid(file.getName(), locations);
                        return null;
                    }
                }
            }
        }

        if (numPacman == 0) {
            log.pacmanInvalid(file.getName(),  null);
            return null;
        }
        return locations.get(0);
    }

}
