package org.backrow.solt.repository;

import org.backrow.solt.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoginRepository extends JpaRepository<Member, Integer> {
}
