package org.backrow.solt.fetcher;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MapFetcher implements Fetcher {

    private final String apiKey;

    // Google Map API 키를 주입
    public MapFetcher(@Value("${google.map.api.key}") String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public List<GoogleMapDTO> fetch() {
        // Google Maps API에서 데이터를 가져오는 로직
        return new ArrayList<>();
    }
}
