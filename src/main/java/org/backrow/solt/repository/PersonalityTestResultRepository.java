package org.backrow.solt.repository;

import org.backrow.solt.domain.personality.Result;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PersonalityTestResultRepository extends JpaRepository<Result, Integer> {
    Optional<Result> findById(Integer id);
}
