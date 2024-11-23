package uk.ac.ed.inf.PizzaDronz.services;

import org.springframework.stereotype.Service;
import uk.ac.ed.inf.PizzaDronz.models.Order;
import uk.ac.ed.inf.PizzaDronz.models.OrderValidationResult;
import uk.ac.ed.inf.PizzaDronz.constants.OrderValidationCode;
import uk.ac.ed.inf.PizzaDronz.constants.OrderStatus;
import uk.ac.ed.inf.PizzaDronz.models.Pizza;
import uk.ac.ed.inf.PizzaDronz.models.Restaurant;
import uk.ac.ed.inf.PizzaDronz.models.CreditCardInformation;
import uk.ac.ed.inf.PizzaDronz.constants.SystemConstants;
import uk.ac.ed.inf.PizzaDronz.interfaces.OrderValidation;

import java.util.List;

/**
 * Service class responsible for handling pizza order operations and validation.
 */
@Service
public class OrderService implements OrderValidation {
    private static final int MAX_PIZZAS_PER_ORDER = SystemConstants.MAX_PIZZAS_PER_ORDER;
    private static final int DELIVERY_FEE_PENCE = SystemConstants.ORDER_CHARGE_IN_PENCE;  // £1.00 delivery fee

    private final RestaurantService restaurantService;

    /**
     * Constructs an OrderService with required dependencies.
     * @param restaurantService Service to handle restaurant-related operations
     */
    public OrderService(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    @Override
    public Order validateOrder(Order orderToValidate, Restaurant[] definedRestaurants) {
        OrderValidationResult result = validateOrder(orderToValidate);
        
        // Update the order with validation results
        orderToValidate.setOrderStatus(result.getOrderStatus());
        orderToValidate.setOrderValidationCode(result.getValidationCode());
        
        return orderToValidate;
    }

    /**
     * Validates an order by checking multiple criteria including:
     * - Basic order details (non-empty, within size limits)
     * - Pizza validity (existence, pricing)
     * - Restaurant availability
     * - Credit card information
     * - Total price accuracy
     *
     * @param order The order to validate
     * @return OrderValidationResult containing validation status and any error codes
     */
    public OrderValidationResult validateOrder(Order order) {
        restaurantService.updateRestaurants(); // Get fresh data before validating
        
        // Basic order validation
        OrderValidationResult basicValidation = validateBasicOrderDetails(order);
        if (basicValidation.getOrderStatus() == OrderStatus.INVALID) {
            return basicValidation;
        }

        // Pizza validation
        OrderValidationResult pizzaValidation = validatePizzas(order.getPizzasInOrder());
        if (pizzaValidation.getOrderStatus() == OrderStatus.INVALID) {
            return pizzaValidation;
        }

        // Restaurant validation
        Restaurant restaurant = inferRestaurant(order.getPizzasInOrder());
        order.setRestaurant(restaurant);
        
        OrderValidationResult restaurantValidation = validateRestaurant(restaurant, order.getOrderDate());
        if (restaurantValidation.getOrderStatus() == OrderStatus.INVALID) {
            return restaurantValidation;
        }


        // Credit card validation
        OrderValidationResult cardValidation = validateCreditCard(order.getCreditCardInformation());
        if (cardValidation.getOrderStatus() == OrderStatus.INVALID) {
            return cardValidation;
        }

        // Price validation
        if (!isPriceCorrect(order)) {
            return new OrderValidationResult(OrderValidationCode.TOTAL_INCORRECT, OrderStatus.INVALID);
        }


        return new OrderValidationResult(OrderValidationCode.NO_ERROR, OrderStatus.VALID);
    }

    /**
     * Validates basic order requirements such as non-empty order and maximum pizza count.
     * @param order The order to validate
     * @return Validation result indicating success or specific failure
     */
    private OrderValidationResult validateBasicOrderDetails(Order order) {
        if (order.getPizzasInOrder() == null || order.getPizzasInOrder().isEmpty()) {
            return new OrderValidationResult(OrderValidationCode.EMPTY_ORDER, OrderStatus.INVALID);
        }
        if (order.getPizzasInOrder().size() > MAX_PIZZAS_PER_ORDER) {
            return new OrderValidationResult(OrderValidationCode.MAX_PIZZA_COUNT_EXCEEDED, OrderStatus.INVALID);
        }
        return new OrderValidationResult(OrderValidationCode.NO_ERROR, OrderStatus.VALID);
    }

    /**
     * Validates all pizzas in an order by checking each individual pizza.
     * @param pizzas List of pizzas to validate
     * @return First encountered error or success if all pizzas are valid
     */
    private OrderValidationResult validatePizzas(List<Pizza> pizzas) {
        return pizzas.stream()
                .map(this::validateSinglePizza)
                .filter(result -> result.getOrderStatus() == OrderStatus.INVALID)
                .findFirst()
                .orElse(new OrderValidationResult(OrderValidationCode.NO_ERROR, OrderStatus.VALID));
    }

    /**
     * Validates a single pizza by checking if it exists in a restaurant's menu
     * and if its price matches the menu price.
     * @param pizza Pizza to validate
     * @return Validation result for the single pizza
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
     * Validates restaurant-specific conditions:
     * - Restaurant exists (not null)
     * - Restaurant is open on the order date
     * @param restaurant Restaurant to validate
     * @param orderDate Date of the order
     * @return Validation result for restaurant conditions
     */
    private OrderValidationResult validateRestaurant(Restaurant restaurant, String orderDate) {
        if (restaurant == null) {
            return new OrderValidationResult(OrderValidationCode.PIZZA_FROM_MULTIPLE_RESTAURANTS, OrderStatus.INVALID);
        }
        if (!restaurant.isOpenOn(orderDate)) {
            return new OrderValidationResult(OrderValidationCode.RESTAURANT_CLOSED, OrderStatus.INVALID);
        }
        return new OrderValidationResult(OrderValidationCode.NO_ERROR, OrderStatus.VALID);
    }

    /**
     * Validates credit card information including:
     * - Card number validity
     * - Expiry date validity
     * - CVV validity
     * @param card Credit card information to validate
     * @return Validation result for credit card details
     */
    private OrderValidationResult validateCreditCard(CreditCardInformation card) {
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
     * Determines the restaurant for an order based on the pizzas ordered.
     * All pizzas must be from the same restaurant.
     * @param pizzas List of pizzas in the order
     * @return The restaurant if all pizzas are from the same place, null otherwise
     */
    private Restaurant inferRestaurant(List<Pizza> pizzas) {
        if (pizzas.isEmpty()) return null;
        
        Restaurant firstRestaurant = restaurantService.findRestaurantByPizza(pizzas.get(0).getName());
        return pizzas.stream()
                .map(pizza -> restaurantService.findRestaurantByPizza(pizza.getName()))
                .allMatch(restaurant -> restaurant == firstRestaurant) ? firstRestaurant : null;
    }

    /**
     * Verifies if the total price provided in the order matches the calculated total.
     * @param order Order to verify price for
     * @return true if price matches, false otherwise
     */
    private boolean isPriceCorrect(Order order) {
        int calculatedTotal = calculatePriceTotalInPence(order.getPizzasInOrder());
        int providedTotal = order.getPriceTotalInPence();
        return providedTotal == calculatedTotal;
    }

    /**
     * Calculates the total price of an order including delivery fee.
     * @param pizzas List of pizzas to calculate total for
     * @return Total price in pence
     */
    private Integer calculatePriceTotalInPence(List<Pizza> pizzas) {
        int pizzaTotal = pizzas.stream()
                .mapToInt(Pizza::getPriceInPence)
                .sum();
        return pizzaTotal + DELIVERY_FEE_PENCE;
    }
} 
