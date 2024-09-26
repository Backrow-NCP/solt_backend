package org.backrow.solt.service.ai;

import org.backrow.solt.dto.ai.ClovaDTO;
import org.backrow.solt.dto.plan.PlanDTO;

import java.util.List;

public interface AIPlanMaker {
    PlanDTO aiRecommend(PlanDTO planDTO);
    List<ClovaDTO> runFetching();
}
