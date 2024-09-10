package org.backrow.solt.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.backrow.solt.dto.ReplyDTO;
import org.backrow.solt.dto.PageRequestDTO;
import org.backrow.solt.dto.PageResponseDTO;
import org.backrow.solt.service.ReplyService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@Tag(name = "댓글 API", description = "댓글에 대한 작성, 조회, 수정, 삭제 기능을 수행하는 API입니다.")
@RestController
@RequestMapping("/reply")
@RequiredArgsConstructor
@Log4j2
public class ReplyController {
    private final ReplyService replyService;

    @Operation(summary = "댓글 목록 조회", description = "게시글 ID를 통해 댓글 목록을 페이지로 조회합니다.")
    @GetMapping("/list/{id}")
    public ResponseEntity<PageResponseDTO<ReplyDTO>> getRepliesByBoardId(
            @PathVariable Long id,
            PageRequestDTO pageRequestDTO) {
        try {
            PageResponseDTO<ReplyDTO> result = replyService.getRepliesByBoardId(id, pageRequestDTO);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "댓글 작성", description = "새로운 댓글을 작성합니다.")
    @PostMapping
    public ResponseEntity<Map<String, Long>> saveReply(
            @Valid @RequestBody ReplyDTO replyDTO,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            ResponseEntity.badRequest().build();
        }
        try {
            Long id = replyService.saveReply(replyDTO);
            return ResponseEntity.ok(Map.of("id", id));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "댓글 수정", description = "ID를 통해 특정 댓글을 수정합니다.")
    @PutMapping("/{id}")
    public Map<String, Boolean> modifyReply(
            @PathVariable Long id,
            @RequestBody ReplyDTO replyDTO,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            return Map.of("isSuccess", false);
        }
        try {
            return Map.of("isSuccess", replyService.modifyReply(id, replyDTO));
        } catch (Exception e) {
            log.error(e.getMessage());
            return Map.of("isSuccess", false);
        }
    }

    @Operation(summary = "댓글 삭제", description = "ID를 통해 특정 댓글을 삭제합니다.")
    @DeleteMapping("/{id}")
    public Map<String, Boolean> deleteReply(@PathVariable Long id) {
        try {
            return Map.of("isSuccess", replyService.deleteReply(id));
        } catch (Exception e) {
            return Map.of("isSuccess", false);
        }
    }
}
