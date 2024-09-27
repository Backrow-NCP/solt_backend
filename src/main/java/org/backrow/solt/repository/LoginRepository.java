package org.backrow.solt.repository;

import org.backrow.solt.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface LoginRepository extends JpaRepository<Member, Long> {

    @Query("select m.name from Member m where m.name = :name")
    String checkName(String name);

    @Query("select m.email from Member m where m.email = :email")
    String checkEmail(String email);

    @Query("select m.password from Member m where m.email = :email")
    String findPwByEmail(String email);

    @Query("select m.memberId from Member m where m.email = :email")
    int findIdByEmail(String email);

    @Query("select m from Member m where m.email= :email")
    Member findByEmail(String email);
}
