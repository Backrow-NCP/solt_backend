package org.backrow.solt.service.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.backrow.solt.domain.plan.api.ClovaApiResponse;
import org.backrow.solt.domain.plan.api.PlacesResponses;
import org.backrow.solt.dto.plan.PlaceDTO;
import org.backrow.solt.dto.plan.PlanInputDTO;
import org.backrow.solt.dto.plan.ThemeDTO;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Log4j2
public class ClovaApiService {

    // 외부에서 주입되는 API 키 값
    @Value("${CLOVA_API_KEY}")
    private String clovaApiKey;

    @Value("${CLOVA_APIGW_KEY}")
    private String clovaApiGatewayKey;

    @Value("${CLOVA_API_URL}")
    private String clovaApiUrl;

    @Value("${CLOVA_REQUEST_ID}")
    private String clovaRequestId;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // 생성자 주입 방식 사용
    public ClovaApiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // Clova API 요청을 보내는 메소드
    public List<PlacesResponses> callClovaApi(PlanInputDTO planInputDTO) {
        // 필요한 PlanInputDTO 필드 값 추출
        String location = planInputDTO.getLocation();
        String startDate = planInputDTO.getStartDate().toString();
        String endDate = planInputDTO.getEndDate().toString();

        // 테마 ID를 ThemeDTO로 변환
        Set<ThemeDTO> themeSet = planInputDTO.getThemes().stream()
                .map(themeId -> ThemeDTO.builder()
                        .themeId(themeId)
                        .build())
                .collect(Collectors.toSet());

        Set<PlaceDTO> places = planInputDTO.getPlaces();

        // 테마와 장소를 문자열로 변환
        String themeList = themeSet.stream()
                .map(ThemeDTO::getThemeId)
                .map(String::valueOf)
                .collect(Collectors.joining(", "));

        String placeList = places.stream()
                .map(PlaceDTO::getPlaceName)
                .collect(Collectors.joining(", "));

        // 사용자 입력 형식의 문자열 생성
        String userContent = String.format("[%s], [%s], [%s], [%s]\\n[꼭 가야하는 장소] - [%s], [숙소]",
                location, startDate, endDate, themeList, placeList);

        // Request Body
        String requestBody = createRequestBody(userContent);

        // Headers
        HttpHeaders headers = createHeaders();

        // HttpEntity에 requestBody와 headers 포함
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    clovaApiUrl,
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

    private String createRequestBody(String userContent) {
        return "{\n" +
                "  \"messages\": [\n" +
                "    {\n" +
                "      \"role\": \"system\",\n" +
                "      \"content\": \"- 지역, 기간, 키워드를 입력하면 여행 일정을 json으로 생성합니다.\\n" +
                "      - 하루에 최소 3 장소 이상이 포함되어 있어야합니다.\\n" +
                "      - 꼭 가야하는 장소는 포함될 수도 있습니다.\\n" +
                "      - 숙소는 반드시 일정에 포함하여야 하고 각 일자별의 마지막에 반드시 포함되어야한다.\\n" +
                "      - 마지막 일정에는 근처 공항이 반드시 포함되어 있어야합니다.\\n\\n" +
                "      - json의 형식은 다음과 같습니다\\n" +
                "      {\\n  \\\"places\\\": [\\r\\n    {\\r\\n      \\\"placeId\\\": 0,\\r\\n      \\\"placeName\\\": \\\"string\\\",\\r\\n      \\\"addr\\\": \\\"string\\\",\\r\\n      \\\"price\\\": 0,\\r\\n      \\\"startTime\\\": \\\"2024-09-15T13:20:00\\\",\\r\\n      \\\"endTime\\\": \\\"2024-09-15T13:20:00\\\",\\r\\n      \\\"description\\\": \\\"string\\\",\\r\\n      \\\"checker\\\": true\\r\\n    }\\r\\n  ],\\r\\n  \\\"themes\\\": [\\r\\n    0\\r\\n  ],\\r\\n  \\\"location\\\": \\\"string\\\",\\r\\n  \\\"startDate\\\": \\\"2024-10-15\\\",\\r\\n  \\\"endDate\\\": \\\"2024-10-15\\\"\\r\\n}\\n\\n" +
                "      - places의 date는 날짜 순으로 배열되어야합니다.\\n" +
                "      - routes의 startPlaceId와 endPlaceId는 같은 날짜에 있는 장소여야합니다.\\n" +
                "      - description은 20자 이내로 제공되어야 합니다.\\n" +
                "      - 모든 Id는 순차적으로 0부터 증가해야합니다.\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"role\": \"user\",\n" +
                "      \"content\": \"" + "[서울], [2024-10-01],[2024-10-03], [여유롭게 즐겨요, 맛집에 관심있어요, 가성비가 중요해요] \\n [꼭 가야하는 장소] - [세종문화회관], [서울월드컵경기장], [반포한강공원] " + "\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"topP\": 0.8,\n" +
                "  \"topK\": 0,\n" +
                "  \"maxTokens\": 3523,\n" +
                "  \"temperature\": 0.7,\n" +
                "  \"repeatPenalty\": 3.5,\n" +
                "  \"stopBefore\": [],\n" +
                "  \"includeAiFilters\": true,\n" +
                "  \"seed\": 2048\n" +
                "}";
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-NCP-CLOVASTUDIO-API-KEY", clovaApiKey);
        headers.set("X-NCP-APIGW-API-KEY", clovaApiGatewayKey);
        headers.set("X-NCP-CLOVASTUDIO-REQUEST-ID", clovaRequestId);
        headers.set("Content-Type", "application/json");
        return headers;
    }

    // 응답 문자열을 ClovaApiResponse 객체로 변환하는 메소드
    private List<PlacesResponses> parseClovaApiResponse(String responseBody) {
        try {
            ClovaApiResponse clovaApiResponse = objectMapper.readValue(responseBody, ClovaApiResponse.class);
            String content = clovaApiResponse.getResult().getMessage().getContent();
            return parsePlaces(content);
        } catch (Exception e) {
            log.error("Failed to parse response body: {}", e.getMessage());
            throw new RuntimeException("Failed to parse ClovaApiResponse", e);
        }
    }

    // content에서 places를 추출하는 메소드
    private List<PlacesResponses> parsePlaces(String content) {
        List<PlacesResponses> placeResponses = new ArrayList<>();

        try {
            JsonNode rootNode = objectMapper.readTree(content);
            JsonNode placesNode = rootNode.path("places");

            for (JsonNode placeNode : placesNode) {
                PlacesResponses placeResponse = new PlacesResponses();
                placeResponse.setPlaceId(placeNode.path("placeId").asLong());
                placeResponse.setPlaceName(placeNode.path("placeName").asText());
                placeResponse.setAddr(placeNode.path("addr").asText());
                placeResponse.setPrice(placeNode.path("price").asInt());

                // 공백 제거 후 LocalDateTime 파싱
                String startTimeString = placeNode.path("startTime").asText().trim();
                String endTimeString = placeNode.path("endTime").asText().trim();

                try {
                    placeResponse.setStartTime(LocalDateTime.parse(startTimeString));
                    placeResponse.setEndTime(LocalDateTime.parse(endTimeString));
                } catch (DateTimeParseException e) {
                    log.error("Error parsing LocalDateTime for startTime: {} or endTime: {}", startTimeString, endTimeString);
                    throw new RuntimeException("Failed to parse LocalDateTime", e);
                }

                placeResponse.setDescription(placeNode.path("description").asText());
                placeResponse.setChecker(placeNode.path("checker").asBoolean());

                placeResponses.add(placeResponse);
            }


        } catch (Exception e) {
            log.error("Error parsing places: {}", e.getMessage());
        }

        return placeResponses;
    }
}
