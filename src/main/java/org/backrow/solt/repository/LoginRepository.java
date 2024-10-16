package org.backrow.solt.repository;

import org.backrow.solt.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LoginRepository extends JpaRepository<Member, Long> {

    @Query("select m.name from Member m where m.name = :name")
    String checkName(@Param("name") String name);

    @Query("select m.email from Member m where m.email = :email")
    String checkEmail(@Param("email") String email);

    @Query("select m.password from Member m where m.email = :email")
    String findPwByEmail(@Param("email") String email);

    @Query("select m.memberId from Member m where m.email = :email")
    Long findIdByEmail(@Param("email") String email);

    @Query("select m from Member m where m.email= :email")
    Member findByEmail(@Param("email") String email);
}
