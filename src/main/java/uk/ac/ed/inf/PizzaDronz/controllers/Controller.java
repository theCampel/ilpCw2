package uk.ac.ed.inf.PizzaDronz.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.ac.ed.inf.PizzaDronz.models.*;

@RestController
public class Controller {

    @GetMapping("/isAlive")
    public boolean isAlive(){
        return true;
    }

    @GetMapping("/uuid")
    public String uuid(){
        return "s2222816";
    }

    @PostMapping("/distanceTo")
    public ResponseEntity<Double> distanceTo(@RequestBody LngLatPairRequest lngLatPairRequest){
        if (lngLatPairRequest == null || !lngLatPairRequest.isValid()){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        double distance = calculateDistance(lngLatPairRequest);
        return new ResponseEntity<>(distance, HttpStatus.OK);
    }

    private double calculateDistance(LngLatPairRequest lngLatPairRequest) {
        double lng1 = lngLatPairRequest.getPosition1().getLng();
        double lat1 = lngLatPairRequest.getPosition1().getLat();
        double lng2 = lngLatPairRequest.getPosition2().getLng();
        double lat2 = lngLatPairRequest.getPosition2().getLat();

        // Euclid distance formula
        return Math.sqrt(Math.pow(lng2 - lng1, 2) + Math.pow(lat2 - lat1, 2));
    }

    @PostMapping("/isCloseTo")
    public ResponseEntity<Boolean> isCloseTo(@RequestBody LngLatPairRequest lngLatPairRequest){
        if (lngLatPairRequest == null || !lngLatPairRequest.isValid()){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        double distance = calculateDistance(lngLatPairRequest);

        if (distance <= 0.00015){
            return new ResponseEntity<>(true, HttpStatus.OK);
        } else if (distance > 0.00015) {
            return new ResponseEntity<>(false, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/nextPosition")
    public ResponseEntity<LngLat> nextPosition(@RequestBody NextPositionRequest nextPositionRequest) {
        if (nextPositionRequest == null || !nextPositionRequest.isValid()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        LngLat nextPosition = calculateNextPosition(nextPositionRequest);
        return new ResponseEntity<>(nextPosition, HttpStatus.OK);
    }

    private LngLat calculateNextPosition(NextPositionRequest nextPositionRequest) {
        double angleDegrees = nextPositionRequest.getAngle();
        double angleRadians = Math.toRadians(90 - angleDegrees); // Adjust angle to mathematical angle
        double deltaLng = 0.00015 * Math.cos(angleRadians);
        double deltaLat = 0.00015 * Math.sin(angleRadians);

        double newLng = nextPositionRequest.getStart().getLng() + deltaLng;
        double newLat = nextPositionRequest.getStart().getLat() + deltaLat;

        return new LngLat(newLng, newLat);
    }


    @PostMapping("/isInRegion")
    public ResponseEntity<Boolean> isInRegion(@RequestBody InRegionRequest inRegionRequest) {
        if (inRegionRequest == null || !inRegionRequest.isValid()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        boolean isInRegion = isPointInPolygon(inRegionRequest);
        return new ResponseEntity<>(isInRegion, HttpStatus.OK);
    }

    private boolean isPointInPolygon(InRegionRequest inRegionRequest) {
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
