package uk.ac.ed.inf.PizzaDronz.services;

import org.springframework.stereotype.Service;
import uk.ac.ed.inf.PizzaDronz.models.Order;
import uk.ac.ed.inf.PizzaDronz.models.OrderValidationResult;
import uk.ac.ed.inf.PizzaDronz.constants.OrderValidationCode;
import uk.ac.ed.inf.PizzaDronz.constants.OrderStatus;
import uk.ac.ed.inf.PizzaDronz.models.Pizza;
import uk.ac.ed.inf.PizzaDronz.models.Restaurant;
import uk.ac.ed.inf.PizzaDronz.models.CreditCardInformation;

import java.util.List;

/**
 * Service responsible for validating pizza orders.
 * Implements a chain of responsibility pattern for order validation.
 */
@Service
public class OrderService {
    private static final int MAX_PIZZAS_PER_ORDER = 4;
    private static final int DELIVERY_FEE_PENCE = 100;
    
    private final RestaurantService restaurantService;

    public OrderService(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    public OrderValidationResult validateOrder(Order order) {
        // Chain of validation steps
        return validateOrderSequentially(order,
            this::validateBasicOrderDetails,
            this::validatePizzas,
            this::validateRestaurant,
            this::validatePricing,
            this::validatePayment
        );
    }

    /**
     * Validate an order by running through diff validation steps.
     * Stops at the first failure.
     * 
     * @param order The order to validate
     * @param steps Variable number of validation steps to perform
     * @return The first failed validation result, or success if all pass
     */
    private OrderValidationResult validateOrderSequentially(Order order, ValidationStep... steps) {
        for (ValidationStep step : steps) {
            OrderValidationResult result = step.validate(order);
            if (result.getOrderStatus() == OrderStatus.INVALID) {
                return result; // If any step fails, immediately return the result
            } // Otherwise, continue to the next step
        }
        return new OrderValidationResult(OrderValidationCode.NO_ERROR, OrderStatus.VALID);
    }

    // Validates basic order properties like existence and number of pizzas.
    private OrderValidationResult validateBasicOrderDetails(Order order) {
        if (order.getPizzasInOrder() == null || order.getPizzasInOrder().isEmpty()) {
            return new OrderValidationResult(OrderValidationCode.EMPTY_ORDER, OrderStatus.INVALID);
        }
        if (order.getPizzasInOrder().size() > MAX_PIZZAS_PER_ORDER) {
            return new OrderValidationResult(OrderValidationCode.MAX_PIZZA_COUNT_EXCEEDED, OrderStatus.INVALID);
        }
        return new OrderValidationResult(OrderValidationCode.NO_ERROR, OrderStatus.VALID);
    }

    // Loops through all pizzas in order, calls validation on them.
     private OrderValidationResult validatePizzas(Order order) {
        return order.getPizzasInOrder().stream()
            .map(this::validateSinglePizza)
            .filter(result -> result.getOrderStatus() == OrderStatus.INVALID)
            .findFirst()
            .orElse(new OrderValidationResult(OrderValidationCode.NO_ERROR, OrderStatus.VALID));
    }

    /**
     * Validates a single pizza's existence and price against the menu.
     * @param pizza The pizza to validate
     * @return Validation result indicating if the pizza is valid
     */
    private OrderValidationResult validateSinglePizza(Pizza pizza) {
        Restaurant restaurant = restaurantService.findRestaurantByPizza(pizza.getName());
        if (restaurant == null) {
            return new OrderValidationResult(OrderValidationCode.PIZZA_NOT_DEFINED, OrderStatus.INVALID);
        }

        Pizza menuPizza = restaurant.findPizzaByName(pizza.getName());
        if (pizza.getPriceInPence() <= 0 || !pizza.getPriceInPence().equals(menuPizza.getPriceInPence())) {
            return new OrderValidationResult(OrderValidationCode.PRICE_FOR_PIZZA_INVALID, OrderStatus.INVALID);
        }

        return new OrderValidationResult(OrderValidationCode.NO_ERROR, OrderStatus.VALID);
    }

    /**
     * Validates restaurant-specific rules:
     * - All pizzas must be from the same restaurant
     * - Restaurant must be open on the order date
     */
    private OrderValidationResult validateRestaurant(Order order) {
        Restaurant restaurant = inferRestaurant(order.getPizzasInOrder());
        order.setRestaurant(restaurant);

        if (restaurant == null) {
            return new OrderValidationResult(OrderValidationCode.PIZZA_FROM_MULTIPLE_RESTAURANTS, OrderStatus.INVALID);
        }
        if (!restaurant.isOpenOn(order.getOrderDate())) {
            return new OrderValidationResult(OrderValidationCode.RESTAURANT_CLOSED, OrderStatus.INVALID);
        }
        return new OrderValidationResult(OrderValidationCode.NO_ERROR, OrderStatus.VALID);
    }

    // Validates that the total price matches the sum of pizza prices plus delivery fee.
    private OrderValidationResult validatePricing(Order order) {
        int calculatedPrice = order.getPizzasInOrder().stream()
                .mapToInt(Pizza::getPriceInPence)
                .sum() + DELIVERY_FEE_PENCE;
                
        // Ternary operator to act diff if the total price matches the calculated price
        return order.getPriceTotalInPence() == calculatedPrice
            ? new OrderValidationResult(OrderValidationCode.NO_ERROR, OrderStatus.VALID)
            : new OrderValidationResult(OrderValidationCode.TOTAL_INCORRECT, OrderStatus.INVALID);
    }

    
    // Validates payment information including credit card number, expiry date, and CVV.
    private OrderValidationResult validatePayment(Order order) {
        CreditCardInformation card = order.getCreditCardInformation();
        
        if (!card.isValidCreditCardNumber()) {
            return new OrderValidationResult(OrderValidationCode.CARD_NUMBER_INVALID, OrderStatus.INVALID);
        }
        if (!card.isValidExpiryDate()) {
            return new OrderValidationResult(OrderValidationCode.EXPIRY_DATE_INVALID, OrderStatus.INVALID);
        }
        if (!card.isValidCvv()) {
            return new OrderValidationResult(OrderValidationCode.CVV_INVALID, OrderStatus.INVALID);
        }
        
        return new OrderValidationResult(OrderValidationCode.NO_ERROR, OrderStatus.VALID);
    }

    /**
     * Determines which restaurant an order is from by checking all pizzas.
     * Returns null if pizzas are from different restaurants.
     * 
     * @param pizzas List of pizzas to check
     * @return The restaurant if all pizzas are from the same one, null otherwise
     */
    private Restaurant inferRestaurant(List<Pizza> pizzas) {
        if (pizzas.isEmpty()) return null;
        
        Restaurant foundRestaurant = restaurantService.findRestaurantByPizza(pizzas.get(0).getName());
        return pizzas.stream()
                .allMatch(pizza -> restaurantService.findRestaurantByPizza(pizza.getName()) == foundRestaurant)
                ? foundRestaurant 
                : null;
    }

    // Functional interface for validation steps
    @FunctionalInterface
    private interface ValidationStep {
        OrderValidationResult validate(Order order);
    }
} 
