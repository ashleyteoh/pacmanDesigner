package src;

import ch.aplu.jgamegrid.Location;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class SimpleAutoplayer implements Autoplayer {

    Game game;
    PacActor pacActor;
    public SimpleAutoplayer(Game game, PacActor pacActor) {
        this.game = game;
        this.pacActor = pacActor;
    }

    @Override
    public void autoplay() {
        if (pacActor.closestPill == null || pacActor.getLocation().equals(pacActor.closestPill)) {
            pacActor.closestPill = pacActor.closestPillLocation();
        }

        Queue<Location> queue;
        boolean[][] visited;
        Location[][] prev;
        queue = new LinkedList<>();
        visited = new boolean[game.getNumHorzCells()][game.getNumVertCells()];
        prev = new Location[game.getNumHorzCells()][game.getNumVertCells()];

        queue.offer(pacActor.getLocation()); // insert pacman location into queue
        visited[pacActor.getLocation().x][pacActor.getLocation().y] = true;
        double oldDirection = pacActor.getDirection();

        while (!queue.isEmpty()) {
            int index = -1;
            int hit = 0;
            Location current = queue.poll();
            // removes head

            Location currentPortal = new Location(current.getX(), current.getY());
            ArrayList<Location> portalHit = null;
            ArrayList<ArrayList<Location>> portals = game.getAllPortals();
            for (ArrayList<Location> portalType : portals) {
                if (portalType.contains(current)) {
                    index = portalType.indexOf(current);
                    portalHit = portalType;
                }
            }

            if (index == 1) {
                current = portalHit.get(0);
            } else if (index == 0 ){
                current = portalHit.get(1);
            }

//
            if (current.equals(pacActor.closestPill)) {
                // Reconstruct the path from closestPill to Pacman's location
                Location next = pacActor.closestPill;
                while (prev[next.x][next.y] != null && !prev[next.x][next.y].equals(pacActor.getLocation())) {
                    next = prev[next.x][next.y];
                }

                if (next != null && pacActor.canMove(next)) {
                    pacActor.setLocation(next);
                    break;
                }
            }

            for (Location neighbor : current.getNeighbourLocations(0.5)) {
                if (pacActor.canMove(neighbor) && !visited[neighbor.x][neighbor.y]) {
                    queue.offer(neighbor);
                    visited[neighbor.x][neighbor.y] = true;
                    prev[neighbor.x][neighbor.y] = currentPortal;
                }
            }
        }
    }
}
