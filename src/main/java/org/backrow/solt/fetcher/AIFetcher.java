package org.backrow.solt.fetcher;

import lombok.RequiredArgsConstructor;
import org.backrow.solt.dto.ai.ClovaAIDTO;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AIFetcher implements Fetcher<ClovaAIDTO> {

    private final String apiKey;

    @Override
    public ClovaAIDTO fetch() {
        // Clova API에서 데이터를 가져오는 로직
        return null;
    }
}
