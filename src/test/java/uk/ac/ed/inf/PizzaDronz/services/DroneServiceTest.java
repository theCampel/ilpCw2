package uk.ac.ed.inf.PizzaDronz.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import uk.ac.ed.inf.PizzaDronz.constants.SystemConstants;
import uk.ac.ed.inf.PizzaDronz.models.LngLat;
import uk.ac.ed.inf.PizzaDronz.models.Region;
import uk.ac.ed.inf.PizzaDronz.models.Order;
import uk.ac.ed.inf.PizzaDronz.models.Restaurant;
import uk.ac.ed.inf.PizzaDronz.models.Pizza;
import java.util.List;
import java.util.Set;
import java.util.Arrays;
import uk.ac.ed.inf.PizzaDronz.models.CreditCardInformation;
import uk.ac.ed.inf.PizzaDronz.constants.DayOfWeek;

import static org.junit.jupiter.api.Assertions.*;

class DroneServiceTest {
    
    private DroneService droneService;
    private LngLat appletonTower;
    private MapFlightPathService mapFlightPathService;
    private RestaurantService restaurantService;
    
    @BeforeEach
    void setUp() {
        droneService = new DroneService();
        appletonTower = new LngLat(SystemConstants.APPLETON_LNG, SystemConstants.APPLETON_LAT);
        restaurantService = new RestaurantService(new RestTemplate());
        mapFlightPathService = new MapFlightPathService(droneService, new RestTemplate());
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

    @Test
    void testDeliveryPathCalculation_PerformanceUnder20Seconds() {
        // Create a sample valid order
        Restaurant restaurant = new Restaurant(
            "Civerinos Slice",
            new LngLat(-3.1912869215011597, 55.945535152517735),
            Set.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY),
            Arrays.asList(
                new Pizza("R1: Margarita", 1000),
                new Pizza("R1: Calzone", 1400)
            )
        );
        
        restaurantService.addRestaurant(restaurant);
        
        Order order = new Order(
            "63067305",
            "2024-11-15",
            2500,
            Arrays.asList(new Pizza("R1: Margarita", 1000), new Pizza("R1: Calzone", 1400)),
            new CreditCardInformation(
                "2221053797986070",
                "11/27",
                "254"
            )
        );

        // Measure execution time
        long startTime = System.currentTimeMillis();
        
        List<LngLat> path = mapFlightPathService.findPath(
            restaurant.getLocation(),
            new LngLat(SystemConstants.APPLETON_LNG, SystemConstants.APPLETON_LAT)
        );
        
        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;

        // Assert path is not null and contains points
        assertNotNull(path);
        assertFalse(path.isEmpty());
        
        // Assert execution time is under 20 seconds (20000 milliseconds)
        assertTrue(executionTime < 10000, 
            String.format("Path calculation took %d ms, which exceeds the 20 second limit", executionTime));
        
        // Optional: Print the actual execution time for monitoring
        System.out.println("Path calculation took " + executionTime + " ms");
    }
}
