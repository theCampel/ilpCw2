package uk.ac.ed.inf.PizzaDronz.models;

public class Pizza {
    private String name;
    private Integer priceInPence;

    // Constructor
    public Pizza(String name, Integer priceInPence) {
        this.name = name;
        this.priceInPence = priceInPence;
    }

    // Default constructor for JSON deserialization
    public Pizza() {
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPriceInPence() {
        return priceInPence;
    }

    public void setPriceInPence(Integer priceInPence) {
        this.priceInPence = priceInPence;
    }

    public boolean isValid() {
        return name != null && !name.isEmpty() && priceInPence > 0;
    }
} 
