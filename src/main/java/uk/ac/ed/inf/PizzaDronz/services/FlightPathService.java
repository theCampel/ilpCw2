package uk.ac.ed.inf.PizzaDronz.services;

import org.springframework.stereotype.Service;

import uk.ac.ed.inf.PizzaDronz.constants.SystemConstants;
import uk.ac.ed.inf.PizzaDronz.models.*;

import java.util.ArrayList;
import java.util.List;

@Service
public class FlightPathService {
    private final List<Region> noFlyZones;
    private final Region centralRegion;
    
    public FlightPathService() {
        this.noFlyZones = initializeNoFlyZones();
        this.centralRegion = initializeCentralRegion();
    }
    
    // TODO: This is hardcoded. Get dynamically from endpoint /noFlyZones
    
    private Region initializeCentralRegion() {
        return new Region(SystemConstants.CENTRAL_REGION_NAME, new LngLat[]{
            new LngLat(-3.192473, 55.946233),
            new LngLat(-3.184319, 55.946233), 
            new LngLat(-3.184319, 55.942617),
            new LngLat(-3.192473, 55.942617),
            new LngLat(-3.192473, 55.946233)
        });
    }
    
    private List<Region> initializeNoFlyZones() {
        List<Region> zones = new ArrayList<>();
        
        // George Square Area
        zones.add(new Region("George Square Area", new LngLat[]{
            new LngLat(-3.190578818321228, 55.94402412577528),
            new LngLat(-3.1899887323379517, 55.94284650540911),
            new LngLat(-3.187097311019897, 55.94328811724263),
            new LngLat(-3.187682032585144, 55.944477740393744),
            new LngLat(-3.190578818321228, 55.94402412577528)
        }));
        
        // Dr Elsie Inglis Quadrangle
        zones.add(new Region("Dr Elsie Inglis Quadrangle", new LngLat[]{
            new LngLat(-3.1907182931900024, 55.94519570234043),
            new LngLat(-3.1906163692474365, 55.94498241796357),
            new LngLat(-3.1900262832641597, 55.94507554227258),
            new LngLat(-3.190133571624756, 55.94529783810495),
            new LngLat(-3.1907182931900024, 55.94519570234043)
        }));
        
        // Bristo Square Open Area
        zones.add(new Region("Bristo Square Open Area", new LngLat[]{
            new LngLat(-3.189543485641479, 55.94552313663306),
            new LngLat(-3.189382553100586, 55.94553214854692),
            new LngLat(-3.189259171485901, 55.94544803726933),
            new LngLat(-3.1892001628875732, 55.94533688994374),
            new LngLat(-3.189194798469543, 55.94519570234043),
            new LngLat(-3.189135789871216, 55.94511759833873),
            new LngLat(-3.188138008117676, 55.9452738061846),
            new LngLat(-3.1885510683059692, 55.946105902745614),
            new LngLat(-3.1895381212234497, 55.94555918427592),
            new LngLat(-3.189543485641479, 55.94552313663306)
        }));
        
        // Bayes Central Area
        zones.add(new Region("Bayes Central Area", new LngLat[]{
            new LngLat(-3.1876927614212036, 55.94520696732767),
            new LngLat(-3.187555968761444, 55.9449621408666),
            new LngLat(-3.186981976032257, 55.94505676722831),
            new LngLat(-3.1872327625751495, 55.94536993377657),
            new LngLat(-3.1874459981918335, 55.9453361389472),
            new LngLat(-3.1873735785484314, 55.94519344934259),
            new LngLat(-3.1875935196876526, 55.94515665035927),
            new LngLat(-3.187624365091324, 55.94521973430925),
            new LngLat(-3.1876927614212036, 55.94520696732767)
        }));
        
        return zones;
    }
    
    public List<Region> getNoFlyZones() {
        return noFlyZones;
    }

    public Region getCentralRegion() {
        return centralRegion;
    }
}
