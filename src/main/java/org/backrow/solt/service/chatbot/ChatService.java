package org.backrow.solt.service.chatbot;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;

import java.net.http.HttpHeaders;
import java.util.HashMap;
import java.util.Map;

@Service
public class ChatService {

    private static final String CLOVA_X_API_URL = "https://clovastudio.stream.ntruss.com/testapp/v1/chat-completions/HCX-003";

    @Value("${CHATBOT_API_KEY}")
    private String chatbotApiKey;

    @Value("${CHATBOT_APIGW_KEY}")
    private String chatbotApiGwKey;

    @Value("${CHATBOT_REQUEST_ID}")
    private String chatbotRequestId;

    private final RestTemplate restTemplate;

    public ChatService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // Chatbot API 요청을 처리하는 메서드
    public String sendMessageToClovaApi(String userMessage) {
        // Chatbot API에 전송할 데이터 생성
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

        // HTTP 요청 헤더 구성
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-NCP-CLOVASTUDIO-API-KEY", chatbotApiKey);
        headers.set("X-NCP-APIGW-API-KEY", chatbotApiGwKey);
        headers.set("X-NCP-CLOVASTUDIO-REQUEST-ID", chatbotRequestId);

        // HTTP 요청 생성
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            // Clova API로 요청 전송
            ResponseEntity<Map> response = restTemplate.exchange(CLOVA_X_API_URL, HttpMethod.POST, entity, Map.class);
            return (String) response.getBody().get("reply");
        } catch (Exception e) {
            // 오류 처리
            throw new RuntimeException("Clova API 호출 중 오류가 발생했습니다.", e);
        }
    }
}