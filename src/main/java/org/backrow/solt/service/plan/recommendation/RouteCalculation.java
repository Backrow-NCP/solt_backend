package org.backrow.solt.service.plan.recommendation;

import org.backrow.solt.service.ai.GoogleMapsApiService;

public class RouteCalculation {

    private final GoogleMapsApiService googleMapsApiService;

    public RouteCalculation(GoogleMapsApiService googleMapsApiService) {
        this.googleMapsApiService = googleMapsApiService;
    }


}
