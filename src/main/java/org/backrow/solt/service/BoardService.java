package org.backrow.solt.service;

import org.backrow.solt.dto.board.BoardDTO;
import org.backrow.solt.dto.PageRequestDTO;
import org.backrow.solt.dto.PageResponseDTO;

public interface BoardService {
    PageResponseDTO<BoardDTO> getBoardList(PageRequestDTO pageRequestDTO);
    BoardDTO getBoard(Long id);
    long saveBoard(BoardDTO boardDTO);
    boolean modifyBoard(Long id, BoardDTO boardDTO);
    boolean deleteBoard(Long id);
}
