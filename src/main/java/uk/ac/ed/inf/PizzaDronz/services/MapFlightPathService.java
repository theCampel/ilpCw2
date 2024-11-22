package uk.ac.ed.inf.PizzaDronz.services;

import org.springframework.stereotype.Service;

import uk.ac.ed.inf.PizzaDronz.constants.SystemConstants;
import uk.ac.ed.inf.PizzaDronz.models.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Set;

@Service
public class MapFlightPathService {
    private final List<Region> noFlyZones;
    private final Region centralRegion;
    private static final double[] VALID_ANGLES = SystemConstants.VALID_ANGLES;
    private final DroneService droneService;
    
    public MapFlightPathService(DroneService droneService) {
        this.droneService = droneService;
        this.noFlyZones = initializeNoFlyZones();
        this.centralRegion = initializeCentralRegion();
    }
    
    // TODO: This is hardcoded. Get dynamically from endpoint /noFlyZones
    
    private Region initializeCentralRegion() {
        return new Region(SystemConstants.CENTRAL_REGION_NAME, new LngLat[]{
            new LngLat(-3.192473, 55.946233),
            new LngLat(-3.184319, 55.946233), 
            new LngLat(-3.184319, 55.942617),
            new LngLat(-3.192473, 55.942617),
            new LngLat(-3.192473, 55.946233)
        });
    }
    
    private List<Region> initializeNoFlyZones() {
        List<Region> zones = new ArrayList<>();
        
        // George Square Area
        zones.add(new Region("George Square Area", new LngLat[]{
            new LngLat(-3.190578818321228, 55.94402412577528),
            new LngLat(-3.1899887323379517, 55.94284650540911),
            new LngLat(-3.187097311019897, 55.94328811724263),
            new LngLat(-3.187682032585144, 55.944477740393744),
            new LngLat(-3.190578818321228, 55.94402412577528)
        }));
        
        // Dr Elsie Inglis Quadrangle
        zones.add(new Region("Dr Elsie Inglis Quadrangle", new LngLat[]{
            new LngLat(-3.1907182931900024, 55.94519570234043),
            new LngLat(-3.1906163692474365, 55.94498241796357),
            new LngLat(-3.1900262832641597, 55.94507554227258),
            new LngLat(-3.190133571624756, 55.94529783810495),
            new LngLat(-3.1907182931900024, 55.94519570234043)
        }));
        
        // Bristo Square Open Area
        zones.add(new Region("Bristo Square Open Area", new LngLat[]{
            new LngLat(-3.189543485641479, 55.94552313663306),
            new LngLat(-3.189382553100586, 55.94553214854692),
            new LngLat(-3.189259171485901, 55.94544803726933),
            new LngLat(-3.1892001628875732, 55.94533688994374),
            new LngLat(-3.189194798469543, 55.94519570234043),
            new LngLat(-3.189135789871216, 55.94511759833873),
            new LngLat(-3.188138008117676, 55.9452738061846),
            new LngLat(-3.1885510683059692, 55.946105902745614),
            new LngLat(-3.1895381212234497, 55.94555918427592),
            new LngLat(-3.189543485641479, 55.94552313663306)
        }));
        
        // Bayes Central Area
        zones.add(new Region("Bayes Central Area", new LngLat[]{
            new LngLat(-3.1876927614212036, 55.94520696732767),
            new LngLat(-3.187555968761444, 55.9449621408666),
            new LngLat(-3.186981976032257, 55.94505676722831),
            new LngLat(-3.1872327625751495, 55.94536993377657),
            new LngLat(-3.1874459981918335, 55.9453361389472),
            new LngLat(-3.1873735785484314, 55.94519344934259),
            new LngLat(-3.1875935196876526, 55.94515665035927),
            new LngLat(-3.187624365091324, 55.94521973430925),
            new LngLat(-3.1876927614212036, 55.94520696732767)
        }));
        
        return zones;
    }
    
    public List<Region> getNoFlyZones() {
        return noFlyZones;
    }

    public Region getCentralRegion() {
        return centralRegion;
    }

    public List<LngLat> findPath(LngLat start, LngLat end) {
        PriorityQueue<PathNode> openSet = new PriorityQueue<>();
        Set<PathNode> closedSet = new HashSet<>();
        Map<PathNode, PathNode> cameFrom = new HashMap<>();
        Map<PathNode, Double> gScore = new HashMap<>();
        
        PathNode startNode = new PathNode(start);
        openSet.add(startNode);
        gScore.put(startNode, 0.0);
        
        boolean enteredCentral = droneService.isInRegion(start, centralRegion);
        
        while (!openSet.isEmpty()) {
            PathNode current = openSet.poll();
            
            if (droneService.isCloseTo(current.position, end)) {
                return reconstructPath(cameFrom, current);
            }
            
            closedSet.add(current);
            
            // Try all possible moves (16 compass directions)
            for (double angle : VALID_ANGLES) {
                LngLat nextPos = droneService.nextPosition(current.position, angle);
                PathNode neighbor = new PathNode(nextPos);
                
                // Skip if we've already visited this node
                if (closedSet.contains(neighbor)) continue;
                
                // Check if move is valid
                if (!isValidMove(current.position, nextPos, enteredCentral)) continue;
                
                // Update enteredCentral flag
                if (!enteredCentral && droneService.isInRegion(nextPos, centralRegion)) {
                    enteredCentral = true;
                }
                
                double tentativeGScore = gScore.get(current) + droneService.distanceTo(current.position, nextPos);
                
                if (!gScore.containsKey(neighbor) || tentativeGScore < gScore.get(neighbor)) {
                    cameFrom.put(neighbor, current);
                    gScore.put(neighbor, tentativeGScore);
                    neighbor.fScore = tentativeGScore + droneService.distanceTo(nextPos, end);
                    
                    if (!openSet.contains(neighbor)) {
                        openSet.add(neighbor);
                    }
                }
            }
        }    
        return new ArrayList<>(); // No path found
    }
    
    private boolean isValidMove(LngLat current, LngLat next, boolean enteredCentral) {
        // Check if we're trying to leave central area after entering it
        if (enteredCentral && !droneService.isInRegion(next, centralRegion)) {
            return false;
        }
        
        // Check for no-fly zones with buffer
        for (Region noFlyZone : noFlyZones) {
            // Check if either point is too close to the no-fly zone // Not perfect - still gets too close.
            for (LngLat vertex : noFlyZone.getVertices()) {
                if (droneService.distanceTo(current, vertex) <= SystemConstants.DRONE_IS_CLOSE_DISTANCE ||
                    droneService.distanceTo(next, vertex) <= SystemConstants.DRONE_IS_CLOSE_DISTANCE) {
                    return false;
                }
            }
            
            // Check if the point is inside the no-fly zone
            if (droneService.isInRegion(next, noFlyZone) || droneService.isInRegion(current, noFlyZone)) {
                return false;
            }
        }
        
        return true;
    }
    
    private List<LngLat> reconstructPath(Map<PathNode, PathNode> cameFrom, PathNode current) {
        List<LngLat> path = new ArrayList<>();
        PathNode node = current;
        
        while (node != null) {
            path.add(0, node.position);
            node = cameFrom.get(node);
        }
        return path;
    }
    
    /**
     * Helper class for A* pathfinding algorithm.
     * Wraps a position (LngLat) with its associated pathfinding metadata.
     * Used to:
     * 1. Track positions in the priority queue (ordered by fScore)
     * 2. Store visited positions in sets/maps
     * 3. Associate each position with its A* score
     */
    private static class PathNode implements Comparable<PathNode> {
        /** The actual coordinate position */
        LngLat position;
        
        /** 
         * The f-score used by A* algorithm
         * f-score = g-score (distance from start) + heuristic (estimated distance to end)
         * Initialized to MAX_VALUE as per A* algorithm requirements
         */
        double fScore = Double.MAX_VALUE;
        
        /**
         * Creates a new PathNode for the given position
         * @param position The LngLat coordinate this node represents
         */
        PathNode(LngLat position) {
            this.position = position;
        }
        
        /**
         * Compares PathNodes based on their fScores
         * Required for PriorityQueue to order nodes by their scores
         * Lower fScores are prioritized (better paths)
         */
        @Override
        public int compareTo(PathNode other) {
            return Double.compare(this.fScore, other.fScore);
        }
        
        /**
         * Two PathNodes are equal if they represent the same position
         * Required for checking if a position has been visited
         */
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PathNode pathNode = (PathNode) o;
            return position.equals(pathNode.position);
        }
        
        /**
         * Hash code based on position only
         * Required for HashSet/HashMap operations
         */
        @Override
        public int hashCode() {
            return Objects.hash(position);
        }
    }
}
