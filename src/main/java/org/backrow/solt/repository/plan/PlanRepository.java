package org.backrow.solt.repository.plan;

import org.backrow.solt.domain.plan.Plan;
import org.backrow.solt.repository.plan.search.PlanSearch;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanRepository extends JpaRepository<Plan, Long>, PlanSearch {
    void deleteByPlanIdAndMember_MemberId(Long planId, Long memberId);
}
