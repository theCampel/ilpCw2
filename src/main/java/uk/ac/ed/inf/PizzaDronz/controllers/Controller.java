package uk.ac.ed.inf.PizzaDronz.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import uk.ac.ed.inf.PizzaDronz.constants.OrderValidationCode;
import uk.ac.ed.inf.PizzaDronz.models.*;
import uk.ac.ed.inf.PizzaDronz.services.*;
@RestController
public class Controller {

    private final DroneService droneService;

    public Controller(DroneService droneService) {
        this.droneService = droneService;
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
        double distance = droneService.calculateDistance(lngLatPairRequest);
        return new ResponseEntity<>(distance, HttpStatus.OK);
    }



    @PostMapping("/isCloseTo")
    public ResponseEntity<Boolean> isCloseTo(@RequestBody LngLatPairRequest lngLatPairRequest){
        if (lngLatPairRequest == null || !lngLatPairRequest.isValid()){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        double distance = droneService.calculateDistance(lngLatPairRequest);

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
        LngLat nextPosition = droneService.calculateNextPosition(nextPositionRequest);
        return new ResponseEntity<>(nextPosition, HttpStatus.OK);
    }


    @PostMapping("/isInRegion")
    public ResponseEntity<Boolean> isInRegion(@RequestBody InRegionRequest inRegionRequest) {
        if (inRegionRequest == null || !inRegionRequest.isValid()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        boolean isInRegion = droneService.isPointInPolygon(inRegionRequest);
        return new ResponseEntity<>(isInRegion, HttpStatus.OK);
    }

    // There's a lot wrong here
    // TODO:
    // 1. Order isn't outputting the correct thing
    // 2. Order isn't correctly validating - you've simply not implemented it tbf
    
    @PostMapping("/validateOrder")
    public ResponseEntity<OrderValidationCode> validateOrder(@RequestBody Order order) {
        if (order == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        
        OrderValidationCode orderValidationCode = order.validateOrder();
        return new ResponseEntity<>(orderValidationCode, HttpStatus.OK);
    }


}
