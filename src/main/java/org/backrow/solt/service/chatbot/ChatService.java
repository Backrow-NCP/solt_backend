package org.backrow.solt.service.chatbot;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.backrow.solt.domain.plan.api.ChatResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;

@Log4j2
@Service
public class ChatService {

    @Value("${CHATBOT_API_KEY}")
    private String chatbotApiKey;

    @Value("${CHATBOT_APIGW_KEY}")
    private String chatbotApiGwKey;

    @Value("${CHATBOT_REQUEST_ID}")
    private String chatbotRequestId;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public ChatService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    // Chatbot API 요청을 처리하는 메서드
    public String sendMessageToClovaApi(String userMessage) {

        // Request Body
        String requestBody = createRequestBody(userMessage);

        // Headers
        HttpHeaders headers = createHeaders();

        // HttpEntity에 requestBody와 headers 포함
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    "https://clovastudio.stream.ntruss.com/testapp/v1/chat-completions/HCX-003",
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            log.info("Response from Clova API: {}", response.getBody());
            return parseClovaApiResponse(response.getBody());

        } catch (HttpClientErrorException e) {
            log.error("Error during Clova API call: {}", e.getMessage());
            throw new RuntimeException("Failed to call Clova API", e);
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage());
            throw new RuntimeException("Unexpected error during Clova API call", e);
        }
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-NCP-CLOVASTUDIO-API-KEY", chatbotApiKey);
        headers.set("X-NCP-APIGW-API-KEY", chatbotApiGwKey);
        headers.set("X-NCP-CLOVASTUDIO-REQUEST-ID", chatbotRequestId);
        headers.set("Content-Type", "application/json");
        return headers;
    }

    private String createRequestBody(String userMessage) {
        return "{\n" +
                "  \"messages\": [\n" +
                "    {\n" +
                "      \"role\": \"system\",\n" +
                "      \"content\": \"당신은 여행 가이드입니다. 당신의 역할은 여행 일정과 추천 장소를 사용자에게 제공하는 것입니다.\\n" +
                "      - 지역, 기간, 키워드에 맞춰 여행 일정을 json 형식으로 작성하세요.\\n" +
                "      - 하루에 최소 3곳 이상의 장소를 포함하세요.\\n" +
                "      - 사용자가 꼭 방문하고자 하는 장소는 일정에 포함되도록 하세요.\\n" +
                "      - 숙소는 각 일자별 마지막 일정에 반드시 포함되어야 합니다.\\n" +
                "      - 여행의 마지막 날에는 공항을 포함해야 합니다.\\n" +
                "      - 일정은 여유롭고 편안한 여행을 즐길 수 있도록 구성하세요.\\n" +
                "      - json 형식은 다음과 같이 맞추세요:\\n" +
                "      {\\n  \\\"places\\\": [\\n    {\\n      \\\"placeId\\\": 0,\\n      \\\"placeName\\\": \\\"string\\\",\\n      \\\"addr\\\": \\\"string\\\",\\n      \\\"price\\\": 0,\\n      \\\"startTime\\\": \\\"2024-09-15T13:20:00\\\",\\n      \\\"endTime\\\": \\\"2024-09-15T13:20:00\\\",\\n      \\\"description\\\": \\\"string\\\",\\n      \\\"checker\\\": true\\n    }\\n  ],\\n  \\\"themes\\\": [\\n    0\\n  ],\\n  \\\"location\\\": \\\"string\\\",\\n  \\\"startDate\\\": \\\"2024-10-15\\\",\\n  \\\"endDate\\\": \\\"2024-10-15\\\"\\n}\\n\\n" +
                "      - 장소는 날짜 순서대로 배치하세요.\\n" +
                "      - 각 장소에 대한 간단한 설명(20자 이내)을 추가하세요.\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"role\": \"user\",\n" +
                "      \"content\": \"" + userMessage + "\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"topP\": 0.8,\n" +
                "  \"topK\": 0,\n" +
                "  \"maxTokens\": 174,\n" +
                "  \"temperature\": 0.5,\n" +
                "  \"repeatPenalty\": 7.0,\n" +
                "  \"stopBefore\": [],\n" +
                "  \"includeAiFilters\": true,\n" +
                "  \"seed\": 0\n" +
                "}";
    }

    // 응답 파싱 메서드
    private String parseClovaApiResponse(String responseBody) {
        try {
            // JSON 문자열을 ClovaApiResponse 객체로 변환
            ChatResponse chatResponse = objectMapper.readValue(responseBody, ChatResponse.class);
            return chatResponse.getReply(); // 응답에서 reply만 반환
        } catch (Exception e) {
            log.error("Error parsing Clova API response: {}", e.getMessage());
            throw new RuntimeException("Failed to parse Clova API response", e);
        }
    }

}