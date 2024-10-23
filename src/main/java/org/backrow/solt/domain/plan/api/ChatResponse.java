package org.backrow.solt.domain.plan.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponse {
    private String content; // assistant가 응답하는 내용 (첫 번째 메시지의 내용)
    private String message; // 추가 메시지 또는 오류 메시지
    private Status status; // API 응답 상태
    private List<Message> results; // API 응답 결과 목록

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Status {
        private String code; // 상태 코드
        private String message; // 상태 메시지
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Message {
        private String role; // 메시지의 역할 (assistant, user 등)
        private String content; // assistant의 응답 내용
    }
}
