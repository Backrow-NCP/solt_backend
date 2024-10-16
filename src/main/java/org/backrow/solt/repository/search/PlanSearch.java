package org.backrow.solt.repository.search;

import org.backrow.solt.dto.plan.PlanViewDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PlanSearch {
    Page<PlanViewDTO> searchPlanView(String[] types, String keyword, Pageable pageable);
    PlanViewDTO searchPlanView(Long planId);
}
