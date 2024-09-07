package org.backrow.solt.service;

import lombok.RequiredArgsConstructor;
import org.backrow.solt.domain.Board;
import org.backrow.solt.dto.BoardDTO;
import org.backrow.solt.dto.PageRequestDTO;
import org.backrow.solt.dto.PageResponseDTO;
import org.backrow.solt.repository.BoardRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
    public PageResponseDTO<BoardDTO> getBoardList(PageRequestDTO pageRequestDTO) {
        Pageable pageable = PageRequest.of(pageRequestDTO.getPage(), pageRequestDTO.getSize());
        Page<Board> boardPage = boardRepository.findAll(pageable);

        List<BoardDTO> dtoList = boardPage.stream()
                .map(board -> modelMapper.map(board, BoardDTO.class))
                .collect(Collectors.toList());

        return new PageResponseDTO<>(pageRequestDTO, dtoList, boardPage.getTotalPages());
    }

    @Override
    public BoardDTO getBoard(Long id) {
        Optional<Board> findBoard = boardRepository.findById(id);

        Board board = findBoard.orElseThrow();

        return modelMapper.map(board, BoardDTO.class);
    }

    @Override
    public long saveBoard(BoardDTO boardDTO) {
        Board board = modelMapper.map(boardDTO, Board.class);
        boardRepository.save(board);
        return board.getBoardId();
    }

    @Override
    public boolean modifyBoard(Long id, BoardDTO boardDTO) {
        Optional<Board> findBoard = boardRepository.findById(id);
        Board board = findBoard.orElseThrow();

        board.modify(boardDTO.getTitle(), boardDTO.getContent());
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
}
