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
    private String role;
    private Status status;
    private Result result;
    private List<PlacesResponses> places;

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
    public static class Result {
        private Message message;

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
}
