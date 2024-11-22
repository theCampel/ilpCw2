package uk.ac.ed.inf.PizzaDronz.services;

import org.springframework.stereotype.Service;

import uk.ac.ed.inf.PizzaDronz.constants.SystemConstants;
import uk.ac.ed.inf.PizzaDronz.interfaces.LngLatHandling;
import uk.ac.ed.inf.PizzaDronz.models.*;

@Service
public class DroneService implements LngLatHandling {
    
    @Override
    public double distanceTo(LngLat startPosition, LngLat endPosition) {
        // Debugging Information:
        System.out.println("startPosition: " + startPosition);
        System.out.println("endPosition: " + endPosition);  

        double lng1 = startPosition.getLng();
        double lat1 = startPosition.getLat();
        double lng2 = endPosition.getLng();
        double lat2 = endPosition.getLat();

        return Math.sqrt(Math.pow(lng2 - lng1, 2) + Math.pow(lat2 - lat1, 2));
    }


    @Override
    public boolean isCloseTo(LngLat startPosition, LngLat otherPosition) {
        // Debugging Information:
        System.out.println("startPosition: " + startPosition);
        System.out.println("otherPosition: " + otherPosition);
        System.out.println("distance: " + distanceTo(startPosition, otherPosition));

        return distanceTo(startPosition, otherPosition) < SystemConstants.DRONE_IS_CLOSE_DISTANCE;
    }

    @Override
    public LngLat nextPosition(LngLat startPosition, double angle) {
        double angleRadians = Math.toRadians(90 - angle);
        double deltaLng = SystemConstants.DRONE_MOVE_DISTANCE * Math.cos(angleRadians);
        double deltaLat = SystemConstants.DRONE_MOVE_DISTANCE * Math.sin(angleRadians);

        double newLng = startPosition.getLng() + deltaLng;
        double newLat = startPosition.getLat() + deltaLat;

        return new LngLat(newLng, newLat);
    }

    public boolean isInRegion(LngLat position, Region region) {
        LngLat[] polygon = region.getVertices();
        LngLat point = position;
        int numVertices = polygon.length;
        boolean inside = false;

        for (int i = 0; i < numVertices; i++) {
            double x1 = polygon[i].getLng();
            double y1 = polygon[i].getLat();
            double x2 = polygon[(i + 1) % numVertices].getLng();
            double y2 = polygon[(i + 1) % numVertices].getLat();

            // Ray Casting algo:
            // Check if the point is within the y-bounds of the edge
            if ((y1 > point.getLat()) != (y2 > point.getLat())) {
                // Compute the x coordinate of the intersection of the edge with the ray from point (x, y)
                double xIntersect = x1 + (point.getLat() - y1) * (x2 - x1) / (y2 - y1);

                if (point.getLng() < xIntersect) {
                    inside = !inside;
                }
            }
        }
        return inside;
    }
} 