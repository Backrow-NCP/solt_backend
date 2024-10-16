package org.backrow.solt.service.board;

import org.backrow.solt.dto.board.BoardModifyDTO;
import org.backrow.solt.dto.page.PageRequestDTO;
import org.backrow.solt.dto.page.PageResponseDTO;
import org.backrow.solt.dto.board.BoardInputDTO;
import org.backrow.solt.dto.board.BoardViewDTO;

public interface BoardService {
    PageResponseDTO<BoardViewDTO> getBoardList(PageRequestDTO pageRequestDTO);
    PageResponseDTO<BoardViewDTO> getBoardListByMemberId(Long memberId, PageRequestDTO pageRequestDTO);
    BoardViewDTO getBoard(Long boardId);
    long saveBoard(BoardInputDTO boardInputDTO, Long memberId);
    boolean modifyBoard(Long boardId, BoardModifyDTO boardModifyDTO, Long memberId);
    boolean deleteBoard(Long boardId, Long memberId);
}
