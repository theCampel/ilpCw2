package uk.ac.ed.inf.PizzaDronz.controllers;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import static org.hamcrest.number.BigDecimalCloseTo.closeTo;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.*;

@WebMvcTest(Controller.class)
class ControllerTest {

    private Matcher<Object> isCloseTo(final double expected, final double error) {
        return new TypeSafeMatcher<Object>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("a numeric value within ")
                        .appendValue(error)
                        .appendText(" of ")
                        .appendValue(expected);
            }

            @Override
            protected boolean matchesSafely(Object item) {
                if (item instanceof Number) {
                    double actual = ((Number) item).doubleValue();
                    return Math.abs(actual - expected) <= error;
                }
                return false;
            }
        };
    }

    @Autowired
    private MockMvc mockMvc;

    //private static final DecimalFormat df = new DecimalFormat("#.########");
    private static final double error = 0.00015;

    // Test isAlive
    @Test
    public void testIsAlive() throws Exception {
        mockMvc.perform(get("/isAlive"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(true)));
    }

    // Test distanceTo with a valid request
    @Test
    public void testDistanceTo_ValidRequest() throws Exception {
        double expectedDistance = 0.00469176;

        String json = "{\"position1\":{\"lng\":-3.188267,\"lat\":55.944154},\"position2\":{\"lng\":-3.192473,\"lat\":55.946233}}";
        mockMvc.perform(post("/distanceTo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", isCloseTo(expectedDistance, error)));
    }

    // Test distanceTo with an invalid request with missing positions
    @Test
    public void testDistanceTo_InvalidRequest_MissingPosition() throws Exception {
        String json = "{\"position1\":{\"lng\":-3.188267,\"lat\":55.944154},\"position2\":{\"lng\":-3.192473}}";
        mockMvc.perform(post("/distanceTo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());
    }

    // Test distanceTo with an invalid request with invalid coordinates
    @Test
    public void testDistanceTo_InvalidRequest_InvalidCoordinates() throws Exception {
        String json = "{\"position1\":{\"lng\":200.0,\"lat\":55.944154},\"position2\":{\"lng\":-3.192473,\"lat\":-91.946233}}";
        mockMvc.perform(post("/distanceTo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());
    }

    // Test distanceTo with the same coordinates
    @Test
    public void testDistanceTo_SameCoordinates() throws Exception {
        String json = "{\"position1\":{\"lng\":-3.188267,\"lat\":55.944154},\"position2\":{\"lng\":-3.188267,\"lat\":55.944154}}";
        mockMvc.perform(post("/distanceTo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(0.0)));
    }

    // Test isCloseTo with a valid request where the distance is less than 0.00015
    @Test
    public void testIsCloseTo_ValidRequest() throws Exception {
        String json = "{\"position1\":{\"lng\":-3.188267,\"lat\":55.944154},\"position2\":{\"lng\":-3.188267,\"lat\":55.944154}}";
        mockMvc.perform(post("/isCloseTo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(true)));
    }

    // Test isCloseTo with a valid request where the distance is greater than 0.00015
    @Test
    public void testIsCloseTo_ValidRequest_False() throws Exception {
        String json = "{\"position1\":{\"lng\":-3.188267,\"lat\":55.944154},\"position2\":{\"lng\":-3.192473,\"lat\":55.946233}}";
        mockMvc.perform(post("/isCloseTo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(false)));
    }

    // Test isCloseTo with an invalid request with missing positions
    @Test
    public void testIsCloseTo_InvalidRequest_MissingPosition() throws Exception {
        String json = "{\"position1\":{\"lng\":-3.188267,\"lat\":55.944154},\"position2\":{\"lng\":-3.192473}}";
        mockMvc.perform(post("/isCloseTo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());
    }

    // Test distanceTo with null input
    @Test
    public void testDistanceTo_NullInput() throws Exception {
        String json = "";
        mockMvc.perform(post("/distanceTo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    // Test isCloseTo with invalid JSON structure
    @Test
    public void testIsCloseTo_InvalidJsonStructure() throws Exception {
        String json = "{\"pos1\":{\"lng\":-3.188267,\"lat\":55.944154}}";
        mockMvc.perform(post("/isCloseTo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    // Test nextPosition with angle out of bounds (negative)
    @Test
    public void testNextPosition_InvalidAngle_Negative() throws Exception {
        String json = "{\"start\":{\"lng\":-3.188267,\"lat\":55.944154},\"angle\":-45}";
        mockMvc.perform(post("/nextPosition")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    // Test nextPosition with angle out of bounds (greater than 359)
    @Test
    public void testNextPosition_InvalidAngle_OutOfBounds() throws Exception {
        String json = "{\"start\":{\"lng\":-3.188267,\"lat\":55.944154},\"angle\":400}";
        mockMvc.perform(post("/nextPosition")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    // Test nextPosition with valid edge angle (0 degrees)
    @Test
    public void testNextPosition_ValidEdgeAngle_ZeroDegrees() throws Exception {
        double lng = -3.188267;
        double lat = 55.944304;

        String json = "{\"start\":{\"lng\":-3.188267,\"lat\":55.944154},\"angle\":0}";
        mockMvc.perform(post("/nextPosition")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lng", isCloseTo(lng, error)))
                .andExpect(jsonPath("$.lat", isCloseTo(lat, error)));
    }

    // Test nextPosition with null start position
    @Test
    public void testNextPosition_NullStartPosition() throws Exception {
        String json = "{\"angle\":90}";
        mockMvc.perform(post("/nextPosition")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    // Test isInRegion with region having less than 3 vertices
    @Test
    public void testIsInRegion_InvalidRegion_InsufficientVertices() throws Exception {
        String json = "{\"position\":{\"lng\":-3.188267,\"lat\":55.944154},\"region\":{\"vertices\":[{\"lng\":-3.188267,\"lat\":55.944154}]}}";
        mockMvc.perform(post("/isInRegion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    // Test isInRegion with all vertices having the same longitude
    @Test
    public void testIsInRegion_InvalidRegion_AllSameLongitude() throws Exception {
        String json = "{\"position\":{\"lng\":-3.188267,\"lat\":55.944154},\"region\":{\"vertices\":[{\"lng\":-3.188267,\"lat\":55.944154},{\"lng\":-3.188267,\"lat\":55.945000},{\"lng\":-3.188267,\"lat\":55.946000}]}}";
        mockMvc.perform(post("/isInRegion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    // Test isInRegion with a valid region and point inside
    @Test
    public void testIsInRegion_ValidPointInsideRegion() throws Exception {
        String json = "{\"position\":{\"lng\":-3.1899133333333336,\"lat\":55.945129},\"region\":{\"vertices\":[{\"lng\":-3.188267,\"lat\":55.944154},{\"lng\":-3.192473,\"lat\":55.946233},{\"lng\":-3.189000,\"lat\":55.945000},{\"lng\":-3.188267,\"lat\":55.944154}]}}";

        mockMvc.perform(post("/isInRegion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(true)));
    }

    // Test isInRegion with a valid region and point outside
    @Test
    public void testIsInRegion_ValidPointOutsideRegion() throws Exception {
        String json = "{\"position\":{\"lng\":-3.195000,\"lat\":55.950000},\"region\":{\"vertices\":[{\"lng\":-3.188267,\"lat\":55.944154},{\"lng\":-3.192473,\"lat\":55.946233},{\"lng\":-3.189000,\"lat\":55.945000}, {\"lng\":-3.188267,\"lat\":55.944154}]}}";
        mockMvc.perform(post("/isInRegion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(false)));
    }
}
