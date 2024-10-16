package org.backrow.solt.service.ai;

import org.backrow.solt.domain.plan.api.RoutesResponses;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GoogleMapsApiService {

    @Value("${google.maps.api.key}")
    private String googleMapsApiKey;

    private final RestTemplate restTemplate;

    public GoogleMapsApiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public RoutesResponses getRoutes(String origin, String destination) {
        // API 호출 URL 생성
        String url = String.format("https://maps.googleapis.com/maps/api/directions/json?origin=%s&destination=%s&key=%s",
                origin, destination, googleMapsApiKey);

        // API 요청 및 응답 받기
        ResponseEntity<RoutesResponses> response = restTemplate.getForEntity(url, RoutesResponses.class);

        // 응답 상태 코드 확인
        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            throw new RuntimeException("Error getting routes from Google Maps: " + response.getStatusCode());
        }
    }
}
