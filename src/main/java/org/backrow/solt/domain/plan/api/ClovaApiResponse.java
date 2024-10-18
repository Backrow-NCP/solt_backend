package org.backrow.solt.domain.plan.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true) // 알 수 없는 필드 무시
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
        private String stopReason; // 추가: stopReason
        private Integer seed; // 추가: seed
        private List<AiFilter> aiFilter; // 추가: aiFilter 목록

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
