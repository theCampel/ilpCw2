package uk.ac.ed.inf.PizzaDronz.models;

public class InRegionRequest {
    private LngLat position;
    private Region region;

    public InRegionRequest() {}

    public InRegionRequest(LngLat position, Region region) {
        this.position = position;
        this.region = region;
    }

    public LngLat getPosition() {
        return position;
    }

    public Region getRegion() {
        return region;
    }

    public boolean isValid() {
        return position != null && position.isValid() && region != null && region.isValid();
    }
}
