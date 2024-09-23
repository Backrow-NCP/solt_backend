package org.backrow.solt.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.backrow.solt.dto.PlanDTO;
import org.backrow.solt.dto.page.PageRequestDTO;
import org.backrow.solt.dto.page.PageResponseDTO;
import org.backrow.solt.service.plan.PlanService;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@Tag(name = "Plan API", description = "Plan에 대한 작성, 조회, 수정, 삭제 기능을 수행하는 API")
@RestController
@RequestMapping("/plan")
@RequiredArgsConstructor
@Log4j2
public class PlanController {

    private final PlanService planService;

    @Operation(summary = "Plan 조회", description = "PlanID에 따른 플랜 조회")
    @GetMapping("/plan/{planId}")
    public PlanDTO getPlan(@PathVariable int planId) {
        return planService.getPlan(planId);
    }

    @Operation(summary = "Plan 리스트 조회", description = "Plan 리스트 조회 ")
    @GetMapping
    public PageResponseDTO<PlanDTO> getPlanList(PageRequestDTO pageRequestDTO) {
        return planService.getPlanList(pageRequestDTO);
    }

    @Operation(summary = "Plan 삭제", description = "Plan ID를 통한 삭제")
    @DeleteMapping("/{planId}")
    public Map<String, Boolean> deletePlan(@PathVariable int planId) {
        boolean delete = planService.deletePlan(planId);
        return Map.of("DELETE", delete);
    }

    @Operation(summary = "Plan 수정", description = "Plan ID를 통한 수정")
    @PutMapping
    public Map<String, Boolean> modifyPlan(@RequestBody PlanDTO planDTO) {
        boolean modified = planService.modifyPlan(planDTO);
        return Map.of("modified", modified);
    }

    @Operation(summary = "Plan 작성", description = "새로운 Plan 작성")
    @PostMapping
    public long savePlan(@RequestBody PlanDTO planDTO) {
        return planService.savePlan(planDTO);
    }

    @PostMapping("/aiRecommend")
    public PlanDTO aiRecommend(@RequestBody PlanDTO planDTO) {
        return planService.aiRecommend(planDTO);
    }
}
