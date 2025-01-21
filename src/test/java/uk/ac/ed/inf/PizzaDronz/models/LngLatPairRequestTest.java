package uk.ac.ed.inf.PizzaDronz.models;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class LngLatPairRequestTest {
    @Test
    void testLngLatPairRequest_ValidRequest() {
        LngLat lngLat1 = new LngLat(-3.1883, 55.9444);
        LngLat lngLat2 = new LngLat(-3.1883, 55.9444);
        
        LngLatPairRequest request = new LngLatPairRequest();
        request.setPosition1(lngLat1);
        request.setPosition2(lngLat2);
        assertTrue(request.isValid());
    }

    @Test
    void testLngLatPairRequest_InvalidRequest() {
        LngLatPairRequest request = new LngLatPairRequest();
        request.setPosition1(null);
        request.setPosition2(new LngLat(-3.1883, 55.9444));
        assertFalse(request.isValid());
    }
}
