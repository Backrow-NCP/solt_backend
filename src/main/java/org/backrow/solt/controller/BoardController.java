package org.backrow.solt.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.backrow.solt.dto.board.BoardModifyDTO;
import org.backrow.solt.dto.page.PageRequestDTO;
import org.backrow.solt.dto.page.PageResponseDTO;
import org.backrow.solt.dto.board.BoardInputDTO;
import org.backrow.solt.dto.board.BoardViewDTO;
import org.backrow.solt.security.CustomUserDetails;
import org.backrow.solt.service.BoardService;
import org.backrow.solt.service.FileStorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@Tag(name = "게시판 API", description = "게시글에 대한 작성, 조회, 수정, 삭제 기능을 수행하는 API입니다.")
@RestController
@RequestMapping("/board")
@RequiredArgsConstructor
@Log4j2
public class BoardController {
    private final BoardService boardService;

    @Operation(summary = "게시글 목록 조회", description = "게시판의 모든 게시글 목록을 페이지로 조회합니다.")
    @GetMapping("/list")
    public ResponseEntity<PageResponseDTO<BoardViewDTO>> getBoardList(PageRequestDTO pageRequestDTO) {
        PageResponseDTO<BoardViewDTO> result = boardService.getBoardList(pageRequestDTO);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "작성한 게시글 목록 조회", description = "특정 멤버가 작성한 게시글 목록을 페이지로 조회합니다.")
    @GetMapping("/list/{id}")
    public ResponseEntity<PageResponseDTO<BoardViewDTO>> getBoardListByMemberId(@PathVariable Long id, PageRequestDTO pageRequestDTO) {
        PageResponseDTO<BoardViewDTO> result = boardService.getBoardListByMemberId(id, pageRequestDTO);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "게시글 조회", description = "ID를 통해 특정 게시글을 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<BoardViewDTO> getBoard(@PathVariable Long id) {
        return ResponseEntity.ok(boardService.getBoard(id));
    }

    @Operation(summary = "게시글 작성", description = "새로운 게시글을 작성합니다.")
    @PostMapping
    public ResponseEntity<Map<String, Long>> saveBoard(
            @Valid @RequestBody BoardInputDTO boardInputDTO,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long id = boardService.saveBoard(boardInputDTO, userDetails.getMemberId());
        return ResponseEntity.ok(Map.of("id", id));
    }

    @Operation(summary = "게시글 수정", description = "ID를 통해 특정 게시글을 수정합니다.")
    @PutMapping("/{id}")
    public Map<String, Boolean> modifyBoard(
            @PathVariable Long id,
            @RequestBody BoardModifyDTO boardModifyDTO,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return Map.of("isSuccess", boardService.modifyBoard(id, boardModifyDTO, userDetails.getMemberId()));
    }

    @Operation(summary = "게시글 삭제", description = "ID를 통해 특정 게시글을 삭제합니다.")
    @DeleteMapping("/{id}")
    public Map<String, Boolean> deleteBoard(
            @PathVariable Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return Map.of("isSuccess", boardService.deleteBoard(id, userDetails.getMemberId()));
    }
}
