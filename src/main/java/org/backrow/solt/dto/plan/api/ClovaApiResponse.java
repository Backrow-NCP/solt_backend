package org.backrow.solt.dto.plan.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ClovaApiResponse {
    private Status status;
    private Result result;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @ToString
    public static class Status {
        private String code;
        private String message;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Result {
        private Message message;
        private Integer inputLength;
        private Integer outputLength;
        private String stopReason;
        private Integer seed;
        private List<AiFilter> aiFilter;

        // places 필드를 Result 내부로 이동
        @JsonProperty("places")
        private List<PlacesResponses> places;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        @ToString
        public static class Message {
            private String role;
            private String content;
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @ToString
    public static class AiFilter {
        private String groupName;
        private String name;
        private String score;
        private String result;
    }
}
