package org.backrow.solt.repository;

import org.backrow.solt.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegisterRepository extends JpaRepository<Member, Integer> {
}
