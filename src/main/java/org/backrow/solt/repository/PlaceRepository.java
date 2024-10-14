package org.backrow.solt.repository;

import org.backrow.solt.domain.plan.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaceRepository extends JpaRepository<Route, Long> {
}
