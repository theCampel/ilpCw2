package uk.ac.ed.inf.PizzaDronz.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.beans.factory.annotation.Autowired;

import uk.ac.ed.inf.PizzaDronz.constants.OrderValidationCode;
import uk.ac.ed.inf.PizzaDronz.services.RestaurantService;

import java.util.List;

public class Order {
    private String orderNo;
    private String orderDate;
    private Integer priceTotalInPence;
    private List<Pizza> pizzasInOrder;
    private CreditCardInformation creditCardInformation;
    
    @JsonIgnore
    private Restaurant restaurant;
    
    @Autowired
    @JsonIgnore
    private RestaurantService restaurantService;

    // Default constructor for JSON deserialization
    public Order() {
    }

    // Constructor
    public Order(String orderNo, String orderDate, Integer priceTotalInPence, List<Pizza> pizzasInOrder, CreditCardInformation creditCardInformation) {
        this.orderNo = orderNo;
        this.orderDate = orderDate;
        this.priceTotalInPence = priceTotalInPence;
        this.pizzasInOrder = pizzasInOrder;
        this.creditCardInformation = creditCardInformation;
        this.restaurant = getRestaurant();
    }

    // Getters and Setters
    @JsonIgnore
    public Restaurant getRestaurant() {
        if (restaurant != null) {
            return restaurant;
        }
        
        if (pizzasInOrder == null || pizzasInOrder.isEmpty()) {
            return null;
        }

        // Get restaurant from first pizza
        String firstPizzaName = pizzasInOrder.get(0).getName();
        Restaurant foundRestaurant = restaurantService.findRestaurantByPizza(firstPizzaName);

        // Verify all pizzas are from the same restaurant
        for (Pizza pizza : pizzasInOrder) {
            Restaurant pizzaRestaurant = restaurantService.findRestaurantByPizza(pizza.getName());
            if (pizzaRestaurant != foundRestaurant) {
                return null; // Pizzas from different restaurants
            }
        }

        this.restaurant = foundRestaurant;
        return foundRestaurant;
    }

    public String getOrderNo() {
        return orderNo;
    }   

    public String getOrderDate() {
        return orderDate;
    }

    public Integer getPriceTotalInPence() {
        return priceTotalInPence;
    }

    public List<Pizza> getPizzasInOrder() {
        return pizzasInOrder;
    }

    public CreditCardInformation getCreditCardInformation() {
        return creditCardInformation;
    }

    // Validation method
    public boolean isValid() {


        return orderNo != null && !orderNo.isEmpty() &&
               orderDate != null && !orderDate.isEmpty() && 
               priceTotalInPence != null && priceTotalInPence > 0 &&
               pizzasInOrder != null && !pizzasInOrder.isEmpty() &&
               creditCardInformation != null;
    }

    public OrderValidationCode validateOrder() {
        

}
