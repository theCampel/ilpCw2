package uk.ac.ed.inf.PizzaDronz.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import uk.ac.ed.inf.PizzaDronz.constants.OrderStatus;
import uk.ac.ed.inf.PizzaDronz.constants.SystemConstants;
import uk.ac.ed.inf.PizzaDronz.models.*;
import uk.ac.ed.inf.PizzaDronz.services.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class Controller {

    private final DroneService droneService;
    private final OrderService orderService;

    public Controller(DroneService droneService, OrderService orderService) {
        this.droneService = droneService;
        this.orderService = orderService;
    }

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

        double distance = droneService.distanceTo(lngLatPairRequest.getPosition1(), 
                                                lngLatPairRequest.getPosition2());
        return new ResponseEntity<>(distance, HttpStatus.OK);
    }

    @PostMapping("/isCloseTo")
    public ResponseEntity<Boolean> isCloseTo(@RequestBody LngLatPairRequest lngLatPairRequest){
        if (lngLatPairRequest == null || !lngLatPairRequest.isValid()){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        boolean isCloseTo = droneService.isCloseTo(lngLatPairRequest.getPosition1(), 
                                                  lngLatPairRequest.getPosition2());
        return new ResponseEntity<>(isCloseTo, HttpStatus.OK);
    }

    @PostMapping("/nextPosition")
    public ResponseEntity<LngLat> nextPosition(@RequestBody NextPositionRequest nextPositionRequest) {
        if (nextPositionRequest == null || !nextPositionRequest.isValid()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        LngLat nextPosition = droneService.nextPosition(nextPositionRequest.getStart(), 
                                                      nextPositionRequest.getAngle());
        return new ResponseEntity<>(nextPosition, HttpStatus.OK);
    }


    @PostMapping("/isInRegion")
    public ResponseEntity<Boolean> isInRegion(@RequestBody InRegionRequest inRegionRequest) {
        if (inRegionRequest == null || !inRegionRequest.isValid()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        boolean isInRegion = droneService.isInRegion(inRegionRequest.getPosition(), 
                                                    inRegionRequest.getRegion());
        return new ResponseEntity<>(isInRegion, HttpStatus.OK);
    }
    
    @PostMapping("/validateOrder")
    public ResponseEntity<OrderValidationResult> validateOrder(@RequestBody Order order) {
        if (order == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        
        OrderValidationResult orderValidationResult = orderService.validateOrder(order);
        return new ResponseEntity<>(orderValidationResult, HttpStatus.OK);
    }

    @PostMapping("/calcDeliveryPath")
    public ResponseEntity<List<LngLat>> calcDeliveryPath(@RequestBody Order order) {
        if (order == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        OrderValidationResult validationResult = orderService.validateOrder(order);
        
        if (validationResult.getOrderStatus() == OrderStatus.INVALID) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        
        // TODO: Placeholder: Just return direct path from restaurant to Appleton Tower
        List<LngLat> path = new ArrayList<>();
        path.add(order.getRestaurant().getLocation());  // Start at restaurant
        path.add(new LngLat(SystemConstants.APPLETON_LNG, SystemConstants.APPLETON_LAT));  // End at Appleton Tower
        
        return new ResponseEntity<>(path, HttpStatus.OK);
    }
}
