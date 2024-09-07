package org.backrow.solt.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.backrow.solt.dto.BoardDTO;
import org.backrow.solt.dto.PageRequestDTO;
import org.backrow.solt.dto.PageResponseDTO;
import org.backrow.solt.service.BoardService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/board")
@RequiredArgsConstructor
@Log4j2
public class BoardController {
    private final BoardService boardService;

    @GetMapping("/list")
    ResponseEntity<PageResponseDTO<BoardDTO>> getBoardList(PageRequestDTO pageRequestDTO) {
        try {
            PageResponseDTO<BoardDTO> result = boardService.getBoardList(pageRequestDTO);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    ResponseEntity<BoardDTO> getBoard(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(boardService.getBoard(id));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping
    ResponseEntity<Map<String, Long>> saveBoard(@RequestBody BoardDTO boardDTO) {
        try {
            Long id = boardService.saveBoard(boardDTO);
            return ResponseEntity.ok(Map.of("id", id));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    Map<String, Boolean> modifyBoard(@PathVariable Long id, @RequestBody BoardDTO boardDTO) {
        try {
            return Map.of("isSuccess", boardService.modifyBoard(id, boardDTO));
        } catch (Exception e) {
            return Map.of("isSuccess", false);
        }
    }

    @DeleteMapping("/{id}")
    Map<String, Boolean> deleteBoard(@PathVariable Long id) {
        try {
            return Map.of("isSuccess", boardService.deleteBoard(id));
        } catch (Exception e) {
            return Map.of("isSuccess", false);
        }
    }
}
