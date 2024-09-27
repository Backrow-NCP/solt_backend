package org.backrow.solt.service.plan;

import lombok.RequiredArgsConstructor;
import org.backrow.solt.domain.Plan;
import org.backrow.solt.dto.plan.PlanDTO;
import org.backrow.solt.dto.page.PageRequestDTO;
import org.backrow.solt.dto.page.PageResponseDTO;
import org.backrow.solt.repository.PlanRepository;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

@Service
@RequiredArgsConstructor
public class PlanServiceImpl implements PlanService {

    private final PlanRepository planRepository;

    @Override
    public PlanDTO getPlan(int planId) {
        // Plan 조회 로직
        return null; // 실제 로직으로 변경 필요
    }

    @Override
    public PageResponseDTO<PlanDTO> getPlanList(PageRequestDTO pageRequestDTO) {
        // Plan 리스트 조회 로직
        return null;
    }

    @Override
    public long savePlan(PlanDTO planDTO) {
        // Plan 작성 로직
        return 0L;
    }

    @Override
    public boolean modifyPlan(int planId, PlanDTO planDTO) {
        // Plan 수정 로직
        return true;
    }

    @Override
    public boolean deletePlan(int planId) {
        // Plan 삭제 로직
        try{
            planRepository.deleteById(planId);
            return true;
        }catch (EmptyResultDataAccessException e){
            throw new NotFoundException("Plan not found" + planId);
        }
    }

    @Override
    public PlanDTO aiRecommend(PlanDTO planDTO) {
        return null;
    }

}

