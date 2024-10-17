package org.backrow.solt.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.backrow.solt.dto.page.PageRequestDTO;
import org.backrow.solt.dto.page.PageResponseDTO;
import org.backrow.solt.dto.plan.PlanInputDTO;
import org.backrow.solt.dto.plan.PlanViewDTO;
import org.backrow.solt.security.CustomUserDetails;
import org.backrow.solt.service.plan.PlanService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@Tag(name = "여행 일정 API", description = "플랜에 대한 작성, 조회, 수정, 삭제 기능을 수행하는 API입니다.")
@RestController
@RequestMapping("/plans")
@RequiredArgsConstructor
@Log4j2
public class PlanController {
    private final PlanService planService;

    @Operation(summary = "멤버의 플랜 리스트 조회", description = "멤버 ID를 통해 특정 멤버의 플랜 목록을 페이지로 조회합니다.")
    @GetMapping("/list/{id}")
    public ResponseEntity<PageResponseDTO<PlanViewDTO>> getPlanList(@PathVariable Long id, PageRequestDTO pageRequestDTO) {
        PageResponseDTO<PlanViewDTO> result = planService.getPlanList(id, pageRequestDTO);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "플랜 조회", description = "ID를 통해 특정 플랜을 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<PlanViewDTO> getPlan(@PathVariable Long id) {
        return ResponseEntity.ok(planService.getPlan(id));
    }

    @Operation(summary = "플랜 작성", description = "새로운 플랜을 저장합니다.")
    @PostMapping
    public ResponseEntity<Map<String, Long>> savePlan(
            @Valid @RequestBody PlanInputDTO planInputDTO,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        long memberId = userDetails.getMemberId();
        planInputDTO.setMemberId(memberId);
        long id = planService.savePlan(planInputDTO);
        return ResponseEntity.ok(Map.of("id", id));
    }

    @Operation(summary = "플랜 수정", description = "플랜 ID를 통해 특정 플랜을 수정합니다.")
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Boolean>> modifyPlan(
            @PathVariable Long id,
            @RequestBody PlanInputDTO planInputDTO,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        boolean result = planService.modifyPlan(id, planInputDTO, userDetails.getMemberId());
        return ResponseEntity.ok(Map.of("isSuccess", result));
    }

    @Operation(summary = "플랜 삭제", description = "ID를 통해 특정 플랜을 삭제합니다.")
    @DeleteMapping("/{id}")
    public Map<String, Boolean> deletePlan(
            @PathVariable Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return Map.of("isSuccess", planService.deletePlan(id, userDetails.getMemberId()));
    }

    @Operation(summary = "추천 플랜 생성", description = "AI를 통해 여행 일정에 맞는 플랜을 추천받습니다.")
    @PostMapping("/recom")
    public ResponseEntity<PlanViewDTO> recommendPlan(@RequestBody PlanInputDTO planInputDTO) {
        return ResponseEntity.ok(planService.recommendPlan(planInputDTO));
    }
}
