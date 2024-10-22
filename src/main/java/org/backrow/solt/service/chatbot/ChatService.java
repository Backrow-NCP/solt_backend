package org.backrow.solt.service.chatbot;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.backrow.solt.domain.plan.api.ChatResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.Map;

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

    private static final String API_URL = "https://clovastudio.stream.ntruss.com/testapp/v1/chat-completions/HCX-003";

    public ChatService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public ChatResponse sendMessageToClovaApi(String userMessage) {
        if (isInvalidMessage(userMessage)) {
            return createChatResponse(null, "메시지를 입력해야 합니다.");
        }

        log.info("User message: {}", userMessage);
        String requestBody = createRequestBody(userMessage);
        return sendChatRequest(requestBody);
    }

    private boolean isInvalidMessage(String message) {
        return message == null || message.trim().isEmpty();
    }

    private ChatResponse sendChatRequest(String requestBody) {
        HttpHeaders headers = createHeaders();
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    API_URL,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            log.info("Response from Clova API: {}", response.getBody());
            return parseChatResponse(response.getBody());

        } catch (HttpClientErrorException e) {
            log.error("Error during Clova API call: {}", e.getResponseBodyAsString());
            return createChatResponse(null, "클로바 API 호출에 실패했습니다: " + e.getStatusCode());
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage());
            return createChatResponse(null, "클로바 API 호출 중 예기치 않은 오류가 발생했습니다: " + e.getMessage());
        }
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-NCP-CLOVASTUDIO-API-KEY", chatbotApiKey);
        headers.set("X-NCP-APIGW-API-KEY", chatbotApiGwKey);
        headers.set("X-NCP-CLOVASTUDIO-REQUEST-ID", chatbotRequestId);
        headers.set("Content-Type", "application/json");
        headers.set("Accept", "text/event-stream");
        return headers;
    }

    private String createRequestBody(String userMessage) {
        return "{\n" +
                "  \"messages\": [\n" +
                "    {\n" +
                "      \"role\": \"system\",\n" +
                "      \"content\": \"" +
                "- 여행지 소개: 해당 여행지의 역사, 문화, 관광 명소, 맛집 등을 소개해 주세요.\\r\\n" +
                "- 여행 팁 제공: 여행 중 유용한 팁이나 주의사항 등을 알려주세요.\\r\\n" +
                "- 여행자 문의 응대: 여행자의 질문이나 요청에 친절하게 응대하고, 필요한 정보를 제공해 주세요.\\r\\n" +
                "- 모든 응답의 글자 수를 100자 이내로 대답해줘\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"role\": \"user\",\n" +
                "      \"content\": \"" + userMessage + "\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"topP\": 0.8,\n" +
                "  \"topK\": 0,\n" +
                "  \"maxTokens\": 250,\n" +
                "  \"temperature\": 0.5,\n" +
                "  \"repeatPenalty\": 7.0,\n" +
                "  \"stopBefore\": [],\n" +
                "  \"includeAiFilters\": true,\n" +
                "  \"seed\": 0\n" +
                "}";
    }

    private ChatResponse parseChatResponse(String responseBody) {
        try {
            // JSON 문자열을 맵으로 변환
            Map<String, Object> responseMap = objectMapper.readValue(responseBody, Map.class);
            Map<String, Object> result = (Map<String, Object>) responseMap.get("result");

            if (result == null) {
                return createChatResponse(null, "결과가 없습니다.");
            }

            // result에서 message 객체를 가져오고 그 안에서 content를 추출
            Map<String, Object> message = (Map<String, Object>) result.get("message");
            if (message == null) {
                return createChatResponse(null, "메시지가 없습니다.");
            }

            // content 값을 추출하여 ChatResponse 객체 생성
            return createChatResponse((String) message.get("content"), null);

        } catch (Exception e) {
            log.error("Error parsing Clova API response: {}", e.getMessage());
            return createChatResponse(null, "Failed to parse Clova API response: " + e.getMessage());
        }
    }


    private ChatResponse createChatResponse(String content, String errorMessage) {
        return new ChatResponse(content, errorMessage);
    }
}
