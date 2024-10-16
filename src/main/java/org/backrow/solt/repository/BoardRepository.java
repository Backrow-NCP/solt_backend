package org.backrow.solt.repository;

import org.backrow.solt.domain.board.Board;
import org.backrow.solt.repository.search.BoardSearch;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Long>, BoardSearch {
    void deleteByBoardIdAndMember_MemberId(Long boardId, Long memberId);
}
