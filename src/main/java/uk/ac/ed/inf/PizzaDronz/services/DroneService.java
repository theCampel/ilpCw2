package uk.ac.ed.inf.PizzaDronz.services;

import org.springframework.stereotype.Service;
import uk.ac.ed.inf.PizzaDronz.models.*;

@Service
public class DroneService {
    
    public double calculateDistance(LngLatPairRequest lngLatPairRequest) {
        double lng1 = lngLatPairRequest.getPosition1().getLng();
        double lat1 = lngLatPairRequest.getPosition1().getLat();
        double lng2 = lngLatPairRequest.getPosition2().getLng();
        double lat2 = lngLatPairRequest.getPosition2().getLat();

        return Math.sqrt(Math.pow(lng2 - lng1, 2) + Math.pow(lat2 - lat1, 2));
    }

    public LngLat calculateNextPosition(NextPositionRequest nextPositionRequest) {
        double angleDegrees = nextPositionRequest.getAngle();
        double angleRadians = Math.toRadians(90 - angleDegrees);
        double deltaLng = 0.00015 * Math.cos(angleRadians);
        double deltaLat = 0.00015 * Math.sin(angleRadians);

        double newLng = nextPositionRequest.getStart().getLng() + deltaLng;
        double newLat = nextPositionRequest.getStart().getLat() + deltaLat;

        return new LngLat(newLng, newLat);
    }

    public boolean isPointInPolygon(InRegionRequest inRegionRequest) {
        LngLat[] polygon = inRegionRequest.getRegion().getVertices();
        LngLat point = inRegionRequest.getPosition();
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