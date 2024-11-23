package uk.ac.ed.inf.PizzaDronz.services;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import uk.ac.ed.inf.PizzaDronz.constants.SystemConstants;
import uk.ac.ed.inf.PizzaDronz.models.*;

import java.util.ArrayList;
import java.util.Arrays;
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
    private static final String NO_FLY_ZONES_API_URL = SystemConstants.NO_FLY_ZONES_API_URL;
    
    public MapFlightPathService(DroneService droneService, RestTemplate restTemplate) {
        this.droneService = droneService;
        this.noFlyZones = initialiseNoFlyZones(restTemplate);
        this.centralRegion = initialiseCentralRegion();
    }
    
    private Region initialiseCentralRegion() {
        return new Region(SystemConstants.CENTRAL_REGION_NAME, new LngLat[]{
            new LngLat(-3.192473, 55.946233),
            new LngLat(-3.184319, 55.946233), 
            new LngLat(-3.184319, 55.942617),
            new LngLat(-3.192473, 55.942617),
            new LngLat(-3.192473, 55.946233)
        });
    }
    
    private List<Region> initialiseNoFlyZones(RestTemplate restTemplate) {
        try {
            Region[] noFlyZonesArray = restTemplate.getForObject(NO_FLY_ZONES_API_URL, Region[].class);
            if (noFlyZonesArray == null) {
                throw new RuntimeException("Failed to fetch no-fly zones from API");
            }
            return new ArrayList<>(Arrays.asList(noFlyZonesArray));
        } catch (Exception e) {
            throw new RuntimeException("Error fetching no-fly zones: " + e.getMessage(), e);
        }
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
        Map<PathNode, Double> lastAngle = new HashMap<>();  // Track the last angle used
        
        PathNode startNode = new PathNode(start);
        openSet.add(startNode);
        gScore.put(startNode, 0.0);
        lastAngle.put(startNode, null);  // No previous angle for start node
        
        boolean enteredCentral = droneService.isInRegion(start, centralRegion);
        
        while (!openSet.isEmpty()) {
            PathNode current = openSet.poll();
            
            if (droneService.isCloseTo(current.position, end)) {
                return reconstructPath(cameFrom, current);
            }
            
            closedSet.add(current);
            
            // Get the previous angle used to reach this node
            Double prevAngle = lastAngle.get(current);
            
            // Filter valid angles based on previous direction
            double[] allowedAngles = filterValidAngles(prevAngle);
            
            // Try all allowed moves
            for (double angle : allowedAngles) {
                LngLat nextPos = droneService.nextPosition(current.position, angle);
                PathNode neighbor = new PathNode(nextPos);
                
                if (closedSet.contains(neighbor)) continue;
                if (!isValidMove(current.position, nextPos, enteredCentral)) continue;
                
                if (!enteredCentral && droneService.isInRegion(nextPos, centralRegion)) {
                    enteredCentral = true;
                }
                
                double tentativeGScore = gScore.get(current) + droneService.distanceTo(current.position, nextPos);
                
                if (!gScore.containsKey(neighbor) || tentativeGScore < gScore.get(neighbor)) {
                    cameFrom.put(neighbor, current);
                    gScore.put(neighbor, tentativeGScore);
                    lastAngle.put(neighbor, angle);  // Store the angle used
                    neighbor.fScore = tentativeGScore + droneService.distanceTo(nextPos, end);
                    
                    if (!openSet.contains(neighbor)) {
                        openSet.add(neighbor);
                    }
                }
            }
        }    
        return new ArrayList<>(); // No path found
    }
    
    // It's reasonable to assume that the drone will never need to turn more 
    // than 112.5 degrees at a time. (I.e. the opposite 5 directions)
    // Hence this heuristic. 
    private double[] filterValidAngles(Double prevAngle) {
        if (prevAngle == null) {
            return VALID_ANGLES;  // Return all angles for first move
        }
        
        List<Double> filtered = new ArrayList<>();
        for (double angle : VALID_ANGLES) {
            // Calculate the absolute difference between angles
            double diff = Math.abs(angle - prevAngle);
            // Normalize the difference to be between 0 and 180
            if (diff > 180) {
                diff = 360 - diff;
            }
            // Only add angles that aren't in the opposite 112.5-degree arc (5 directions)
            if (diff <= 112.5) {
                filtered.add(angle);
            }
        }
        
        return filtered.stream().mapToDouble(Double::doubleValue).toArray();
    }
    
    private boolean isValidMove(LngLat current, LngLat next, boolean enteredCentral) {
        // Check if we're trying to leave central area after entering it
        if (enteredCentral && !droneService.isInRegion(next, centralRegion)) {
            return false;
        }
        
        // Buffer distance for no-fly zones (in degrees)
        final double BUFFER = SystemConstants.NO_FLY_ZONE_BUFFER;
        
        // Check for no-fly zones with buffer
        for (Region noFlyZone : noFlyZones) {
            // Create a buffered version of the no-fly zone
            LngLat[] vertices = noFlyZone.getVertices();
            LngLat[] bufferedVertices = new LngLat[vertices.length];
            
            // For each vertex, extend it outward from the polygon's center
            LngLat center = calculateCenter(vertices);
            for (int i = 0; i < vertices.length; i++) {
                double dx = vertices[i].getLng() - center.getLng();
                double dy = vertices[i].getLat() - center.getLat();
                // Normalize and extend by buffer
                double distance = Math.sqrt(dx * dx + dy * dy);
                double scale = (distance + BUFFER) / distance;
                bufferedVertices[i] = new LngLat(
                    center.getLng() + dx * scale,
                    center.getLat() + dy * scale
                );
            }
            
            Region bufferedZone = new Region(noFlyZone.getName() + "_buffered", bufferedVertices);
            
            // Check if either point is in the buffered zone
            if (droneService.isInRegion(next, bufferedZone) || droneService.isInRegion(current, bufferedZone)) {
                return false;
            }
        }
        
        return true;
    }
    
    private LngLat calculateCenter(LngLat[] vertices) {
        double sumLng = 0, sumLat = 0;
        for (LngLat vertex : vertices) {
            sumLng += vertex.getLng();
            sumLat += vertex.getLat();
        }
        return new LngLat(sumLng / vertices.length, sumLat / vertices.length);
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
         * initialised to MAX_VALUE as per A* algorithm requirements
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

    public String convertPathToGeoJson(List<LngLat> path) {
        StringBuilder geoJson = new StringBuilder();
        geoJson.append("{\"type\":\"FeatureCollection\",\"features\":[{\"type\":\"Feature\",")
               .append("\"geometry\":{\"type\":\"LineString\",\"coordinates\":[");
        
        // Add coordinates
        for (int i = 0; i < path.size(); i++) {
            LngLat point = path.get(i);
            geoJson.append("[").append(point.getLng()).append(",").append(point.getLat()).append("]");
            if (i < path.size() - 1) {
                geoJson.append(",");
            }
        }
        
        // Close the GeoJSON structure
        geoJson.append("]},\"properties\":{}}]}");
        
        return geoJson.toString();
    }
}
