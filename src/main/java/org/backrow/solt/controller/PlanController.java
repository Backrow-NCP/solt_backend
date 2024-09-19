package org.backrow.solt.controller;

import org.backrow.solt.dto.PlanDTO;
import org.backrow.solt.dto.page.PageRequestDTO;
import org.backrow.solt.dto.page.PageResponseDTO;
import org.backrow.solt.service.plan.PlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/plans")
public class PlanController {

    @Autowired
    private PlanService planService;

    @GetMapping("/{planId}")
    public PlanDTO getPlan(@PathVariable int planId) {
        return planService.getPlan(planId);
    }

    @GetMapping
    public PageResponseDTO<PlanDTO> getPlanList(PageRequestDTO pageRequestDTO) {
        return planService.getPlanList(pageRequestDTO);
    }

    @DeleteMapping("/{planId}")
    public Map<String, Boolean> deletePlan(@PathVariable int planId) {
        boolean deleted = planService.deletePlan(planId);
        return Map.of("deleted", deleted);
    }

    @PutMapping
    public Map<String, Boolean> modifyPlan(@RequestBody PlanDTO planDTO) {
        boolean modified = planService.modifyPlan(planDTO);
        return Map.of("modified", modified);
    }

    @PostMapping
    public long savePlan(@RequestBody PlanDTO planDTO) {
        return planService.savePlan(planDTO);
    }

    @PostMapping("/aiRecommend")
    public PlanDTO aiRecommend(@RequestBody PlanDTO planDTO) {
        return planService.aiRecommend(planDTO);
    }
}
