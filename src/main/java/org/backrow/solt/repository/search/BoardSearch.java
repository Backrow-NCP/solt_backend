package org.backrow.solt.repository.search;

import org.backrow.solt.dto.board.BoardViewDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BoardSearch {
    Page<BoardViewDTO> searchBoardView(String[] types, String keyword, Pageable pageable);
    Page<BoardViewDTO> searchBoardViewByMemberId(Long memberId, String[] types, String keyword, Pageable pageable);
    BoardViewDTO searchBoardView(Long boardId);
}
