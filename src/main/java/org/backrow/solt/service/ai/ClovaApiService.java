package org.backrow.solt.service.ai;

import org.backrow.solt.domain.plan.api.RoutesResponses;
import org.backrow.solt.dto.plan.PlanInputDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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
        StringBuilder requestBody = new StringBuilder();
        requestBody.append("{");
        requestBody.append("\"location\":\"").append(planInputDTO.getLocation()).append("\",");
        requestBody.append("\"startDate\":\"").append(planInputDTO.getStartDate()).append("\",");
        requestBody.append("\"endDate\":\"").append(planInputDTO.getEndDate()).append("\",");
        requestBody.append("\"themes\":").append(planInputDTO.getThemes()).append(",");
        requestBody.append("}");

        return requestBody.toString();
    }

}
