package org.backrow.solt.service.ai;

import org.backrow.solt.dto.ClovaDTO;
import org.backrow.solt.dto.PlanDTO;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class AIPlanMakerImpl implements AIPlanMaker {

    @Override
    public PlanDTO aiRecommend(PlanDTO planDTO) {
        // AI 추천 알고리즘 로직
        return null; // 실제 로직으로 변경 필요
    }

    @Override
    public List<ClovaDTO> runFetching() {
        // 외부 API와 통신하는 로직
        return null; // 실제 로직으로 변경 필요
    }
}

