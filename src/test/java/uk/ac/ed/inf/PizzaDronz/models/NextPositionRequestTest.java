package uk.ac.ed.inf.PizzaDronz.models;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class NextPositionRequestTest {
    @Test
    void testNextPositionRequest_ValidRequest() {
        NextPositionRequest request = new NextPositionRequest();
        request.setStart(new LngLat(-3.1883, 55.9444));
        request.setAngle(0);
        
        assertTrue(request.isValid());
    }
}