package uk.ac.ed.inf.PizzaDronz.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.ac.ed.inf.PizzaDronz.constants.SystemConstants;
import uk.ac.ed.inf.PizzaDronz.models.LngLat;
import uk.ac.ed.inf.PizzaDronz.models.Region;

import static org.junit.jupiter.api.Assertions.*;

class DroneServiceTest {
    
    private DroneService droneService;
    private LngLat appletonTower;
    
    @BeforeEach
    void setUp() {
        droneService = new DroneService();
        appletonTower = new LngLat(SystemConstants.APPLETON_LNG, SystemConstants.APPLETON_LAT);
    }
    
    @Test
    void testDistanceTo_UniqueCoordinates() {
        LngLat point1 = new LngLat(-3.186267, 55.944154);
        LngLat point2 = new LngLat(-3.184954, 55.946999);
        
        double distance = droneService.distanceTo(point1, point2);
        assertEquals(0.0031336, distance, SystemConstants.DRONE_IS_CLOSE_DISTANCE);
    }

    @Test
    void testDistanceTo_SameCoordinates_ReturnsZero() {
        LngLat point1 = new LngLat(-3.186267, 55.944154);
        LngLat point2 = new LngLat(-3.186267, 55.944154);
        double distance = droneService.distanceTo(point1, point2);
        assertEquals(0, distance, SystemConstants.DRONE_IS_CLOSE_DISTANCE);
    }
    
    @Test
    void testIsCloseTo_PointsAreClose() {
        LngLat nearPoint = new LngLat(
            appletonTower.getLng() + (SystemConstants.DRONE_IS_CLOSE_DISTANCE / 2),
            appletonTower.getLat()
        );
        assertTrue(droneService.isCloseTo(appletonTower, nearPoint));
    }
    
    @Test
    void testIsCloseTo_PointsAreFar() {
        LngLat farPoint = new LngLat(
            appletonTower.getLng() + (SystemConstants.DRONE_IS_CLOSE_DISTANCE * 2),
            appletonTower.getLat()
        );
        assertFalse(droneService.isCloseTo(appletonTower, farPoint));
    }
    
    @Test
    void testNextPosition_MoveNorthEastFromAppleton() {
        LngLat step1 = droneService.nextPosition(appletonTower, 45);
        LngLat result = droneService.nextPosition(step1, 45);
        
        assertEquals(-3.186662, result.getLng(), 
                    SystemConstants.DRONE_IS_CLOSE_DISTANCE);
        assertEquals(55.944706, result.getLat(), 
                    SystemConstants.DRONE_IS_CLOSE_DISTANCE);
    }
    
    @Test
    void testNextPosition_MoveNorthFromAppleton() {
        LngLat step1 = droneService.nextPosition(appletonTower, 90);
        LngLat result = droneService.nextPosition(step1, 90);
        
        assertEquals(appletonTower.getLng(), 
                    result.getLng(), SystemConstants.DRONE_IS_CLOSE_DISTANCE);
        assertEquals(appletonTower.getLat() + (SystemConstants.DRONE_MOVE_DISTANCE * 2), 
                    result.getLat(), SystemConstants.DRONE_IS_CLOSE_DISTANCE);
    }
    
    @Test
    void testIsInRegion_PointInsideRegion() {
        LngLat[] vertices = {
            new LngLat(-3.188267, 55.944154),
            new LngLat(-3.192473, 55.946233),
            new LngLat(-3.189000, 55.945000),
            new LngLat(-3.188267, 55.944154)
        };
        Region testRegion = new Region("Test Region", vertices);
        
        LngLat insidePoint = new LngLat(-3.1899133333333336, 55.945129);
        assertTrue(droneService.isInRegion(insidePoint, testRegion));
    }
    
    @Test
    void testIsInRegion_PointOutsideRegion() {
        LngLat[] vertices = {
            new LngLat(-3.188267, 55.944154),
            new LngLat(-3.192473, 55.946233),
            new LngLat(-3.189000, 55.945000),
            new LngLat(-3.188267, 55.944154)
        };
        Region testRegion = new Region("Test Region", vertices);
        
        LngLat outsidePoint = new LngLat(-3.195000, 55.950000);
        assertFalse(droneService.isInRegion(outsidePoint, testRegion));
    }
}
