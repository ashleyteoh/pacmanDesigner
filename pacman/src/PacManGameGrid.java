// PacGrid.java
package src;

import ch.aplu.jgamegrid.*;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import src.mapeditor.editor.Tile;
import src.mapeditor.editor.TileManager;
import src.mapeditor.grid.Grid;
import src.mapeditor.grid.GridModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PacManGameGrid
{
  private List<Tile> tiles;
  private int nbHorzCells;
  private int nbVertCells;
  private Space[][] mazeArray;
  private Grid model;
  private ArrayList<Location> trollLoc = new ArrayList<>();
  private ArrayList<Location> TX5Loc = new ArrayList<>();
  private ArrayList<Location> portals = new ArrayList<>();
  private ArrayList<Location> whitePortals = new ArrayList<>();
  private ArrayList<Location> yellowPortals = new ArrayList<>();
  private ArrayList<Location> DGoldPortals = new ArrayList<>();

  private ArrayList<Location> DGreyPortals = new ArrayList<>();

  private ArrayList<ArrayList<Location>> allPortals = new ArrayList<>();
  //private ArrayList<Location> ortals = new ArrayList<>();


  private Location pacLoc;

  public PacManGameGrid(int nbHorzCells, int nbVertCells, File file)
  {
    this.nbHorzCells = nbHorzCells;
    this.nbVertCells = nbVertCells;
    mazeArray = new Space[nbVertCells][nbHorzCells];
    this.tiles = TileManager.getTilesFromFolder("pacman/data/");
    this.model = new GridModel(nbHorzCells, nbVertCells, tiles.get(0).getCharacter());


    String maze = loadMap(file);


    maze = maze.replace("\n", "");

    // Copy structure into integer array
    for (int i = 0; i < nbVertCells; i++)
    {
      for (int k = 0; k < nbHorzCells; k++) {
        Space space = toSpace(maze.charAt(nbHorzCells * i + k));
        mazeArray[i][k] = space;
      }
    }
  }

  public Grid getModel() {
    return model;
  }

  public Space getCell(Location location)
  {
    return mazeArray[location.y][location.x];
  }
  private Space toSpace(char c)
  {
    if (c == 'a')
      return Space.Empty;
    if (c == 'b')
      return Space.Wall;
    if (c == 'c')
      return Space.Pill;
    if (c == 'd')
      return Space.Gold;
    if (c == 'e')
      return Space.Ice;
    if (c == 'f')
      return Space.PacMan;
    if (c == 'g')
      return Space.Troll;
    if (c == 'h')
      return Space.TX5;
    if (c == 'i')
      return Space.WhitePortal;
    if (c == 'j')
      return Space.YellowPortal;
    if (c == 'k')
      return Space.DGoldPortal;
    if (c == 'l')
      return Space.DGrayPortal;
    return null;
  }
  private String loadMap(File file) {
      SAXBuilder builder = new SAXBuilder();
      try {
  //			JFileChooser chooser = new JFileChooser();
        File selectedFile = file;
        Document document;
  //      selectedFile = new File(file);
        if (selectedFile.canRead() && selectedFile.exists()) {
          document = (Document) builder.build(selectedFile);

          Element rootNode = document.getRootElement();

          List rows = rootNode.getChildren("row");
          for (int y = 0; y < rows.size(); y++) {
            Element cellsElem = (Element) rows.get(y);
            List cells = cellsElem.getChildren("cell");

            for (int x = 0; x < cells.size(); x++) {
              Element cell = (Element) cells.get(x);
              String cellValue = cell.getText();

              char tileNr = 'a';
              if (cellValue.equals("PathTile"))
                tileNr = 'a';
              else if (cellValue.equals("WallTile"))
                tileNr = 'b';
              else if (cellValue.equals("PillTile"))
                tileNr = 'c';
              else if (cellValue.equals("GoldTile"))
                tileNr = 'd';
              else if (cellValue.equals("IceTile"))
                tileNr = 'e';
              else if (cellValue.equals("PacTile")) {
                tileNr = 'f';
                pacLoc = new Location(x, y);
              }
              else if (cellValue.equals("TrollTile")) {
                tileNr = 'g';
                trollLoc.add(new Location(x, y));
              }
              else if (cellValue.equals("TX5Tile")) {
                tileNr = 'h';
                TX5Loc.add(new Location(x, y));
              }
              else if (cellValue.equals("PortalWhiteTile")) {
                tileNr = 'i';
                whitePortals.add(new Location(x, y));
              }
              else if (cellValue.equals("PortalYellowTile")) {
                tileNr = 'j';
                yellowPortals.add(new Location(x, y));
              }
              else if (cellValue.equals("PortalDarkGoldTile")) {
                tileNr = 'k';
                DGoldPortals.add(new Location(x, y));
              }
              else if (cellValue.equals("PortalDarkGrayTile")) {
                tileNr = 'l';
                DGreyPortals.add(new Location(x, y));
              }
              else
                tileNr = '0';

              model.setTile(x, y, tileNr);
            }
          }
        }

        allPortals.add(whitePortals);
        allPortals.add(yellowPortals);
        allPortals.add(DGoldPortals);
        allPortals.add(DGreyPortals);
        String mapString = model.getMapAsString();

        return mapString;


      } catch (Exception e) {
        e.printStackTrace();
      }
      return null;
    }

  public ArrayList<Location> getTrollLoc() {
    return trollLoc;
  }

  public Location getPacLoc() {
    return pacLoc;
  }

  public ArrayList<Location> getTX5Loc() {
    return TX5Loc;
  }
}
