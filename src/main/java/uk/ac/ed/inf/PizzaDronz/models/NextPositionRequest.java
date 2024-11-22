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

    private boolean isValidAngle(int angle) {
        double[] validAngles = {0, 22.5, 45, 67.5, 90, 112.5, 135, 157.5, 180, 202.5, 225, 247.5, 270, 292.5, 315, 337.5};
        for (double validAngle : validAngles) {
            if (angle == validAngle) {
                return true;
            }
        }
        return false;
    }

    // Validation method to check if the request is valid
    public boolean isValid() {
        return start != null && start.isValid() && isValidAngle(angle);
    }
}