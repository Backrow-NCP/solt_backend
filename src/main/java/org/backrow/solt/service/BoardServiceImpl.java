package org.backrow.solt.service;

import org.backrow.solt.dto.BoardDTO;
import org.backrow.solt.dto.PageRequestDTO;
import org.backrow.solt.dto.PageResponseDTO;
import org.springframework.stereotype.Service;

@Service
public class BoardServiceImpl implements BoardService {
    @Override
    public PageResponseDTO<BoardDTO> getBoardList(PageRequestDTO pageRequestDTO) {
        return null;
    }

    @Override
    public BoardDTO getBoard(Long id) {
        return null;
    }

    @Override
    public long saveBoard(BoardDTO boardDTO) {
        return 0;
    }

    @Override
    public boolean modifyBoard(Long id, BoardDTO boardDTO) {
        return false;
    }

    @Override
    public boolean deleteBoard(Long id) {
        return false;
    }
}
