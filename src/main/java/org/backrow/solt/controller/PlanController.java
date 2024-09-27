package org.backrow.solt.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.backrow.solt.dto.plan.PlanDTO;
import org.backrow.solt.dto.page.PageRequestDTO;
import org.backrow.solt.dto.page.PageResponseDTO;
import org.backrow.solt.service.plan.PlanService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@Tag(name = "Plan API", description = "Plan에 대한 작성, 조회, 수정, 삭제 기능을 수행하는 API")
@RestController
@RequestMapping("/plan")
@RequiredArgsConstructor
@Log4j2
public class PlanController {

    private final PlanService planService;

    @Operation(summary = "Plan 리스트 조회", description = "Plan 리스트 조회 ")
    @GetMapping("/planList")
    public ResponseEntity<PageResponseDTO<PlanDTO>> getPlanList(PageRequestDTO pageRequestDTO) {
        PageResponseDTO<PlanDTO> result = planService.getPlanList(pageRequestDTO);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Plan 조회", description = "PlanID에 따른 플랜 조회")
    @GetMapping("/plan/{planId}")
    public ResponseEntity<PlanDTO> getPlan(@PathVariable int planId) {
        PlanDTO result = planService.getPlan(planId);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Plan 작성", description = "새로운 Plan 작성")
    @PostMapping
    public ResponseEntity<Map<String, Long>> savePlan(@Valid @RequestBody PlanDTO planDTO) {
        Long planId = planService.savePlan(planDTO);
        return ResponseEntity.ok(Map.of("planId", planId));
    }

    @Operation(summary = "Plan 수정", description = "Plan ID를 통한 수정")
    @PutMapping("/{planId}")
    public ResponseEntity<Map<String, Boolean>> modifyPlan(
            @PathVariable int planId,
            @RequestBody PlanDTO planDTO) {
        boolean modified = planService.modifyPlan(planId, planDTO);
        return ResponseEntity.ok(Map.of("modify", modified));
    }

    @Operation(summary = "Plan 삭제", description = "Plan ID를 통한 삭제")
    @DeleteMapping("/{planId}")
    public Map<String, Boolean> deletePlan(@PathVariable int planId) {
        return Map.of("delete", planService.deletePlan(planId));
    }

    @PostMapping("/aiRecommend")
    public PlanDTO aiRecommend(@RequestBody PlanDTO planDTO) {
        return planService.aiRecommend(planDTO);
    }
}
