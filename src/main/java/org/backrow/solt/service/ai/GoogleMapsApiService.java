package org.backrow.solt.service.ai;

import org.backrow.solt.domain.plan.Place;
import org.backrow.solt.domain.plan.Route;
import org.backrow.solt.domain.plan.TransportationType;
import org.backrow.solt.domain.plan.api.RoutesResponses;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;

@Service
public class GoogleMapsApiService {

    @Value("${google.maps.api.key}")
    private String googleMapsApiKey;

    private final RestTemplate restTemplate;

    public GoogleMapsApiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Route createRouteEntity(RoutesResponses routesResponses, Place startPlace, Place endPlace, TransportationType transportationType, LocalDate routeDate) {
        if (routesResponses != null && !routesResponses.getRoutes().isEmpty()) {
            RoutesResponses.Route firstRoute = routesResponses.getRoutes().get(0);
            if (!firstRoute.getLegs().isEmpty()) {
                RoutesResponses.Route.Leg firstLeg = firstRoute.getLegs().get(0);

                // Extracting necessary information
                int travelTime = (firstLeg.getDuration().getValue())/60; // 이동 시간 (분 단위)
                int distance = (firstLeg.getDistance().getValue())/1000 + (firstLeg.getDistance().getValue())%1000; // 이동 거리 (미터 단위)

                // Create and return the Route entity
                return Route.builder()
                        .startPlaceId(startPlace) // Start place entity
                        .endPlaceId(endPlace) // End place entity
                        .date(routeDate) // Route date
                        .price(0) // Default price, you might want to extract this if available
                        .transportationType(transportationType) // Transportation type entity
                        .distance(distance) // Distance
                        .travelTime(travelTime) // Travel time
                        .build();
            }
        }
        throw new RuntimeException("No valid routes found to create Route.");
    }

}
