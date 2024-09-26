package org.backrow.solt.fetcher;

import org.backrow.solt.dto.ai.ClovaDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AIFetcher implements Fetcher<ClovaDTO> {

    private final String apiKey;

    public AIFetcher(@Value("${clova.api.key}") String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public List<ClovaAIDTO> fetch() {
        // Clova API에서 데이터를 가져오는 로직
        return new ArrayList<>();
    }
}
