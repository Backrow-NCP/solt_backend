package org.backrow.solt.repository.board.search;

import org.backrow.solt.dto.board.BoardViewDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BoardSearch {
    Page<BoardViewDTO> searchBoardView(String[] types, String keyword, Pageable pageable);
    BoardViewDTO searchBoardView(Long boardId);
    Page<BoardViewDTO> searchBoardViewWithBoardPlan(String[] types, String keyword, Pageable pageable);
    Page<BoardViewDTO> searchBoardViewByMemberIdWithBoardPlan(Long memberId, String[] types, String keyword, Pageable pageable);
    BoardViewDTO searchBoardViewWithBoardPlan(Long boardId);
}
