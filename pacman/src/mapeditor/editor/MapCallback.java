package src.mapeditor.editor;

import ch.aplu.jgamegrid.Location;
import src.Monster;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class MapCallback {

    private String logFilePath = "MapLog.txt";
    private FileWriter fileWriter = null;

    public MapCallback() {
        try {
            fileWriter = new FileWriter(new File(logFilePath));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void writeString(String str) {
        try {
            fileWriter.write(str);
            fileWriter.write("\n");
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void endOfGame(String gameResult) {
        writeString(gameResult);
    }


    public void pacmanInvalid(String filename, ArrayList<Location> locations) {
        String pacmanLocationString;
        if (locations == null) {
            pacmanLocationString = String.format("[%s - No start for PacMan]", filename);
        }
        else {
            pacmanLocationString = String.format("[%s - more than one start for Pacman: ", filename);
            for (Location loc : locations) {
                pacmanLocationString += loc.toString();
                pacmanLocationString += "; ";
            }
            pacmanLocationString = pacmanLocationString.substring(0, pacmanLocationString.length() - 2);
            pacmanLocationString += "]";
        }

        writeString(pacmanLocationString);
    }

    public void portalInvalid(String filename, String colour, ArrayList<Location> locations) {
        String portalString;

        portalString = String.format("[%s - portal %s count is not 2: ",filename, colour);
        for (Location loc : locations) {
            portalString += loc.toString();
            portalString += "; ";
        }
        portalString = portalString.substring(0, portalString.length() - 2);
        portalString += "]";

        writeString(portalString);
    }

    public void numItemInvalid(String filename) {
        String itemString = String.format("[%s - less than 2 Gold and Pill]", filename);
        writeString(itemString);
    }

    public void itemNotReach(String filename, String item, ArrayList<Location> locations) {
        String itemString = String.format("[%s - %s not accessible: ", filename, item);
        for (Location loc : locations) {
            itemString += loc;
            itemString += "; ";
        }
        itemString = itemString.substring(0, itemString.length() - 2);
        itemString += "]";
        writeString(itemString);
    }

    public void dirInvalid(String dirName) {
        String dirString = String.format("[%s - no maps found]", dirName);
        writeString(dirString);
    }

    public void dupNames(String dirName, ArrayList<String> names) {
        String dupString = String.format("[%s - multiple maps at same level: ", dirName);
        for (String name : names) {
            dupString += name;
            dupString += "; ";
        }
        dupString = dupString.substring(0, dupString.length() - 2);
        dupString += "]";
        writeString(dupString);
    }

}
