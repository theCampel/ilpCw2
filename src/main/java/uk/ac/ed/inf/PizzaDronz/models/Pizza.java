package uk.ac.ed.inf.PizzaDronz.models;

public class Pizza {
    private String name;
    private int price;

    // Constructor
    public Pizza(String name, int price) {
        this.name = name;
        this.price = price;
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

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public boolean isValid() {
        return name != null && !name.isEmpty() && price > 0;
    }
} 
