package uk.ac.ed.inf.PizzaDronz.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class LngLat {
    private Double lng;
    private Double lat;

    // Constructor to initialize LngLat with longitude and latitude
    public LngLat(Double lng, Double lat) {
        this.lng = lng;
        this.lat = lat;
    }

    // Getters and Setters
    public Double getLng() {
        return lng;
    }

    public Double getLat() {
        return lat;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    // Check if a pair of longitude and latitude is are equals
    public boolean equals(LngLat other){
        return lng.equals(other.getLng()) && lat.equals(other.getLat());
    }

    // Use @JSONIgnore cos otherwise it would be included in the JSON response
    @JsonIgnore
    public boolean isValid(){
        return lng != null && lat != null &&
                lng >= -180 && lng <= 180 &&
                lat >= -180 && lat <= 180;
    }

}
