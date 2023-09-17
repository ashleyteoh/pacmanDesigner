// PacMan.java
// Simple PacMan implementation
package src;

import ch.aplu.jgamegrid.*;
import src.mapeditor.editor.CheckerAdapter;
import src.mapeditor.editor.LevelCheckerAdapter;
import src.utility.GameCallback;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Properties;
import java.util.List;

public class Game extends GameGrid
{
  private final static int nbHorzCells = 20;
  private final static int nbVertCells = 11;
  protected PacManGameGrid grid;


  protected PacActor pacActor;

  private ArrayList<Monster> trolls = new ArrayList<Monster>();
  private ArrayList<Monster> tx5s = new ArrayList<Monster>();

  private ArrayList<Location> pillAndItemLocations = new ArrayList<Location>();
  private ArrayList<Actor> iceCubes = new ArrayList<Actor>();
  private ArrayList<Actor> goldPieces = new ArrayList<Actor>();
  private GameCallback gameCallback;
  private Properties properties;
  private int seed = 30006;


  private ArrayList<Location> whitePortals = new ArrayList<>();
  private ArrayList<Location> yellowPortals = new ArrayList<>();
  private ArrayList<Location> DGoldPortals = new ArrayList<>();
  private ArrayList<Location> DGreyPortals = new ArrayList<>();
  private ArrayList<ArrayList<Location>> allPortals = new ArrayList<>();

  private ArrayList<Actor> portals = new ArrayList<>();
  private ArrayList<Monster> monsters = new ArrayList<>();
  List<File> filePaths = new ArrayList<File>();

  public Game(GameCallback gameCallback, Properties properties, File filename)
  {
    //Setup game
    super(nbHorzCells, nbVertCells, 20, false);
    this.gameCallback = gameCallback;
    this.properties = properties;
    setSimulationPeriod(100);
    setTitle("[PacMan in the Multiverse]");



    if (filename.isDirectory()) {
      File[] files = filename.listFiles();
      assert files != null;
      for(File f : files) {
        if (f.getName().endsWith(".xml")) {
          filePaths.add(f);
        }
      }
      Collections.sort(filePaths);
    }
    else {
      filePaths.add(filename);
    }

  }

  public File act(File filename) {
    // Sequentially run each level in directory
    boolean lose = false;
    int i;
    for (i = 0; i < filePaths.size(); i++) {
      pacActor = PacActor.getInstance(this);
      pacActor.setNbPills(0);
      pacActor.setClosestPill(null);
      pacActor.setScore(0);

      //Setup for auto test
      pacActor.setAuto(Boolean.parseBoolean(properties.getProperty("PacMan.isAuto")));
      renewArrays();

      grid = new PacManGameGrid(nbHorzCells, nbVertCells, filePaths.get(i));
      CheckerAdapter checker = new LevelCheckerAdapter(grid.getModel(), filePaths.get(i));
      boolean check = checker.checkFile();
      if (!check) {
        return filePaths.get(i);
      }
      GGBackground bg = getBg();
      drawGrid(bg);

      //Setup Random seeds
      seed = Integer.parseInt(properties.getProperty("seed"));
      pacActor.setSeed(seed);
      addKeyRepeatListener(pacActor);
      setKeyRepeatPeriod(150);
      pacActor.setSlowDown(3);

      setupActorLocations();


      //Run the game
      doRun();
      show();

      // Loop to look for collision in the application thread
      // This makes it improbable that we miss a hit
      boolean hasPacmanBeenHit;
      boolean hasPacmanEatAllPills;
      setupPillAndItemsLocations();
      int maxPillsAndItems = countPillsAndItems();

      do {
        hasPacmanBeenHit = checkPacmanHit();
//        hasPacmanBeenHit = false;
        hasPacmanEatAllPills = pacActor.getNbPills() >= maxPillsAndItems;
        delay(10);
      } while(!hasPacmanBeenHit && !hasPacmanEatAllPills);
      delay(120);

      Location loc = pacActor.getLocation();
      setMonstersStopMoving(true);
      pacActor.removeSelf();

      String title = "";
      if (hasPacmanBeenHit) {
        bg.setPaintColor(Color.red);
        lose = true;
        title = "GAME OVER";
        addActor(new Actor("sprites/explosion3.gif"), loc);
        setTitle(title);
        break;
      }

      for (Monster monster: monsters) {
        monster.removeSelf();
      }
      for (Actor portal : portals) {
        portal.removeSelf();
      }

    }
    // out of loop, either we lost OR finished all files in dir
    if (!lose) {
      String title = "YOU WIN";
      setTitle(title);
      gameCallback.endOfGame(title);
    }

    if (filename.isDirectory()) {
      return null;
    }
    else {
      return filename;
    }

//    Directory:
//      win = no map
//      lose = no map
//    Level:
//      win/lose = that map
//    return null;
  }

  private void renewArrays() {
    whitePortals = new ArrayList<>();
    yellowPortals = new ArrayList<>();
    DGoldPortals = new ArrayList<>();
    DGreyPortals = new ArrayList<>();
    allPortals = new ArrayList<>();

    portals = new ArrayList<>();
    monsters = new ArrayList<>();
  }

  private void setMonstersStopMoving(boolean b) {
    for (Monster monster : monsters) {
      monster.setStopMoving(b);
    }
  }

  private boolean checkPacmanHit() {
    for (Monster monster : monsters) {
      if (monster.getLocation().equals(pacActor.getLocation())) {
        return true;
      }
    }
    return false;
  }


  public GameCallback getGameCallback() {
    return gameCallback;
  }

  private void setupActorLocations() {

    for (Location trollLoc : grid.getTrollLoc()) {
      trolls.add(new Monster(this, MonsterType.Troll));
      addActor(trolls.get(trolls.size() -1), trollLoc, Location.NORTH);
      trolls.get(trolls.size() -1).setSeed(seed);
      trolls.get(trolls.size() -1).setSlowDown(3);
    }

    for (Location tx5Loc : grid.getTX5Loc()) {
      tx5s.add(new Monster(this, MonsterType.TX5));
      addActor(tx5s.get(tx5s.size() -1), tx5Loc, Location.NORTH);
      tx5s.get(tx5s.size() -1).setSeed(seed);
      tx5s.get(tx5s.size() -1).setSlowDown(3);
      tx5s.get(tx5s.size() -1).stopMoving(5);
    }
    addActor(pacActor, grid.getPacLoc());

    monsters.addAll(trolls);
    monsters.addAll(tx5s);
  }

  private int countPillsAndItems() {
    int pillsAndItemsCount = 0;
    for (int y = 0; y < nbVertCells; y++)
    {
      for (int x = 0; x < nbHorzCells; x++)
      {
        Location location = new Location(x, y);
        Space space = grid.getCell(location);
        if (space == Space.Pill) { // Pill
          pillsAndItemsCount++;
        } else if (space == Space.Gold) { // Gold
          pillsAndItemsCount++;
        }
      }
    }
    return pillsAndItemsCount;
  }

  public ArrayList<Location> getPillAndItemLocations() {
    return pillAndItemLocations;
  }



  private void setupPillAndItemsLocations() {
    for (int y = 0; y < nbVertCells; y++)
    {
      for (int x = 0; x < nbHorzCells; x++)
      {
        Location location = new Location(x, y);
        Space space = grid.getCell(location);
        if (space == Space.Pill) {
          pillAndItemLocations.add(location);
        }
        if (space == Space.Gold) {
          pillAndItemLocations.add(location);
        }
        if (space == Space.Ice) {
//          pillAndItemLocations.add(location);
        }
      }
    }
  }

  private void drawGrid(GGBackground bg)
  {
    bg.clear(Color.gray);
    bg.setPaintColor(Color.white);
    for (int y = 0; y < nbVertCells; y++)
    {
      for (int x = 0; x < nbHorzCells; x++)
      {
        bg.setPaintColor(Color.white);
        Location location = new Location(x, y);
        Space s = grid.getCell(location);
        if (s != Space.Wall)
          bg.fillCell(location, Color.lightGray);

        if (s == Space.Pill) { // Pill
          putPill(bg, location);
        } else if (s == Space.Gold ) { // Gold
          putGold(bg, location);
        } else if (s == Space.Ice) {
          putIce(bg, location);
        }
        else if (s == Space.WhitePortal) {
          Actor portal = new Actor("data/i_portalWhiteTile.png");
          portals.add(portal);
          addActor(portal, location);
          whitePortals.add(location);
        }
        else if (s == Space.YellowPortal) {
          Actor portal = new Actor("data/j_portalYellowTile.png");
          portals.add(portal);
          addActor(portal, location);
          yellowPortals.add(location);
        }
        else if (s == Space.DGoldPortal) {
          Actor portal = new Actor("data/k_portalDarkGoldTile.png");
          portals.add(portal);
          addActor(portal, location);
          DGoldPortals.add(location);
        }
        else if (s == Space.DGrayPortal) {
          Actor portal = new Actor("data/l_portalDarkGrayTile.png");
          portals.add(portal);
          addActor(portal, location);
          DGreyPortals.add(location);
        }
      }
    }
    allPortals.add(whitePortals);
    allPortals.add(yellowPortals);
    allPortals.add(DGoldPortals);
    allPortals.add(DGreyPortals);

  }

  private void putPill(GGBackground bg, Location location){
    bg.fillCircle(toPoint(location), 5);
  }

  private void putGold(GGBackground bg, Location location){
    bg.setPaintColor(Color.yellow);
    bg.fillCircle(toPoint(location), 5);
    Actor gold = new Actor("sprites/gold.png");
    this.goldPieces.add(gold);
    addActor(gold, location);
  }

  private void putIce(GGBackground bg, Location location){
    bg.setPaintColor(Color.blue);
    bg.fillCircle(toPoint(location), 5);
    Actor ice = new Actor("sprites/ice.png");
    this.iceCubes.add(ice);
    addActor(ice, location);
  }

  public void removeItem(String type,Location location){
    if(type.equals("gold")){
      for (Actor item : this.goldPieces){
        if (location.getX() == item.getLocation().getX() && location.getY() == item.getLocation().getY()) {
          item.hide();
        }
      }
    }else if(type.equals("ice")){
      for (Actor item : this.iceCubes){
        if (location.getX() == item.getLocation().getX() && location.getY() == item.getLocation().getY()) {
          item.hide();
        }
      }
    }
  }

  public int getNumHorzCells(){
    return this.nbHorzCells;
  }
  public int getNumVertCells(){
    return this.nbVertCells;
  }



  public ArrayList<ArrayList<Location>> getAllPortals(){
    return allPortals;
  }
}
