package org.backrow.solt.service.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.backrow.solt.domain.plan.api.ClovaApiResponse;
import org.backrow.solt.domain.plan.api.PlacesResponses;
import org.backrow.solt.dto.plan.PlaceDTO;
import org.backrow.solt.dto.plan.PlanInputDTO;
import org.backrow.solt.dto.plan.ThemeDTO;
import org.backrow.solt.service.plan.ThemeStore;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.*;
import java.util.stream.Collectors;

@Service
@Log4j2
public class ClovaApiService {

    @Value("${CLOVA_API_KEY}")
    private String clovaApiKey;

    @Value("${CLOVA_APIGW_KEY}")
    private String clovaApiGatewayKey;

    @Value("${CLOVA_REQUEST_ID}")
    private String clovaRequestId;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private ThemeStore themeStore;

    public ClovaApiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<PlacesResponses> callClovaApi(PlanInputDTO planInputDTO) {
        String location = planInputDTO.getLocation();
        String startDate = planInputDTO.getStartDate().toString();
        String endDate = planInputDTO.getEndDate().toString();
        Set<Long> themeIds = planInputDTO.getThemes() != null ? planInputDTO.getThemes() : Collections.emptySet();


        // 테마 ID를 ThemeDTO로 변환
        Set<ThemeDTO> themeSet = themeIds.stream()
                .map(themeId -> {
                    try {
                        return themeStore.getThemeById(themeId);
                    } catch (RuntimeException e) {
                        System.err.println("Error fetching theme with ID: " + themeId + ", " + e.getMessage());
                        return null; // null 반환
                    }
                })
                .filter(Objects::nonNull) // null 값 필터링
                .collect(Collectors.toSet());

        Set<PlaceDTO> places = planInputDTO.getPlaces();

        // 테마와 장소를 문자열로 변환
        String themeList = themeSet.isEmpty()
                ? "테마 없음"
                : themeSet.stream()
                .map(ThemeDTO::getName) // ThemeDTO의 name을 가져옴
                .collect(Collectors.joining(", "));

        String placeList = places.stream()
                .map(place -> String.format("[%s]", place.getPlaceName()))
                .collect(Collectors.joining(", "));

        String userContent = String.format("[%s], [%s], [%s], [%s]\\n[꼭 가야하는 장소] - %s, [숙소]",
                location, startDate, endDate, themeList, placeList);

        log.info("userContent: " + userContent);

        // Request Body
        String requestBody = createRequestBody(userContent);

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
            List<PlacesResponses> apiResponses = parseClovaApiResponse(response.getBody());

            // API 응답으로 PlaceDTO 업데이트
            updatePlacesWithApiResponse(places, apiResponses);

            // 세부정보가 없는 장소 필터링
            places.removeIf(place -> place.getStartTime() == null || place.getEndTime() == null
                    || place.getAddr() == null || place.getDescription() == null || place.getCategory() == null);

            return apiResponses;

        } catch (HttpClientErrorException e) {
            log.error("Error during Clova API call: {}", e.getMessage());
            throw new RuntimeException("Failed to call Clova API", e);
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage());
            throw new RuntimeException("Unexpected error during Clova API call", e);
        }
    }

    private void updatePlacesWithApiResponse(Set<PlaceDTO> places, List<PlacesResponses> apiResponses) {
        Map<String, PlacesResponses> filledPlacesMap = new HashMap<>();

        // 세부정보가 채워진 PlacesResponses 찾기
        for (PlacesResponses apiResponse : apiResponses) {
            String placeName = apiResponse.getPlaceName();
            if (isResponseFilled(apiResponse)) {
                filledPlacesMap.putIfAbsent(placeName, apiResponse);
            }
        }

        // PlaceDTO 업데이트 및 중복 제거
        for (PlaceDTO place : places) {
            PlacesResponses apiResponse = filledPlacesMap.get(place.getPlaceName());
            if (apiResponse != null && (place.getStartTime() == null || place.getEndTime() == null)) {
                place.setStartTime(apiResponse.getStartTime());
                place.setEndTime(apiResponse.getEndTime());
                place.setAddr(apiResponse.getAddr());
                place.setDescription(apiResponse.getDescription());
                place.setCategory(apiResponse.getCategory());
            }
        }

        // 세부정보가 없는 장소 필터링
        places.removeIf(place -> place.getStartTime() == null || place.getEndTime() == null
                || place.getAddr() == null || place.getDescription() == null || place.getCategory() == null);
    }

    private boolean isResponseFilled(PlacesResponses apiResponse) {
        // 필요한 세부정보가 모두 채워졌는지 확인하는 로직
        return apiResponse.getStartTime() != null
                && apiResponse.getEndTime() != null
                && apiResponse.getAddr() != null && !apiResponse.getAddr().isEmpty()
                && apiResponse.getDescription() != null && !apiResponse.getDescription().isEmpty()
                && apiResponse.getPrice() > 0;
    }

    private String createRequestBody(String userContent) {
        return "{\n" +
                "  \"messages\": [\n" +
                "    {\n" +
                "      \"role\": \"system\",\n" +
                "      \"content\": \"- 지역, 기간, 키워드 입력 시 필수 장소 포함 여행 일정 JSON 생성.\\r\\n" +
                "      - 마지막 날 마지막 장소는 공항 필수.\\r\\n" +
                "      - 매일 숙소는 마지막 장소, 하루 최소 3개 장소 필요, 최소 3시간 체류.\\r\\n" +
                "      - place는 날짜순 정렬, description 20자 이내 필수.\\r\\n" +
                "      - placeId는 0부터 순차 증가.\\r\\n" +
                "      - category는 숙박, 음식점, 교통비, 쇼핑, 관광지, 레포츠, 문화시설, 축제 중 하나.\\r\\n" +
                "      - category별 가격: 교통비=3000원, 음식점=15000원, 쇼핑=100000원, 숙박=100000원, 레포츠=30000원, 관광지=20000원.\\r\\n" +
                "      - `checker=false`는 최소 5곳, 나머지는 `checker=true`.\\r\\n" +
                "      - Naver Maps API로 정확한 장소명과 주소 제공, '맛집', '명소' 사용 금지.\\r\\n" +
                "      - addr은 반드시 Naver Maps API로 획득.\\r\\n" +
                "      - 중복 없는 다양한 category 포함.\\r\\n" +
                "      - JSON 형식:\\r\\n\\n" +
                "      {\\r\\n" +
                "        \\\"places\\\": [\\r\\n" +
                "          {\\r\\n" +
                "            \\\"placeId\\\": 0,\\r\\n" +
                "            \\\"placeName\\\": \\\"string\\\",\\r\\n" +
                "            \\\"addr\\\": \\\"string\\\",\\r\\n" +
                "            \\\"price\\\": 0,\\r\\n" +
                "            \\\"startTime\\\": \\\"2024-09-15T10:20:00\\\",\\r\\n" +
                "            \\\"endTime\\\": \\\"2024-09-15T13:20:00\\\",\\r\\n" +
                "            \\\"description\\\": \\\"string\\\",\\r\\n" +
                "            \\\"category\\\": \\\"string\\\",\\r\\n" +
                "            \\\"checker\\\": true\\r\\n" +
                "          }\\r\\n" +
                "        ],\\r\\n" +
                "        \\\"themes\\\": [1, 2, 3],\\r\\n" +
                "        \\\"location\\\": \\\"string\\\",\\r\\n" +
                "        \\\"startDate\\\": \\\"2024-10-15\\\",\\r\\n" +
                "        \\\"endDate\\\": \\\"2024-10-15\\\"\\r\\n" +
                "      }\\n\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"role\": \"user\",\n" +
                "      \"content\": \"" + userContent + "\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"topP\": 0.8,\n" +
                "  \"topK\": 0,\n" +
                "  \"maxTokens\": 3550,\n" +
                "  \"temperature\": 0.85,\n" +
                "  \"repeatPenalty\": 1.9,\n" +
                "  \"stopBefore\": [],\n" +
                "  \"includeAiFilters\": true,\n" +
                "  \"seed\": 3000\n" +
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
                placeResponse.setCategory(placeNode.path("category").asText()); // 수정: category 설정
                placeResponse.setChecker(placeNode.path("checker").asBoolean());

                placeResponses.add(placeResponse);
            }
        } catch (Exception e) {
            log.error("Error parsing places: {}", e.getMessage());
        }

        return placeResponses;
    }
}
