package org.backrow.solt.service.ai;

import lombok.RequiredArgsConstructor;
import org.backrow.solt.dto.ai.ClovaDTO;
import org.backrow.solt.dto.plan.PlanDTO;
import org.backrow.solt.fetcher.FetchManager;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AIPlanMakerImpl implements AIPlanMaker {

    private final FetchManager fetchManager;

    @Override
    public PlanDTO aiRecommend(PlanDTO planDTO) {
        // AI 추천 알고리즘 로직
        return null;
    }

    @Override
    public List<ClovaDTO> runFetching() {
        fetchManager.runFetching();
        // ClovaDTO 객체를 가져오는 로직 
        return new ArrayList<ClovaDTO>();
    }
}

