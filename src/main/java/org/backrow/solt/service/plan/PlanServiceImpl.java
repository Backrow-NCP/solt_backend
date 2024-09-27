package org.backrow.solt.service.plan;

import lombok.RequiredArgsConstructor;
import org.backrow.solt.domain.Plan;
import org.backrow.solt.dto.plan.PlanConvertion;
import org.backrow.solt.dto.plan.PlanDTO;
import org.backrow.solt.dto.page.PageRequestDTO;
import org.backrow.solt.dto.page.PageResponseDTO;
import org.backrow.solt.repository.PlanRepository;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import javax.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
public class PlanServiceImpl implements PlanService {

    private final PlanRepository planRepository;
    private final PlanConvertion planConvertion;

    @Override
    public PlanDTO getPlan(int planId) {
        // Plan 조회 로직
        Plan plan = planRepository.findById(planId)
                                  .orElseThrow(() -> new EntityNotFoundException("Plan not found" + planId));
        return planConvertion.convertToDTO(plan);
    }

    @Override
    public PageResponseDTO<PlanDTO> getPlanList(PageRequestDTO pageRequestDTO) {
        // Plan 리스트 조회 로직
        return null;
    }

    @Override
    public long savePlan(PlanDTO planDTO) {
        // Plan 작성 로직
        Plan plan = planConvertion.convertToEntity(planDTO);
        planRepository.save(plan);
        return plan.getPlanId();
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

