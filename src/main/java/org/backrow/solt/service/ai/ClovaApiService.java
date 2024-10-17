package org.backrow.solt.service.ai;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.backrow.solt.domain.plan.api.DirectionsResponses;
import org.backrow.solt.dto.plan.PlaceDTO;
import org.backrow.solt.dto.plan.PlanInputDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ClovaApiService {

    // 외부에서 주입되는 API 키 값
    @Value("${clova.api.key}")
    private String clovaApiKey;

    @Value("${clova.apigw.api.key}")
    private String clovaApiGatewayKey;

    @Value("${clova.api.url}")
    private String clovaApiUrl;

    @Value("${clova.request.id}")
    private String clovaRequestId;

    private final RestTemplate restTemplate;

    // 생성자 주입 방식 사용
    public ClovaApiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // Clova API 요청을 보내는 메소드
    public DirectionsResponses callClovaApi(String requestBody) {
        // Clova API에 보낼 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-NCP-CLOVASTUDIO-API-KEY", clovaApiKey);
        headers.set("X-NCP-APIGW-API-KEY", clovaApiGatewayKey);
        headers.set("X-NCP-CLOVASTUDIO-REQUEST-ID", clovaRequestId);
        headers.set("Content-Type", "application/json");

        // 요청 엔티티 생성
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        // Clova API에 POST 요청을 보내고 응답을 받음
        ResponseEntity<DirectionsResponses> response = restTemplate.postForEntity(clovaApiUrl, entity, DirectionsResponses.class);

        // 응답 상태 확인 및 예외 처리
        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        } else {
            throw new RuntimeException("Error getting response from Clova API: " + response.getStatusCode().toString());
        }
    }

    // PlanInputDTO를 기반으로 Clova API에 보낼 요청 바디를 생성하는 메소드
    public static String createClovaRequestBody(PlanInputDTO planInputDTO) {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> requestBody = new HashMap<>();

        // 장소 정보 설정
        List<Map<String, Object>> places = new ArrayList<>();
        for (PlaceDTO place : planInputDTO.getPlaces()) {
            Map<String, Object> placeInfo = new HashMap<>();
            placeInfo.put("placeId", place.getPlaceId());
            placeInfo.put("placeName", place.getPlaceName());
            placeInfo.put("addr", place.getAddr());
            placeInfo.put("price", place.getPrice());
            placeInfo.put("startTime", place.getStartTime().toString());
            placeInfo.put("endTime", place.getEndTime().toString());
            placeInfo.put("description", place.getDescription());
            placeInfo.put("checker", place.getChecker());

            places.add(placeInfo);
        }
        requestBody.put("places", places);

        // 테마 정보 설정
        requestBody.put("themes", planInputDTO.getThemes());
        requestBody.put("location", planInputDTO.getLocation());
        requestBody.put("startDate", planInputDTO.getStartDate().toString());
        requestBody.put("endDate", planInputDTO.getEndDate().toString());

        try {
            return objectMapper.writeValueAsString(requestBody);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error creating request body for Clova API: " + e.getMessage());
        }
    }
}
