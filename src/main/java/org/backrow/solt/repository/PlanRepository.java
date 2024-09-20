package org.backrow.solt.repository;

import org.backrow.solt.dto.PlanDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanRepository extends JpaRepository<PlanDTO, Integer> {
}
