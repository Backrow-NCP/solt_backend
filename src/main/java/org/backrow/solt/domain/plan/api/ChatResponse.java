package org.backrow.solt.domain.plan.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponse {
    private String content; // assistant가 응답하는 내용
    private String message; // 추가 메시지 또는 오류 메시지
}
