package uk.ac.ed.inf.PizzaDronz.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import uk.ac.ed.inf.PizzaDronz.models.Order;
import uk.ac.ed.inf.PizzaDronz.models.OrderValidationResult;
import uk.ac.ed.inf.PizzaDronz.constants.OrderValidationCode;
import uk.ac.ed.inf.PizzaDronz.constants.OrderStatus;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OrderServiceTest {

    private OrderService orderService;
    private List<Order> testOrders;

    @BeforeEach
    void setUp() throws IOException {
        RestaurantService restaurantService = new RestaurantService();
        orderService = new OrderService(restaurantService);
        
        // Load test orders from JSON file
        ObjectMapper mapper = new ObjectMapper();
        InputStream inputStream = getClass().getResourceAsStream("/test-orders.json");
        testOrders = mapper.readValue(inputStream, new TypeReference<List<Order>>() {});
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