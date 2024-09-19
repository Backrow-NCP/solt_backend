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
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.webjars.NotFoundException;

import java.util.Optional;
import java.util.Set;
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
    public PageResponseDTO<BoardViewDTO> getBoardListByMemberId(Long id, PageRequestDTO pageRequestDTO) {
        String[] types = pageRequestDTO.getTypes();
        String keyword = pageRequestDTO.getKeyword();
        Pageable pageable = pageRequestDTO.getPageable();

        Page<BoardViewDTO> boardPage = boardRepository.searchBoardViewByMemberId(id, types, keyword, pageable);

        return new PageResponseDTO<>(pageRequestDTO, boardPage.getContent(), (int) boardPage.getTotalElements());
    }

    @Override
    public BoardViewDTO getBoard(Long id) {
        BoardViewDTO result = boardRepository.searchBoardView(id);
        if (result == null) {
            throw new NotFoundException("Board not found: " + id);
        }
        return result;
    }

    @Override
    public long saveBoard(BoardInputDTO boardInputDTO) {
        Board board = convertToEntity(boardInputDTO);
        setBoardImages(board);
        boardRepository.save(board);
        return board.getBoardId();
    }

    @Transactional
    @Override
    public boolean modifyBoard(Long id, BoardInputDTO boardInputDTO) {
        Optional<Board> findBoard = boardRepository.findById(id);
        Board board = findBoard.orElseThrow(() -> new NotFoundException("Board not found: " + id));

        Set<BoardImageDTO> boardImageDTOS = boardInputDTO.getBoardImages();
        Set<BoardImage> boardImages = null;
        if (boardImageDTOS != null) {
            boardImages = boardImageDTOS.stream()
                    .map((boardImageDTO -> modelMapper.map(boardImageDTO, BoardImage.class)))
                    .collect(Collectors.toSet());
        }

        board.modify(boardInputDTO.getTitle(), boardInputDTO.getContent(), boardImages);
        setBoardImages(board);

        boardRepository.save(board);
        return true;
    }

    @Override
    public boolean deleteBoard(Long id) {
        try {
            boardRepository.deleteById(id);
            return true;
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Board not found: " + id);
        }
    }

    /** Board 내 Image 엔티티에 참조할 Board를 지정합니다. */
    private void setBoardImages(Board board) {
        Set<BoardImage> boardImages = board.getBoardImages();
        if (boardImages != null) {
            boardImages.forEach(boardImage -> boardImage.setBoard(board));
        }
    }

    /** BoardInputDTO를 Board Entity로 매핑합니다. **/
    private Board convertToEntity(BoardInputDTO boardInputDTO) {
        Board board = modelMapper.map(boardInputDTO, Board.class);
        board.setBoardId(null);
        Member member = Member.builder()
                .memberId(boardInputDTO.getMemberId())
                .build();
        board.setMember(member);
        return board;
    }
}
