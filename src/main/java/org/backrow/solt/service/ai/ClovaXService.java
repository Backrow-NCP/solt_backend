package org.backrow.solt.service.ai;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import java.util.*;

@Service
public class ClovaXService {

    @Value("${clova.x.api-key}")
    private String clovaXApiKey;

    @Value("${clova.x.gateway-api-key}")
    private String clovaXGatewayKey;

    @Value("${clova.x.request-id}")
    private String clovaXRequestId;

    @Value("${clova.x.api-url}")
    private String clovaXApiUrl;

    public String getChatbotReply(String userMessage) {
        RestTemplate restTemplate = new RestTemplate();

        // 요청 데이터 구성
        Map<String, Object> requestBody = new HashMap<>();
        Map<String, String> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", userMessage);

        requestBody.put("messages", new Map[]{message});
        requestBody.put("topP", 0.8);
        requestBody.put("topK", 0);
        requestBody.put("maxTokens", 174);
        requestBody.put("temperature", 0.5);
        requestBody.put("repeatPenalty", 7.0);
        requestBody.put("includeAiFilters", true);
        requestBody.put("seed", 0);

        // HTTP 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-NCP-CLOVASTUDIO-API-KEY", clovaXApiKey);
        headers.set("X-NCP-APIGW-API-KEY", clovaXGatewayKey);
        headers.set("X-NCP-CLOVASTUDIO-REQUEST-ID", clovaXRequestId);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            // Clova X API에 요청 전송
            ResponseEntity<Map> response = restTemplate.exchange(clovaXApiUrl, HttpMethod.POST, entity, Map.class);
            String botReply = (String) response.getBody().get("reply");
            return botReply;
        } catch (Exception e) {
            // 오류 발생 시
            return "오류가 발생했습니다.";
        }
    }
}
