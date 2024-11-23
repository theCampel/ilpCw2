package uk.ac.ed.inf.PizzaDronz.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.mockito.Mockito;
import org.springframework.web.client.RestTemplate;
import uk.ac.ed.inf.PizzaDronz.models.*;
import uk.ac.ed.inf.PizzaDronz.constants.OrderValidationCode;
import uk.ac.ed.inf.PizzaDronz.constants.OrderStatus;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;

class OrderServiceTest {

    private OrderService orderService;
    private List<Order> testOrders;
    private Restaurant[] testRestaurants;

    // Basically, we're not dynamically calling restaurant api endpoint for each
    // test, so instead we're getting hardcoding them into a json and reading
    // them from there. Below is reading them. 
    @BeforeEach
    void setUp() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        
        // Load test restaurants
        InputStream restaurantsStream = getClass().getResourceAsStream("/test-restaurants.json");
        testRestaurants = mapper.readValue(restaurantsStream, Restaurant[].class);
        
        // Create mock RestTemplate
        RestTemplate mockRestTemplate = Mockito.mock(RestTemplate.class);
        Mockito.when(mockRestTemplate.getForObject(anyString(), eq(Restaurant[].class)))
               .thenReturn(testRestaurants);
        
        // Initialize services with mock
        RestaurantService restaurantService = new RestaurantService(mockRestTemplate);
        orderService = new OrderService(restaurantService);
        
        // Load test orders
        InputStream ordersStream = getClass().getResourceAsStream("/test-orders.json");
        testOrders = mapper.readValue(ordersStream, new TypeReference<List<Order>>() {});
    }

    @TestFactory
    Stream<DynamicTest> testOrderValidation() {
        return testOrders.stream().map(order -> 
            DynamicTest.dynamicTest("Testing order: " + order.getOrderNo(), () -> {
                OrderValidationResult result = orderService.validateOrder(order);
                
                // Convert expected OrderValidationCode from string to enum
                OrderValidationCode expectedCode = OrderValidationCode.valueOf(order.getOrderValidationCode().toString());
                OrderStatus expectedStatus = OrderStatus.valueOf(order.getOrderStatus().toString());
                
                assertEquals(expectedCode, result.getValidationCode(),
                    String.format("Order %s: Expected validation code %s but got %s",
                        order.getOrderNo(), expectedCode, result.getValidationCode()));
                        
                assertEquals(expectedStatus, result.getOrderStatus(),
                    String.format("Order %s: Expected status %s but got %s",
                        order.getOrderNo(), expectedStatus, result.getOrderStatus()));
            })
        );
    }
} 