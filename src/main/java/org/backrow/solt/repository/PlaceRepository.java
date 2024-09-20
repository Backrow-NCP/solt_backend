package org.backrow.solt.repository;

import org.backrow.solt.dto.PlaceDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaceRepository extends JpaRepository<PlaceDTO, Integer> {
}
