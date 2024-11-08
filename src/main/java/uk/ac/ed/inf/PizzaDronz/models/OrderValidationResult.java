package uk.ac.ed.inf.PizzaDronz.models;

import uk.ac.ed.inf.PizzaDronz.constants.OrderStatus;
import uk.ac.ed.inf.PizzaDronz.constants.OrderValidationCode;

public class OrderValidationResult {

    private OrderValidationCode validationCode;
    private OrderStatus orderStatus;

    public OrderValidationResult(OrderValidationCode validationCode, OrderStatus orderStatus) {
        this.validationCode = validationCode;
        this.orderStatus = orderStatus;
    }

    public OrderValidationCode getValidationCode() {
        return validationCode;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }   

    
}
