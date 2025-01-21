package uk.ac.ed.inf.PizzaDronz.models;

import java.util.List;

import uk.ac.ed.inf.PizzaDronz.constants.OrderStatus;
import uk.ac.ed.inf.PizzaDronz.constants.OrderValidationCode;

public class Order {
    private String orderNo;
    private String orderDate;
    private Integer priceTotalInPence;
    private List<Pizza> pizzasInOrder;
    private CreditCardInformation creditCardInformation;
    private Restaurant restaurant;
    private OrderValidationCode orderValidationCode;
    private OrderStatus orderStatus;

    // The default constructor that actually gets used by JSON deserialisation
    public Order() {
    }

    // This constructor doesn't actually get used, but here for clarity
    public Order(String orderNo, String orderDate, Integer priceTotalInPence, List<Pizza> pizzasInOrder, CreditCardInformation creditCardInformation) {
        this.orderNo = orderNo;
        this.orderDate = orderDate;
        this.priceTotalInPence = priceTotalInPence;
        this.pizzasInOrder = pizzasInOrder;
        this.creditCardInformation = creditCardInformation;
    }

    // Getters and Setters
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

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public OrderValidationCode getOrderValidationCode() {
        return orderValidationCode;
    }   

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderValidationCode(OrderValidationCode orderValidationCode) {
        this.orderValidationCode = orderValidationCode;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public boolean isValid() {
        return orderStatus == OrderStatus.VALID && orderValidationCode == OrderValidationCode.NO_ERROR;
    }
}
