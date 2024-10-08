package org.backrow.solt.service.plan;

import lombok.RequiredArgsConstructor;
import org.backrow.solt.domain.Member;
import org.backrow.solt.domain.plan.*;
import org.backrow.solt.dto.page.PageRequestDTO;
import org.backrow.solt.dto.page.PageResponseDTO;
import org.backrow.solt.dto.plan.PlanInputDTO;
import org.backrow.solt.dto.plan.PlanViewDTO;
import org.backrow.solt.repository.PlanRepository;
import org.backrow.solt.service.ai.MapAPIService;
import org.backrow.solt.service.ai.PlanAiService;
import org.modelmapper.ModelMapper;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlanServiceImpl implements PlanService {

    private final PlanRepository planRepository;
    private final ModelMapper modelMapper;

    private final PlanAiService planAiService;
    private final MapAPIService googleMapService;

    @Override
    public PageResponseDTO<PlanViewDTO> getPlanList(long id, PageRequestDTO pageRequestDTO) { // List 조회 시에는 Plan의 세부 내용은 필요 없지 않을까..?
        String[] types = pageRequestDTO.getTypes();
        String keyword = pageRequestDTO.getKeyword();
        Pageable pageable = pageRequestDTO.getPageable();

        Page<PlanViewDTO> planPage = planRepository.searchPlanView(types, keyword, pageable);

        return new PageResponseDTO<>(pageRequestDTO, planPage.getContent(), (int) planPage.getTotalElements());
    }

    @Override
    public PlanViewDTO getPlan(long id) {
        PlanViewDTO result = planRepository.searchPlanView(id);
        if (result == null) {
            throw new NotFoundException("Plan not found: " + id);
        }
        return result;
    }

    @Override
    public long savePlan(PlanInputDTO planInputDTO) {
        Plan plan = convertToEntity(planInputDTO);
        assignPlanToEntities(plan);
        planRepository.save(plan);
        return plan.getPlanId();
    }

    @Override
    public boolean modifyPlan(long id, PlanInputDTO planInputDTO) {
        Optional<Plan> findPlan = planRepository.findById(id);
        Plan plan = findPlan.orElseThrow(() -> new NotFoundException("Plan not found: " + id));

        Set<Place> places = mapToEntitySet(planInputDTO.getPlaces(), Place.class);
        Set<Route> routes = mapToEntitySet(planInputDTO.getRoutes(), Route.class);
//        Set<Theme> themes = mapToEntitySet(planInputDTO.getThemes(), Theme.class);

        plan.modify(planInputDTO.getTitle(), places, routes);
        planRepository.save(plan);
        return true;
    }

    @Override
    public boolean deletePlan(long id) {
        try {
            planRepository.deleteById(id);
            return true;
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Plan not found: " + id);
        }
    }

    @Override
    public PlanViewDTO recommendPlan(PlanInputDTO planInputDTO) {
        return null; // 임시 보류
    }

    /** PlanInputDTO를 Plan 엔티티로 변환합니다. **/
    private Plan convertToEntity(PlanInputDTO planInputDTO) {
        Plan result = modelMapper.map(planInputDTO, Plan.class);

        Member member = Member.builder()
                .memberId(planInputDTO.getMemberId())
                .build();
        result.setMember(member);

        return result;
    }

    /** DTO 객체 Set을 엔티티 객체 Set으로 변환합니다. **/
    private <D, E> Set<E> mapToEntitySet(Set<D> dtoSet, Class<E> entityClass) {
        if (dtoSet == null) return null;
        return dtoSet.stream()
                .map(dto -> modelMapper.map(dto, entityClass))
                .collect(Collectors.toSet());
    }

    /** Place와 Route의 Plan 필드에 관계를 설정합니다. **/
    private void assignPlanToEntities(Plan plan) {
        plan.setPlaces(plan.getPlaces().stream()
                .peek(place -> place.setPlan(plan))
                .collect(Collectors.toSet()));

        plan.setRoutes(plan.getRoutes().stream()
                .peek(route -> route.setPlan(plan))
                .collect(Collectors.toSet()));
    }
}