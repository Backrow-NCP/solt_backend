package org.backrow.solt.domain.plan.api;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ClovaApiResponse {
    private Result result;
    private List<PlacesResponses> places; // PlacesResponses 리스트

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
            private String content;
        }
    }
}
