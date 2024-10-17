package org.backrow.solt.service.ai;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.backrow.solt.domain.plan.Place;
import org.backrow.solt.domain.plan.api.RoutesResponses;
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

    @Value("${clova.api.key}")
    private String clovaApiKey;

    @Value("${clova.apigw.key}")
    private String clovaRequestId;

    private final RestTemplate restTemplate;

    public ClovaApiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public RoutesResponses callClovaApi(String requestBody) {
        String url = "https://clovastudio.stream.ntruss.com/testapp/v1/chat-completions/HCX-003";

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-NCP-CLOVASTUDIO-API-KEY", clovaApiKey);
        headers.set("X-NCP-APIGW-API-KEY", "j96OgrLz3aBC78T9HgjgsQvcg5EhYxgWOT0Ot4RK");
        headers.set("X-NCP-CLOVASTUDIO-REQUEST-ID", clovaRequestId);
        headers.set("Content-Type", "application/json");

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<RoutesResponses> response = restTemplate.postForEntity(url, entity, RoutesResponses.class);

        if(response.getStatusCode().is2xxSuccessful()){
            return response.getBody();
        }else{
            throw new RuntimeException("Error getting response from Clova API: " + response.getStatusCode().toString());
        }
    }

    public String createClovaRequestBody(PlanInputDTO planInputDTO){

        // json 형식의 요청 파일 생성
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> requestBody = new HashMap<>();

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

        // Themes 정보 설정
        requestBody.put("themes", planInputDTO.getThemes());

        try{
            return objectMapper.writeValueAsString(requestBody);
        }catch(JsonProcessingException e){
            throw new RuntimeException("Error Creating request body for Clova API: " + e.getMessage());
        }

    }

}
