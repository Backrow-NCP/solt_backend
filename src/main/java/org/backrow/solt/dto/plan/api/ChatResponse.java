package org.backrow.solt.dto.plan.api;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChatResponse {
    private String content;
}
