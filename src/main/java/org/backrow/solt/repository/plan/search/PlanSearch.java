package org.backrow.solt.repository.plan.search;

import org.backrow.solt.dto.plan.PlanViewDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PlanSearch {
    Page<PlanViewDTO> searchPlanViewWithMemberId(String[] types, String keyword, Pageable pageable, Long memberId);
    PlanViewDTO searchPlanView(Long planId);
}
