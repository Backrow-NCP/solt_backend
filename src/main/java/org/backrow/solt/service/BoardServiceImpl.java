package org.backrow.solt.service;

import lombok.RequiredArgsConstructor;
import org.backrow.solt.domain.Board;
import org.backrow.solt.domain.BoardImage;
import org.backrow.solt.domain.Member;
import org.backrow.solt.dto.board.BoardImageDTO;
import org.backrow.solt.dto.page.PageRequestDTO;
import org.backrow.solt.dto.page.PageResponseDTO;
import org.backrow.solt.dto.board.BoardInputDTO;
import org.backrow.solt.dto.board.BoardViewDTO;
import org.backrow.solt.repository.BoardRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {
    private final BoardRepository boardRepository;
    private final ModelMapper modelMapper;

    @Override
    public PageResponseDTO<BoardViewDTO> getBoardList(PageRequestDTO pageRequestDTO) {
        String[] types = pageRequestDTO.getTypes();
        String keyword = pageRequestDTO.getKeyword();
        Pageable pageable = pageRequestDTO.getPageable();

        Page<BoardViewDTO> boardPage = boardRepository.searchBoardView(types, keyword, pageable);

        return new PageResponseDTO<>(pageRequestDTO, boardPage.getContent(), (int) boardPage.getTotalElements());
    }

    @Override
    public BoardViewDTO getBoard(Long id) {
        return boardRepository.searchBoardView(id);
    }

    @Override
    public long saveBoard(BoardInputDTO boardInputDTO) {
        Board board = convertToEntity(boardInputDTO);
        setBoardImages(board);
        boardRepository.save(board);
        return board.getBoardId();
    }

    @Override
    public boolean modifyBoard(Long id, BoardInputDTO boardInputDTO) {
        Optional<Board> findBoard = boardRepository.findById(id);
        Board board = findBoard.orElseThrow();

        List<BoardImageDTO> boardImageDTOS = boardInputDTO.getBoardImages();
        List<BoardImage> boardImages = null;
        if (boardImageDTOS != null) {
            boardImages = boardImageDTOS.stream()
                    .map((boardImageDTO -> modelMapper.map(boardImageDTO, BoardImage.class)))
                    .collect(Collectors.toList());
        }

        board.modify(boardInputDTO.getTitle(), boardInputDTO.getContent(), boardImages);
        setBoardImages(board);

        boardRepository.save(board);
        return true;
    }

    @Override
    public boolean deleteBoard(Long id) {
        if (boardRepository.existsById(id)) {
            boardRepository.deleteById(id);
            return true;
        } else {
            throw new NoSuchElementException("Board not found for id: " + id);
        }
    }

    /** Board 내 Image 엔티티에 참조할 Board를 지정합니다. */
    private void setBoardImages(Board board) {
        List<BoardImage> boardImages = board.getBoardImages();
        if (boardImages != null) {
            boardImages.forEach(boardImage -> boardImage.setBoard(board));
        }
    }

    /** BoardInputDTO를 Board Entity로 매핑합니다. **/
    private Board convertToEntity(BoardInputDTO boardInputDTO) {
        Board board = modelMapper.map(boardInputDTO, Board.class);
        Member member = Member.builder()
                .memberId(boardInputDTO.getMemberId())
                .build();
        board.setMember(member);
        setBoardImages(board);
        return board;
    }
}
