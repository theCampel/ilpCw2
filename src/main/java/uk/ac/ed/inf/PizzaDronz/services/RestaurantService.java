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
    private final List<Restaurant> restaurants;
    private static final String RESTAURANTS_API_URL = SystemConstants.RESTAURANTS_API_URL;
    private final RestTemplate restTemplate;

    public RestaurantService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.restaurants = initialiseRestaurants();
    }

    private List<Restaurant> initialiseRestaurants() {
        try {
            Restaurant[] restaurantsArray = restTemplate.getForObject(RESTAURANTS_API_URL, Restaurant[].class);
            if (restaurantsArray == null) {
                throw new RuntimeException("Failed to fetch restaurants from API");
            }
            return new ArrayList<>(Arrays.asList(restaurantsArray));
        } catch (Exception e) {
            throw new RuntimeException("Error fetching restaurants: " + e.getMessage(), e);
        }
    }

    public Restaurant findRestaurantByPizza(String pizzaName) {
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
        return restaurants;
    }
} 