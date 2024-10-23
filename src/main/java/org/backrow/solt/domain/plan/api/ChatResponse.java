package org.backrow.solt.domain.plan.api;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChatResponse {
    private String content;
}
