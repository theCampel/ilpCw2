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

@Service
public class OrderService {
    private static final int MAX_PIZZAS_PER_ORDER = 4;
    private static final int DELIVERY_FEE_PENCE = 100;
    
    private final RestaurantService restaurantService;

    public OrderService(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    public OrderValidationResult validateOrder(Order order) {
        if (order.getPizzasInOrder() == null || order.getPizzasInOrder().isEmpty()) {
            return new OrderValidationResult(OrderValidationCode.EMPTY_ORDER, OrderStatus.INVALID);
        }
        if (order.getPizzasInOrder().size() > MAX_PIZZAS_PER_ORDER) {
            return new OrderValidationResult(OrderValidationCode.MAX_PIZZA_COUNT_EXCEEDED, OrderStatus.INVALID);
        }


        
        for (Pizza pizza : order.getPizzasInOrder()) {

            if (restaurantService.findRestaurantByPizza(pizza.getName()) == null) {
                return new OrderValidationResult(OrderValidationCode.PIZZA_NOT_DEFINED, OrderStatus.INVALID);
            }
            
            // Checks the price is valid and that it matches the price of the pizza in the restaurant's menu
            // TODO: This is a horrible way to do this. Fix.
            if (pizza.getPriceInPence() <= 0 
                || !pizza.getPriceInPence()
                    .equals(restaurantService.findRestaurantByPizza(pizza.getName())
                    .findPizzaByName(pizza.getName())
                    .getPriceInPence())) {
                return new OrderValidationResult(OrderValidationCode.PRICE_FOR_PIZZA_INVALID, OrderStatus.INVALID);
            }
        }


        Restaurant restaurant = inferRestaurant(order.getPizzasInOrder());
        order.setRestaurant(restaurant);

        if (restaurant == null) {
            return new OrderValidationResult(OrderValidationCode.PIZZA_FROM_MULTIPLE_RESTAURANTS, OrderStatus.INVALID);
        }

        if (!restaurant.isOpenOn(order.getOrderDate())) {
            return new OrderValidationResult(OrderValidationCode.RESTAURANT_CLOSED, OrderStatus.INVALID);
        }


        if (!isPriceCorrect(order)) {
            return new OrderValidationResult(OrderValidationCode.TOTAL_INCORRECT, OrderStatus.INVALID);
        }

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

    private Restaurant inferRestaurant(List<Pizza> pizzas) {
        if (pizzas.isEmpty()) return null; // Should never happen
        
        Restaurant foundRestaurant = restaurantService.findRestaurantByPizza(pizzas.get(0).getName());
        
        // Verify all pizzas are from the same restaurant
        return pizzas.stream()
                .allMatch(pizza -> restaurantService.findRestaurantByPizza(pizza.getName()) == foundRestaurant)
                ? foundRestaurant 
                : null;
    }




    // private boolean doesPizzaExistInAnyRestaurant(Pizza pizza) {
    //     return restaurantService.findRestaurantByPizza(pizza.getName()) != null;
    // }

    private boolean isPriceCorrect(Order order) {
        int calculatedPrice = calculatePriceTotalInPence(order.getPizzasInOrder());
        return order.getPriceTotalInPence() == calculatedPrice;
    }

    private Integer calculatePriceTotalInPence(List<Pizza> pizzas) {
        return pizzas.stream()
                .mapToInt(Pizza::getPriceInPence)
                .sum() + DELIVERY_FEE_PENCE; // Delivery fee
    }
} 
