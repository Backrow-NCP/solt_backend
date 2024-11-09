package org.backrow.solt.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.backrow.solt.dto.like.LikeDTO;
import org.backrow.solt.security.CustomUserDetails;
import org.backrow.solt.service.board.LikeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@Tag(name = "좋아요 API", description = "게시글의 좋아요 수 조회, 좋아요 등록 및 취소 기능을 수행하는 API입니다.")
@RestController
@RequestMapping("/like")
@RequiredArgsConstructor
@Log4j2
public class LikeController {
    private final LikeService likeService;

    @Operation(summary = "좋아요 수 조회", description = "ID를 통해 특정 게시글의 좋아요 개수를 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Integer>> getLikesByBoardId(@PathVariable Long id) {
        return ResponseEntity.ok(Map.of("likeCount", likeService.getLikesByBoardId(id)));
    }

    @Operation(summary = "좋아요 여부 확인", description = "게시글 ID와 멤버 ID를 이용해 주어진 멤버가 특정 게시글에 대해 좋아요를 눌렀는지 여부를 확인합니다.")
    @GetMapping("/status")
    public ResponseEntity<Map<String, Boolean>> isBoardLikedByUser(@Valid LikeDTO likeDTO) {
        return ResponseEntity.ok(Map.of("isLiked", likeService.isLiked(likeDTO)));
    }

    @Operation(summary = "좋아요 등록/취소 토글", description = "게시글 ID와 멤버 ID를 통해 좋아요를 등록/취소합니다.<br>토글 후 좋아요 개수를 반환힙니다.")
    @PostMapping
    public ResponseEntity<Map<String, Integer>> toggleLike(
            @Valid @RequestBody LikeDTO likeDTO,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        likeDTO.setMemberId(userDetails.getMemberId());
        return ResponseEntity.ok(Map.of("likeCount", likeService.toggleLike(likeDTO)));
    }
}
