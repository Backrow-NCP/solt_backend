package org.backrow.solt.service.plan;

import lombok.RequiredArgsConstructor;
import org.backrow.solt.domain.plan.*;
import org.backrow.solt.dto.plan.PlanConvertion;
import org.backrow.solt.dto.plan.PlanDTO;
import org.backrow.solt.dto.page.PageRequestDTO;
import org.backrow.solt.dto.page.PageResponseDTO;
import org.backrow.solt.repository.PlanRepository;
import org.modelmapper.ModelMapper;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.webjars.NotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlanServiceImpl implements PlanService {

    private final PlanRepository planRepository;
    private final PlanConvertion planConvertion;
    private final ModelMapper modelMapper;

    @Override
    public PlanDTO getPlan(int planId) {
        // Plan 조회 로직
        Plan plan = planRepository.findById(planId)
                                  .orElseThrow(() -> new NotFoundException("Plan not found : " + planId));
        return planConvertion.convertToDTO(plan);
    }

    @Override
    public PageResponseDTO<PlanDTO> getPlanList(PageRequestDTO pageRequestDTO) {
        // Plan 리스트 조회 로직
        Pageable pageable = pageRequestDTO.getPageable("regDate"); // 정렬 기준 필드 명시 (예: 등록일로 정렬)
        Page<Plan> planPage = planRepository.findAll(pageable);

        // Plan 리스트를 PlanDTO 리스트로 변환
        List<PlanDTO> planDTOList = planPage.getContent().stream()
                .map(planConvertion::convertToDTO) // Plan -> PlanDTO 변환
                .collect(Collectors.toList());

        // PageResponseDTO 반환 (리스트, 전체 요소 수, 전체 페이지 수)
        return new PageResponseDTO<>(planDTOList, planPage.getTotalElements(), planPage.getTotalPages());
    }


    @Override
    public long savePlan(PlanDTO planDTO) {
        // Plan 작성 로직
        Plan plan = planConvertion.convertToEntity(planDTO);
        planRepository.save(plan);
        return plan.getPlanId();
    }

    @Transactional
    @Override
    public boolean modifyPlan(int planId, PlanDTO planDTO) {
        // Plan 수정 로직: Plan 조회 후 없으면 예외 처리
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new NotFoundException("Plan not found: " + planId));

        // Plan의 기본 필드 수정
        plan.setTitle(planDTO.getTitle());

        // Places 업데이트
        if (planDTO.getPlace() != null && !planDTO.getPlace().isEmpty()) {
            List<Place> updatedPlaces = planDTO.getPlace().stream()
                    .map(placeDTO -> {
                        Place place = new Place();
                        place.setPlaceId((long) placeDTO.getPlaceId());
                        place.setPlaceName(placeDTO.getPlaceName());
                        place.setPrice(placeDTO.getPrice());
                        place.setAddr(placeDTO.getAddr());
                        place.setStartTime(placeDTO.getStartTime());
                        place.setEndTime(placeDTO.getEndTime());
                        place.setPlan(plan); // Place가 Plan을 참조하도록 설정
                        return place;
                    }).collect(Collectors.toList());

            plan.setPlaces(updatedPlaces); // 새로운 Place 리스트로 교체
        }

        // Routes 업데이트
        if (planDTO.getRoute() != null && !planDTO.getRoute().isEmpty()) {
            List<Route> updatedRoutes = planDTO.getRoute().stream()
                    .map(routeDTO -> {
                        Route route = new Route();
                        // 출발 장소와 도착 장소를 설정
                        Place startPlace = new Place();
                        startPlace.setPlaceId((long) routeDTO.getStartPlaceId());
                        route.setStartPlace(startPlace);

                        Place endPlace = new Place();
                        endPlace.setPlaceId((long) routeDTO.getEndPlaceId());
                        route.setEndPlace(endPlace);

                        // 기타 Route 정보 설정
                        route.setPrice(routeDTO.getPrice());
                        TransportationType transportType = new TransportationType();
                        transportType.setId(routeDTO.getTransport().getId());
                        transportType.setType(routeDTO.getTransport().getType());
                        route.setTransport(transportType);

                        route.setTravelTime(routeDTO.getTravelTime());
                        route.setChecker(routeDTO.isChecker());
                        route.setPlan(plan); // Route가 Plan을 참조하도록 설정
                        return route;
                    }).collect(Collectors.toList());

            plan.setRoutes(updatedRoutes); // 새로운 Route 리스트로 교체
        }

        // Themes 업데이트
        if (planDTO.getThemes() != null && !planDTO.getThemes().isEmpty()) {
            List<Theme> updatedThemes = planDTO.getThemes().stream()
                    .map(themeDTO -> {
                        Theme theme = new Theme();
                        theme.setId(themeDTO.getId());
                        return theme;
                    }).collect(Collectors.toList());

            plan.setThemes(updatedThemes); // 새로운 Theme 리스트로 교체
        }

        // 수정된 Plan 엔티티를 저장
        planRepository.save(plan);
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