package uk.ac.ed.inf.PizzaDronz.models;

import uk.ac.ed.inf.PizzaDronz.constants.SystemConstants;

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
        double[] validAngles = SystemConstants.VALID_ANGLES;
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