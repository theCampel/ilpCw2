package uk.ac.ed.inf.PizzaDronz.services;

import uk.ac.ed.inf.PizzaDronz.models.Restaurant;
import uk.ac.ed.inf.PizzaDronz.models.Pizza;
import uk.ac.ed.inf.PizzaDronz.models.LngLat;
import uk.ac.ed.inf.PizzaDronz.constants.DayOfWeek;

import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class RestaurantService {
    private final List<Restaurant> restaurants;

    public RestaurantService() {
        this.restaurants = initializeRestaurants();
    }

    private List<Restaurant> initializeRestaurants() {
        List<Restaurant> restaurants = new ArrayList<>();
        
        // Civerinos Slice
        restaurants.add(new Restaurant(
            "Civerinos Slice",
            new LngLat(-3.1912869215011597, 55.945535152517735),
            new HashSet<>(Arrays.asList(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)),
            Arrays.asList(
                new Pizza("R1: Margarita", 1000),
                new Pizza("R1: Calzone", 1400)
            )
        ));
        
        restaurants.add(new Restaurant(
            "Sora Lella Vegan Restaurant",
            new LngLat(-3.202541470527649, 55.943284737579376),
            new HashSet<>(Arrays.asList(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY)),
            Arrays.asList(
                new Pizza("R2: Meat Lover", 1400),
                new Pizza("R2: Vegan Delight", 1100)
            )
        ));
        
        restaurants.add(new Restaurant(
            "Domino's Pizza - Edinburgh - Southside",
            new LngLat(-3.1838572025299072, 55.94449876875712),
            new HashSet<>(Arrays.asList(DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)),
            Arrays.asList(
                new Pizza("R3: Super Cheese", 1400),
                new Pizza("R3: All Shrooms", 900)
            )
        ));
        
        restaurants.add(new Restaurant(
            "Sodeberg Pavillion",
            new LngLat(-3.1940174102783203, 55.94390696616939),
            new HashSet<>(Arrays.asList(DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)),
            Arrays.asList(
                new Pizza("R4: Proper Pizza", 1400),
                new Pizza("R4: Pineapple & Ham & Cheese", 900)
            )
        ));
        
        restaurants.add(new Restaurant(
            "La Trattoria",
            new LngLat(-3.1810810679852035, 55.938910643735845),
            new HashSet<>(Arrays.asList(DayOfWeek.MONDAY, DayOfWeek.THURSDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)),
            Arrays.asList(
                new Pizza("R5: Pizza Dream", 1400),
                new Pizza("R5: My kind of pizza", 900)
            )
        ));
        
        restaurants.add(new Restaurant(
            "Halal Pizza",
            new LngLat(-3.185428203143916, 55.945846113595),
            new HashSet<>(Arrays.asList(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)),
            Arrays.asList(
                new Pizza("R6: Sucuk delight", 1400),
                new Pizza("R6: Dreams of Syria", 900)
            )
        ));
        
        restaurants.add(new Restaurant(
            "World of Pizza",
            new LngLat(-3.179798972064253, 55.939884084483),
            new HashSet<>(Arrays.asList(DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.TUESDAY)),
            Arrays.asList(
                new Pizza("R7: Hot, hotter, the hottest", 1400),
                new Pizza("R7: All you ever wanted", 900)
            )
        ));
        return restaurants;
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