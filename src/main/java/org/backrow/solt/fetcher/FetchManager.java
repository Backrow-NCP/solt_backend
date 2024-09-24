package org.backrow.solt.fetcher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FetchManager {

    private final List<Fetcher<?>> fetchers;

    // 모든 Fetcher들을 주입받음
    @Autowired
    public FetchManager(List<Fetcher<?>> fetchers) {
        this.fetchers = fetchers;
    }

    // 각 Fetcher의 fetch 메서드를 실행
    public void runFetching() {
        fetchers.forEach(Fetcher::fetch);
    }

}
