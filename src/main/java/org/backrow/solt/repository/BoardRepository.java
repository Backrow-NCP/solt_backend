package org.backrow.solt.repository;

import org.backrow.solt.domain.Board;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Long> {
//    Page<Board> findByMemberId(Long memberId);
}
