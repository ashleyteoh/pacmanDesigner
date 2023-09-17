package src;

import src.mapeditor.editor.CheckerAdapter;
import src.mapeditor.editor.Controller;
import src.mapeditor.editor.GameCheckerAdapter;
import src.utility.GameCallback;
import src.utility.PropertiesLoader;

import java.io.File;
import java.util.Properties;

public class Driver {
    public static final String PROPERTIES_PATH = "pacman/properties/test.properties";

    /**
     * Starting point
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (args.length > 0) {
            File filename = new File(args[0]);
            // open editor with directory
            if (filename.isDirectory()) {
                // check game folder
                CheckerAdapter checker = new GameCheckerAdapter(filename);
                boolean gameCheck = checker.checkFile();
                if (gameCheck == true) { // game check passed
                    runGameDriver(PROPERTIES_PATH, filename);
                }
                else {
                    new Controller();
                }

            }
            // open editor with given map
            else if (filename.getName().endsWith(".xml")) {
                File file = new File(args[0]);
                new Controller(file);
            }
        }
        else {
            // open editor with no map
            new Controller();
        }
    }

    public static void runGameDriver(String propertiesPath, File filename) {
        final Properties properties = PropertiesLoader.loadPropertiesFile(PROPERTIES_PATH);
        GameCallback gameCallback = new GameCallback();
        Game game = new Game(gameCallback, properties, filename);
        // Level checking failed or specific map ended
        File failedFile = game.act(filename);
        if (failedFile != null) {
            new Controller(failedFile);
        } else {
            new Controller();
        }
    }



}
