package org.backrow.solt.service.chatbot;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringEscapeUtils;
import org.backrow.solt.domain.plan.api.ChatResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

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
            log.warn("Invalid user message: message is null or empty.");
            return new ChatResponse(null, "메시지를 입력해야 합니다.", null, null);
        }

        log.info("User message: {}", userMessage);
        String requestBody = createRequestBody(userMessage);
        log.debug("Request body created: {}", requestBody);

        ChatResponse response = sendChatRequest(requestBody);

        // 여기서 assistant의 응답 내용을 활용
        String assistantContent = response.getResult() != null && response.getResult().getMessage() != null
                ? response.getResult().getMessage().getContent()
                : null;

        if (assistantContent != null) {
            log.info("Assistant's response: {}", assistantContent);
            // 추가적으로 assistantContent를 활용하는 로직을 작성할 수 있습니다.
        } else {
            log.warn("No content received from assistant.");
        }

        return response;
    }

    private boolean isInvalidMessage(String message) {
        return message == null || message.trim().isEmpty();
    }

    private ChatResponse sendChatRequest(String requestBody) {
        HttpHeaders headers = createHeaders();
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        log.info("Sending request to Clova API with URL: {}", API_URL);
        log.debug("Request headers: {}", headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    API_URL,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            log.info("Response from Clova API received.");
            log.debug("Clova API response body: {}", response.getBody());

            return parseChatResponse(response.getBody());

        } catch (HttpClientErrorException e) {
            log.error("Error during Clova API call: status code = {}, response body = {}",
                    e.getStatusCode(), e.getResponseBodyAsString());
            return new ChatResponse(null, "클로바 API 호출에 실패했습니다: " + e.getStatusCode(), null, null);
        } catch (Exception e) {
            log.error("Unexpected error during Clova API call: {}", e.getMessage());
            return new ChatResponse(null, "클로바 API 호출 중 예기치 않은 오류가 발생했습니다: " + e.getMessage(), null, null);
        }
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-NCP-CLOVASTUDIO-API-KEY", chatbotApiKey); // 환경변수에서 가져오기
        headers.set("X-NCP-APIGW-API-KEY", chatbotApiGwKey); // 환경변수에서 가져오기
        headers.set("X-NCP-CLOVASTUDIO-REQUEST-ID", chatbotRequestId); // 환경변수에서 가져오기
        headers.set("Content-Type", "application/json");
        headers.set("Accept", "text/event-stream");
        log.debug("Headers created for Clova API call: {}", headers);
        return headers;
    }

    private String createRequestBody(String userMessage) {
        String escapedUserMessage = StringEscapeUtils.escapeJson(userMessage); // JSON 문자열 이스케이프
        String requestBody = "{\n" +
                "  \"messages\": [\n" +
                "    {\n" +
                "      \"role\": \"system\",\n" +
                "      \"content\": \"- 여행지 소개: 해당 여행지의 역사, 문화, 관광 명소, 맛집 등을 소개해 주세요.\\r\\n- 여행 팁 제공: 여행 중 유용한 팁이나 주의사항 등을 알려주세요.\\r\\n- 여행자 문의 응대: 여행자의 질문이나 요청에 친절하게 응대하고, 필요한 정보를 제공해 주세요.\\n- 모든 응답의 글자 수를 100자 이내로 대답해줘\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"role\": \"user\",\n" +
                "      \"content\": \"" + escapedUserMessage + "\"\n" +
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
        log.debug("Request body created: {}", requestBody);
        return requestBody;
    }

    private ChatResponse parseChatResponse(String responseBody) {
        try {
            log.debug("Parsing response body from Clova API.");
            String jsonData = extractJsonFromStream(responseBody);
            ChatResponse apiResponse = objectMapper.readValue(jsonData, ChatResponse.class);

            if (apiResponse.getStatus() == null || apiResponse.getResult() == null || apiResponse.getResult().getMessage() == null) {
                log.warn("Invalid response from Clova API: missing status or result.");
                return new ChatResponse(null, "결과가 없습니다.", null, null);
            }

            return new ChatResponse(apiResponse.getResult().getMessage().getContent(), null, apiResponse.getStatus(), apiResponse.getResult());

        } catch (Exception e) {
            log.error("Error parsing Clova API response: {}", e.getMessage());
            return new ChatResponse(null, "Failed to parse Clova API response: " + e.getMessage(), null, null);
        }
    }

    private String extractJsonFromStream(String responseBody) {
        // 스트리밍 형식의 응답에서 JSON 데이터 추출
        StringBuilder jsonBuilder = new StringBuilder();
        String[] lines = responseBody.split("\n");
        for (String line : lines) {
            if (line.startsWith("data:")) {
                String jsonLine = line.substring(5).trim();
                if (!jsonLine.isEmpty()) {
                    jsonBuilder.append(jsonLine).append("\n");
                }
            }
        }
        return jsonBuilder.toString();
    }

}
