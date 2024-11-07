package uk.ac.ed.inf.PizzaDronz.models;

import uk.ac.ed.inf.PizzaDronz.constants.DayOfWeek;
import java.util.List;
import java.util.Set;

public class Restaurant {
    private String name;
    private LngLat location;
    private Set<DayOfWeek> openingDays;  // Using Set to avoid duplicates
    private List<Pizza> menu;

    // Default constructor for JSON deserialization
    public Restaurant() {
    }

    // Constructor
    public Restaurant(String name, LngLat location, Set<DayOfWeek> openingDays, List<Pizza> menu) {
        this.name = name;
        this.location = location;
        this.openingDays = openingDays;
        this.menu = menu;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LngLat getLocation() {
        return location;
    }

    public void setLocation(LngLat location) {
        this.location = location;
    }

    public Set<DayOfWeek> getOpeningDays() {
        return openingDays;
    }

    public void setOpeningDays(Set<DayOfWeek> openingDays) {
        this.openingDays = openingDays;
    }

    public List<Pizza> getMenu() {
        return menu;
    }

    public void setMenu(List<Pizza> menu) {
        this.menu = menu;
    }

    // Validation method
    public boolean isValid() {
        return name != null && !name.trim().isEmpty() &&
               location != null && location.isValid() &&
               openingDays != null && !openingDays.isEmpty() &&
               menu != null && !menu.isEmpty();
    }

    // Helper method to check if restaurant is open on a specific day
    public boolean isOpenOn(DayOfWeek day) {
        return openingDays.contains(day);
    }
} 