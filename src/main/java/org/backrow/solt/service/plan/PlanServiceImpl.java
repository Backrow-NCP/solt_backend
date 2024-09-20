package org.backrow.solt.service.plan;

import org.backrow.solt.dto.PlanDTO;
import org.backrow.solt.dto.page.PageRequestDTO;
import org.backrow.solt.dto.page.PageResponseDTO;
import org.backrow.solt.repository.PlanRepository;
import org.backrow.solt.service.ai.AIPlanMaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PlanServiceImpl implements PlanService {

    @Autowired
    private PlanRepository planRepository;
    private AIPlanMaker aiPlanMaker;

    @Override
    public PlanDTO getPlan(int planId) {
        // Plan 조회 로직
        return null; // 실제 로직으로 변경 필요
    }

    @Override
    public PageResponseDTO<PlanDTO> getPlanList(PageRequestDTO pageRequestDTO) {
        // Plan 리스트 조회 로직
        return null; // 실제 로직으로 변경 필요
    }

    @Override
    public boolean deletePlan(int planId) {
        // Plan 삭제 로직
        return false; // 실제 로직으로 변경 필요
    }

    @Override
    public boolean modifyPlan(PlanDTO planDTO) {
        // Plan 수정 로직
        return false; // 실제 로직으로 변경 필요
    }

    @Override
    public long savePlan(PlanDTO planDTO) {
        // Plan 저장 로직
        return 0L; // 실제 로직으로 변경 필요
    }

    @Override
    public PlanDTO aiRecommend(PlanDTO planDTO) {
        // AI 추천 로직
        return null; // 실제 로직으로 변경 필요
    }
}

