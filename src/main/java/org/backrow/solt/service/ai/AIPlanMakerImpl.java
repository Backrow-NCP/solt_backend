package org.backrow.solt.service.ai;

import org.backrow.solt.dto.ClovaDTO;
import org.backrow.solt.dto.plan.PlanDTO;
import org.backrow.solt.fetcher.FetchManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AIPlanMakerImpl implements AIPlanMaker {

    private final FetchManager fetchManager;

    @Autowired
    public AIPlanMakerImpl(FetchManager fetchManager) {
        this.fetchManager = fetchManager;
    }

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

