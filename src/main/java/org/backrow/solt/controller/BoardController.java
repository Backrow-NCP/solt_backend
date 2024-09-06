package org.backrow.solt.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.backrow.solt.dto.BoardDTO;
import org.backrow.solt.dto.PageRequestDTO;
import org.backrow.solt.dto.PageResponseDTO;
import org.backrow.solt.service.BoardService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/board")
@RequiredArgsConstructor
@Log4j2
public class BoardController {
    private final BoardService boardService;

    @GetMapping("/list")
    PageResponseDTO<BoardDTO> getBoardList(PageRequestDTO pageRequestDTO) {
        return boardService.getBoardList(pageRequestDTO);
    }

    @GetMapping("/{id}")
    BoardDTO getBoard(@PathVariable Long id) {
        log.info("getBoard: " + id);
        return boardService.getBoard(id);
    }

    @PostMapping
    Map<String, Long> saveBoard(@RequestBody BoardDTO boardDTO) {
        return Map.of("id", boardService.saveBoard(boardDTO));
    }

    @PutMapping("/{id}")
    Map<String, Boolean> modifyBoard(@PathVariable Long id, @RequestBody BoardDTO boardDTO) {
        log.info("modifyBoard: " + id + " " + boardDTO);
        return Map.of("isSuccess", boardService.modifyBoard(id, boardDTO));
    }

    @DeleteMapping("/{id}")
    Map<String, Boolean> deleteBoard(Long id) {
        log.info("deleteBoard: " + id);
        return Map.of("isSuccess", boardService.deleteBoard(id));
    }
}
