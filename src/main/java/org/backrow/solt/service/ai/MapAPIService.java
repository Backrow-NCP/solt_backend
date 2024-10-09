package org.backrow.solt.service.ai;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

@Service
public class MapAPIService {

    private final RestTemplate restTemplate;

    public MapAPIService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Integer getTravelTime(String origin, String destination) {
        String googleMapsApiUrl = "Input Google Map API Key";
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(googleMapsApiUrl)
                .queryParam("origins", origin)
                .queryParam("destinations", destination)
                .queryParam("key", "YOUR_GOOGLE_MAPS_API_KEY"); // Google Maps API Key

        ResponseEntity<Map> response = restTemplate.getForEntity(uriBuilder.toUriString(), Map.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            Map<String, Object> body = response.getBody();

            // 이동 시간 추출
            List<Map<String, Object>> rows = (List<Map<String, Object>>) body.get("rows");
            List<Map<String, Object>> elements = (List<Map<String, Object>>) rows.get(0).get("elements");
            Map<String, Object> duration = (Map<String, Object>) elements.get(0).get("duration");
            Integer totalSeconds = (Integer) duration.get("value"); // 초 단위의 시간 반환

            return totalSeconds / 60; // 분 단위로 반환
        }

        throw new RuntimeException("Failed to fetch travel time from Google Maps API");
    }
}
