package org.backrow.solt.repository;

import org.backrow.solt.domain.plan.Plan;
import org.backrow.solt.repository.search.PlanSearch;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanRepository extends JpaRepository<Plan, Long>, PlanSearch {
}
