package org.backrow.solt.service;

import org.backrow.solt.dto.page.PageRequestDTO;
import org.backrow.solt.dto.page.PageResponseDTO;
import org.backrow.solt.dto.board.BoardInputDTO;
import org.backrow.solt.dto.board.BoardViewDTO;

public interface BoardService {
    PageResponseDTO<BoardViewDTO> getBoardList(PageRequestDTO pageRequestDTO);
    PageResponseDTO<BoardViewDTO> getBoardListByMemberId(Long id, PageRequestDTO pageRequestDTO);
    BoardViewDTO getBoard(Long id);
    long saveBoard(BoardInputDTO boardDTO);
    boolean modifyBoard(Long id, BoardInputDTO boardDTO);
    boolean deleteBoard(Long id);
}
