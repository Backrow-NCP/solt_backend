package org.backrow.solt.repository;

import org.backrow.solt.domain.personality.PersonalityTest;
import org.backrow.solt.domain.personality.PersonalityTestLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PersonalityTestLogRepository extends JpaRepository<PersonalityTestLog, Long> {
    List<PersonalityTestLog> findByMemberMemberId(Long memberId);
}
