package org.backrow.solt.service;

import org.backrow.solt.dto.board.BoardModifyDTO;
import org.backrow.solt.dto.page.PageRequestDTO;
import org.backrow.solt.dto.page.PageResponseDTO;
import org.backrow.solt.dto.board.BoardInputDTO;
import org.backrow.solt.dto.board.BoardViewDTO;

public interface BoardService {
    PageResponseDTO<BoardViewDTO> getBoardList(PageRequestDTO pageRequestDTO);
    BoardViewDTO getBoard(Long id);
    long saveBoard(BoardInputDTO boardInputDTO);
    boolean modifyBoard(Long id, BoardModifyDTO boardModifyDTO);
    boolean deleteBoard(Long id);
}
