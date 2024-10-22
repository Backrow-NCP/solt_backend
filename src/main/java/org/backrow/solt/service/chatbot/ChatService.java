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

        return sendChatRequest(requestBody);
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
        headers.set("X-NCP-CLOVASTUDIO-API-KEY", chatbotApiKey);
        headers.set("X-NCP-APIGW-API-KEY", chatbotApiGwKey);
        headers.set("X-NCP-CLOVASTUDIO-REQUEST-ID", chatbotRequestId);
        headers.set("Content-Type", "application/json");
        headers.set("Accept", "text/event-stream");
        log.debug("Headers created for Clova API call: {}", headers);
        return headers;
    }

    private String createRequestBody(String userMessage) {
        String requestBody = "{\n" +
                "  \"messages\": [\n" +
                "    {\n" +
                "      \"role\": \"user\",\n" +
                "      \"content\": \"" + userMessage + "\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";
        log.debug("Request body created: {}", requestBody);
        return requestBody;
    }


    private ChatResponse parseChatResponse(String responseBody) {
        try {
            log.debug("Parsing response body from Clova API.");
            ChatResponse apiResponse = objectMapper.readValue(responseBody, ChatResponse.class);

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
}
