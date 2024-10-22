package org.backrow.solt.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/clovaX")
public class ChatController {

    private static final String CLOVA_X_API_URL = "https://clovastudio.stream.ntruss.com/testapp/v1/chat-completions/HCX-003";
    private static final String CHATBOT_API_KEY = "NTA0MjU2MWZlZTcxNDJiY1vC5aF5j6hNRVO0DmcUe6IpWkBvVw/A9olJoK46uYvh"; // 여기에 Clova X API 키 입력
    private static final String CHATBOT_APIGW_KEY = "xXsMvq2mEm3I9KHY1XwdnwtwlLm0Klch5QmGgl71"; // API Gateway Key
    private static final String CHATBOT_REQUEST_ID = "e259898ccd95477c846db04dc538a848"; // 고유 요청 ID

    @PostMapping("/chat")
    public ResponseEntity<Map<String, String>> sendMessage(@RequestBody Map<String, String> request) {
        String userMessage = request.get("message");

        // Clova X API에 전송할 데이터
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
        headers.set("X-NCP-CLOVASTUDIO-API-KEY", CHATBOT_API_KEY);
        headers.set("X-NCP-APIGW-API-KEY", CHATBOT_APIGW_KEY);
        headers.set("X-NCP-CLOVASTUDIO-REQUEST-ID", CHATBOT_REQUEST_ID);

        // HTTP 요청 구성
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        RestTemplate restTemplate = new RestTemplate();

        try {
            ResponseEntity<Map> response = restTemplate.exchange(CLOVA_X_API_URL, HttpMethod.POST, entity, Map.class);
            String botReply = (String) response.getBody().get("reply");

            // 클라이언트로 챗봇 응답 전송
            Map<String, String> result = new HashMap<>();
            result.put("reply", botReply);
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            // 오류 처리
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("reply", "오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
