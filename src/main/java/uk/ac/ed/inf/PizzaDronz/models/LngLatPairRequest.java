package uk.ac.ed.inf.PizzaDronz.models;

public class LngLatPairRequest {
    private LngLat position1 = null;
    private LngLat position2 = null;

    // Getters and Setters
    public LngLat getPosition1() {
        return position1;
    }

    public LngLat getPosition2() {
        return position2;
    }

    public void setPosition1(LngLat position1) {
        this.position1 = position1;
    }

    public void setPosition2(LngLat position2) {
        this.position2 = position2;
    }
    public boolean isValid(){
        return position1 != null && position2 != null && position1.isValid() && position2.isValid();
    }
}
