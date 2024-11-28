package uk.ac.ed.inf.PizzaDronz.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import uk.ac.ed.inf.PizzaDronz.constants.OrderStatus;
import uk.ac.ed.inf.PizzaDronz.constants.SystemConstants;
import uk.ac.ed.inf.PizzaDronz.models.*;
import uk.ac.ed.inf.PizzaDronz.services.*;

import java.util.List;

@RestController
public class Controller {

    private final DroneService droneService;
    private final OrderService orderService;
    private final MapFlightPathService mapFlightPathService;

    public Controller(DroneService droneService, OrderService orderService, 
                       MapFlightPathService mapFlightPathService) {
        this.droneService = droneService;
        this.orderService = orderService;
        this.mapFlightPathService = mapFlightPathService;
    }

    // Health check
    @GetMapping("/isAlive")
    public boolean isAlive(){
        return true;
    }

    // Return the student ID
    @GetMapping("/uuid")
    public String uuid(){
        return "s2222816";
    }

    // Return the distance between two points, given as a request body
    @PostMapping("/distanceTo")
    public ResponseEntity<Double> distanceTo(@RequestBody LngLatPairRequest lngLatPairRequest){
        if (lngLatPairRequest == null || !lngLatPairRequest.isValid()){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        double distance = droneService.distanceTo(lngLatPairRequest.getPosition1(), 
                                                lngLatPairRequest.getPosition2());
        return new ResponseEntity<>(distance, HttpStatus.OK);
    }

    // Return whether two points are close (less than 0.00015 as declared in constants file),
    // given as a request body
    @PostMapping("/isCloseTo")
    public ResponseEntity<Boolean> isCloseTo(@RequestBody LngLatPairRequest lngLatPairRequest){
        if (lngLatPairRequest == null || !lngLatPairRequest.isValid()){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        boolean isCloseTo = droneService.isCloseTo(lngLatPairRequest.getPosition1(), 
                                                  lngLatPairRequest.getPosition2());
        return new ResponseEntity<>(isCloseTo, HttpStatus.OK);
    }

    // Return the next position (direction + 0.00015 degrees)
    @PostMapping("/nextPosition")
    public ResponseEntity<LngLat> nextPosition(@RequestBody NextPositionRequest nextPositionRequest) {
        if (nextPositionRequest == null || !nextPositionRequest.isValid()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        LngLat nextPosition = droneService.nextPosition(nextPositionRequest.getStart(), 
                                                      nextPositionRequest.getAngle());
        return new ResponseEntity<>(nextPosition, HttpStatus.OK);
    }

    // Return whether a point is in a region, given as a request body 
    // Uses ray casting algorithm.
    @PostMapping("/isInRegion")
    public ResponseEntity<Boolean> isInRegion(@RequestBody InRegionRequest inRegionRequest) {
        if (inRegionRequest == null || !inRegionRequest.isValid()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        boolean isInRegion = droneService.isInRegion(inRegionRequest.getPosition(), 
                                                    inRegionRequest.getRegion());
        return new ResponseEntity<>(isInRegion, HttpStatus.OK);
    }

    // Return the validation result of an order, given as a request body
    @PostMapping("/validateOrder")
    public ResponseEntity<OrderValidationResult> validateOrder(@RequestBody Order order) {
        if (order == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        
        OrderValidationResult orderValidationResult = orderService.validateOrder(order);
        return new ResponseEntity<>(orderValidationResult, HttpStatus.OK);
    }

    // Return the delivery path of an order.
    @PostMapping("/calcDeliveryPath")
    public ResponseEntity<List<LngLat>> calcDeliveryPath(@RequestBody Order order) {
        if (order == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        OrderValidationResult validationResult = orderService.validateOrder(order);
        
        if (validationResult.getOrderStatus() == OrderStatus.INVALID) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        
        List<LngLat> path = mapFlightPathService.findPath(order.getRestaurant().getLocation(), 
                                                      new LngLat(SystemConstants.APPLETON_LNG, 
                                                                 SystemConstants.APPLETON_LAT));
        
        return new ResponseEntity<>(path, HttpStatus.OK);
    }

    // Return the delivery path of an order as a GeoJSON-pastable string.
    @PostMapping("/calcDeliveryPathAsGeoJson")
    public ResponseEntity<String> calcDeliveryPathAsGeoJson(@RequestBody Order order) {
        if (order == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        OrderValidationResult validationResult = orderService.validateOrder(order);
        
        if (validationResult.getOrderStatus() == OrderStatus.INVALID) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        
        List<LngLat> path = mapFlightPathService.findPath(
            order.getRestaurant().getLocation(), 
            new LngLat(SystemConstants.APPLETON_LNG, SystemConstants.APPLETON_LAT)
        );
        
        String geoJson = mapFlightPathService.convertPathToGeoJson(path);
        return new ResponseEntity<>(geoJson, HttpStatus.OK);
    }
}
