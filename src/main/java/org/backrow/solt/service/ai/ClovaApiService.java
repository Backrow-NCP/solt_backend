package org.backrow.solt.service.ai;

import lombok.extern.log4j.Log4j2;
import org.backrow.solt.domain.plan.api.DirectionsResponses;
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

import java.util.Set;
import java.util.stream.Collectors;

@Service
@Log4j2
public class ClovaApiService {

    /*
    // 외부에서 주입되는 API 키 값
    @Value("${clova.api.key}")
    private String clovaApiKey;

    @Value("${clova.apigw.api.key}")
    private String clovaApiGatewayKey;

    @Value("${clova.api.url}")
    private String clovaApiUrl;

    @Value("${clova.request.id}")
    private String clovaRequestId;
     */

    private final RestTemplate restTemplate;

    // 생성자 주입 방식 사용
    public ClovaApiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // Clova API 요청을 보내는 메소드
    public DirectionsResponses callClovaApi(PlanInputDTO planInputDTO) {

        // 필요한 PlanInputDTO 필드 값 추출
        String location = planInputDTO.getLocation(); // 지역
        String startDate = planInputDTO.getStartDate().toString(); // 시작 날짜
        String endDate = planInputDTO.getEndDate().toString(); // 종료 날짜

        // 테마 ID를 ThemeDTO로 변환
        Set<ThemeDTO> themeSet = planInputDTO.getThemes().stream()
                .map(themeId -> ThemeDTO.builder()
                        .themeId(themeId)  // Long 값을 ThemeDTO로 변환
                        .build())
                .collect(Collectors.toSet());

        Set<PlaceDTO> places = planInputDTO.getPlaces(); // 꼭 가야하는 장소

        // 테마와 장소를 문자열로 변환
        String themeList = themeSet.stream()
                .map(ThemeDTO::getThemeId) // 여기서 ID 사용, 나중에 이름 추가 필요
                .map(String::valueOf) // Long -> String 변환
                .collect(Collectors.joining(", "));

        String placeList = places.stream()
                .map(PlaceDTO::getPlaceName)
                .collect(Collectors.joining(", "));

        // 사용자 입력 형식의 문자열 생성
        String userContent = String.format("[%s], [%s], [%s], [%s]\\n[꼭 가야하는 장소] - [%s]",
                location, startDate, endDate, themeList, placeList);

        // Request Body
        String requestBody =
                "{\n" +
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
                        "      \"content\": \"" + userContent + "\"\n" +
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

        // Headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-NCP-CLOVASTUDIO-API-KEY", "NTA0MjU2MWZlZTcxNDJiYwBYJNO67mQdsAlDTunBopY34AAw2fGnua3t3a4kFgPm");
        headers.set("X-NCP-APIGW-API-KEY", "j96OgrLz3aBC78T9HgjgsQvcg5EhYxgWOT0Ot4RK");
        headers.set("X-NCP-CLOVASTUDIO-REQUEST-ID", "5589e9f2-ef1a-42b8-8b2b-39c6968e83ea");
        headers.set("Content-Type", "application/json");

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

            // 응답을 DTO로 매핑하여 반환
            // (String을 DirectionsResponses로 변환하는 로직 필요)
            return convertToDirectionsResponses(response.getBody());

        } catch (HttpClientErrorException e) {
            log.error("Error during Clova API call: {}", e.getMessage());
            throw new RuntimeException("Failed to call Clova API", e);
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage());
            throw new RuntimeException("Unexpected error during Clova API call", e);
        }
    }

    // 응답 문자열을 DirectionsResponses 객체로 변환하는 메소드 (구현 필요)
    private DirectionsResponses convertToDirectionsResponses(String responseBody) {
        // JSON 파싱 로직을 추가하여 문자열을 DTO로 변환
        return new DirectionsResponses(); // 임시로 빈 객체 반환
    }

}
