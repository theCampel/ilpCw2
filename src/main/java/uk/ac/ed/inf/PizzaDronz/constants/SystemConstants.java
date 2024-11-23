package uk.ac.ed.inf.PizzaDronz.constants;

public final class SystemConstants {

    /**
     * the charge for any order
     */
    public static final int ORDER_CHARGE_IN_PENCE = 100;

    /**
     * the maximum number of pizzas in one order
     */
    public static final int MAX_PIZZAS_PER_ORDER = 4;

    /**
     * the distance a drone can move in 1 iteration
     */
    public static final double DRONE_MOVE_DISTANCE = 0.00015;

    /**
     * the distance which is considered "close"
     */
    public static final double DRONE_IS_CLOSE_DISTANCE = 0.00015;

    /**
     * the maximum moves a drone can make before running out of battery
     */
    public static final int DRONE_MAX_MOVES = 2000;

    /**
     * the central region name
     */
    public static final String CENTRAL_REGION_NAME = "central";

    /**
     * Longitude AT
     */
    public static final double APPLETON_LNG = -3.186874;

    /**
     * Latitude AT
     */
    public static final double APPLETON_LAT = 55.944494;

    /**
     * Valid angles the drone can fly at
     */
    public static final double[] VALID_ANGLES = {0, 22.5, 45, 67.5, 90, 
                                                112.5, 135, 157.5, 180, 
                                                202.5, 225, 247.5, 270, 
                                                292.5, 315, 337.5};

    /**
     * The buffer distance for no-fly zones
     */
    public static final double NO_FLY_ZONE_BUFFER = 0.0003;

    // API Endpoints
    public static final String BASE_API_URL = "https://ilp-rest-2024.azurewebsites.net";
    public static final String RESTAURANTS_API_URL = BASE_API_URL + "/restaurants";
    public static final String NO_FLY_ZONES_API_URL = BASE_API_URL + "/noFlyZones";

}