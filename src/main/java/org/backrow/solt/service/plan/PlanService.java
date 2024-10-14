package org.backrow.solt.service.plan;

import org.backrow.solt.dto.page.PageRequestDTO;
import org.backrow.solt.dto.page.PageResponseDTO;
import org.backrow.solt.dto.plan.PlanInputDTO;
import org.backrow.solt.dto.plan.PlanViewDTO;

public interface PlanService {
    PageResponseDTO<PlanViewDTO> getPlanList(long id, PageRequestDTO pageRequestDTO);
    PlanViewDTO getPlan(long id);
    long savePlan(PlanInputDTO planInputDTO);
    boolean modifyPlan(long id, PlanInputDTO planInputDTO);
    boolean deletePlan(long id);
    PlanViewDTO recommendPlan(PlanInputDTO planInputDTO);
}
