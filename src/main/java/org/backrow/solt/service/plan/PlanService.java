package org.backrow.solt.service.plan;

import org.backrow.solt.dto.page.PageRequestDTO;
import org.backrow.solt.dto.page.PageResponseDTO;
import org.backrow.solt.dto.plan.PlanInputDTO;
import org.backrow.solt.dto.plan.PlanViewDTO;

public interface PlanService {
    PageResponseDTO<PlanViewDTO> getPlanList(Long memberId, PageRequestDTO pageRequestDTO);
    PlanViewDTO getPlan(Long planId);
    long savePlan(PlanInputDTO planInputDTO);
    boolean modifyPlan(Long planId, PlanInputDTO planInputDTO, Long memberId);
    boolean deletePlan(Long planId, Long memberId);
    PlanViewDTO recommendPlan(PlanInputDTO planInputDTO);
}
