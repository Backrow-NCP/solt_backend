package org.backrow.solt.service.plan;

import org.backrow.solt.dto.PlanDTO;
import org.backrow.solt.dto.page.PageRequestDTO;
import org.backrow.solt.dto.page.PageResponseDTO;

public interface PlanService {
    PlanDTO getPlan(int planId);
    PageResponseDTO<PlanDTO> getPlanList(PageRequestDTO pageRequestDTO);
    boolean deletePlan(int planId);
    boolean modifyPlan(PlanDTO planDTO);
    long savePlan(PlanDTO planDTO);
    PlanDTO aiRecommend(PlanDTO planDTO);
}
