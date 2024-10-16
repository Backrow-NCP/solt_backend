package org.backrow.solt.repository;

import org.backrow.solt.domain.personality.PersonalityTest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonalityTestRepository extends JpaRepository<PersonalityTest, Integer> {

}
