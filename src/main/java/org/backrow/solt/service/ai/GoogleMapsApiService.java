package org.backrow.solt.service.ai;

import org.backrow.solt.domain.plan.api.DirectionsResponses;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GoogleMapsApiService {

    @Value("${google.maps.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;

    public GoogleMapsApiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public DirectionsResponses getDirections(Long origin, Long destination) {
        String url = String.format(
                "https://maps.googleapis.com/maps/api/directions/json?origin=%s&destination=%s&mode=transit&key=%s",
                origin, destination, apiKey
        );

        ResponseEntity<DirectionsResponses> response = restTemplate.getForEntity(url, DirectionsResponses.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        } else {
            throw new RuntimeException("Failed to get directions from Google Maps API");
        }
    }
}
