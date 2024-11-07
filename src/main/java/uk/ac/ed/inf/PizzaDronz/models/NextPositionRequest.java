package uk.ac.ed.inf.PizzaDronz.models;

// Class representing a request to calculate the next position
public class NextPositionRequest {
    private LngLat start;
    private int angle;

    // Getters and Setters
    public LngLat getStart() {
        return start;
    }

    public void setStart(LngLat start) {
        this.start = start;
    }

    public int getAngle() {
        return angle;
    }

    public void setAngle(int angle) {
        this.angle = angle;
    }

    // Validation method to check if the request is valid
    public boolean isValid() {
        return start != null && start.isValid() && angle >= 0 && angle < 360;
    }
}