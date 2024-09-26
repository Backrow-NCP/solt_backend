package org.backrow.solt.fetcher;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FetchManager {

    private final List<Fetcher<?>> fetchers;

    public List<?> fetch(){
        return null;
    }

    // 각 Fetcher의 fetch 메서드를 실행
    public void runFetching() {
        fetchers.forEach(Fetcher::fetch);
    }

}
