package uk.ac.ed.inf.PizzaDronz.services;

import uk.ac.ed.inf.PizzaDronz.models.Restaurant;
import uk.ac.ed.inf.PizzaDronz.models.Pizza;
import uk.ac.ed.inf.PizzaDronz.models.LngLat;
import uk.ac.ed.inf.PizzaDronz.constants.DayOfWeek;
import uk.ac.ed.inf.PizzaDronz.constants.SystemConstants;

import org.springframework.stereotype.Service;
import java.util.*;
import org.springframework.web.client.RestTemplate;

@Service
public class RestaurantService {
    private volatile List<Restaurant> restaurants;
    private final RestTemplate restTemplate;

    public RestaurantService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void updateRestaurants() {
        try {
            Restaurant[] restaurantsArray = restTemplate.getForObject(SystemConstants.RESTAURANTS_API_URL, Restaurant[].class);
            if (restaurantsArray == null) {
                throw new RuntimeException("Failed to fetch restaurants from API");
            }
            this.restaurants = new ArrayList<>(Arrays.asList(restaurantsArray));
        } catch (Exception e) {
            throw new RuntimeException("Error fetching restaurants: " + e.getMessage(), e);
        }
        System.out.println("Updated restaurants");
        System.out.println(restaurants);
    }

    public Restaurant findRestaurantByPizza(String pizzaName) {
        //updateRestaurants();  // Get fresh data before searching
        for (Restaurant restaurant : restaurants) {
            for (Pizza pizza : restaurant.getMenu()) {
                if (pizza.getName().equals(pizzaName)) {
                    return restaurant;
                }
            }
        }
        return null;
    }

    public List<Restaurant> getAllRestaurants() {
        //updateRestaurants();  // Get fresh data before returning
        return restaurants;
    }
} 