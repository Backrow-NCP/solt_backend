package org.backrow.solt.repository;

import org.backrow.solt.domain.board.Board;
import org.backrow.solt.repository.search.BoardSearch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Long>, BoardSearch {
    Page<Board> findByMemberMemberId(Integer memberId, Pageable pageable);
}
