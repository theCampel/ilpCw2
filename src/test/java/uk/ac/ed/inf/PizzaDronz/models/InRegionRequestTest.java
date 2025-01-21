package uk.ac.ed.inf.PizzaDronz.models;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class InRegionRequestTest {

    @Test
    void testInRegionRequest_ValidRequest() {
        LngLat[] vertices = {
            new LngLat(-3.188267, 55.944154),
            new LngLat(-3.192473, 55.946233),
            new LngLat(-3.189000, 55.945000)
        };
        Region region = new Region("Test Region", vertices);
        LngLat point = new LngLat(-3.1883, 55.9444);

        InRegionRequest inRegionRequest = new InRegionRequest(point, region);
        assertTrue(inRegionRequest.isValid());
    }
}