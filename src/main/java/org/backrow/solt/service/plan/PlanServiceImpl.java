package org.backrow.solt.service.plan;

import lombok.RequiredArgsConstructor;
import org.backrow.solt.domain.member.Member;
import org.backrow.solt.domain.plan.*;
import org.backrow.solt.dto.page.PageRequestDTO;
import org.backrow.solt.dto.page.PageResponseDTO;
import org.backrow.solt.dto.plan.PlanInputDTO;
import org.backrow.solt.dto.plan.PlanViewDTO;
import org.backrow.solt.repository.plan.ThemeLogRepository;
import org.backrow.solt.repository.plan.PlanRepository;
import org.modelmapper.ModelMapper;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.webjars.NotFoundException;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlanServiceImpl implements PlanService {
    private final PlanRepository planRepository;
    private final ThemeLogRepository themeLogRepository;
    private final ModelMapper modelMapper;

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

    @Transactional
    @Override
    public long savePlan(PlanInputDTO planInputDTO) {
        Plan plan = convertToEntity(planInputDTO);
        assignPlanToEntities(plan);
        planRepository.save(plan);

        Set<ThemeLog> themeLogs = planInputDTO.getThemes().stream()
                        .map(themeId -> ThemeLog.builder()
                                .theme(Theme.builder()
                                        .themeId(themeId).build())
                                .plan(plan).build())
                        .collect(Collectors.toSet());
        themeLogRepository.saveAll(themeLogs);

        return plan.getPlanId();
    }

    @Transactional
    @Override
    public boolean modifyPlan(long planId, PlanInputDTO planInputDTO, long memberId) {
        Optional<Plan> findPlan = planRepository.findById(planId);
        Plan plan = findPlan.orElseThrow(() -> new NotFoundException("Plan not found: " + planId));
        if (!Objects.equals(plan.getMember().getMemberId(), memberId))
            throw new AccessDeniedException("You do not have permission to modify this plan.");

        Set<Place> places = mapToEntitySet(planInputDTO.getPlaces(), Place.class);
        Set<Route> routes = mapToEntitySet(planInputDTO.getRoutes(), Route.class);

        plan.modify(
                planInputDTO.getTitle(),
                places,
                routes,
                planInputDTO.getStartDate(),
                planInputDTO.getEndDate());
        planRepository.save(plan);
        return true;
    }

    @Transactional
    @Override
    public boolean deletePlan(long planId, long memberId) {
        try {
            planRepository.deleteByPlanIdAndMember_MemberId(planId, memberId);
            return true;
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Plan not found: " + planId);
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
    
//    private void check(PlanInputDTO planInputDTO) {
//        // 1. 기존에 사용자가 입력한 장소와 checker 값 확인
//        Set<PlaceDTO> userInputPlaces = planInputDTO.getPlaces();
//
//        // 2. AI가 변경 가능한 장소만 따로 리스트로 저장 (checker가 false인 경우만)
//        List<PlaceDTO> modifiablePlaces = userInputPlaces.stream()
//                .filter(placeDTO -> !placeDTO.getChecker()) // AI가 변경할 수 있는 곳만 필터링
//                .collect(Collectors.toList());
//
//        // 3. Clova AI를 통해 장소 추천 받기 (AI가 변경 가능한 장소만)
//        List<PlaceDTO> recommendedPlaces = planAiService.getRecommendedPlaces(
//                planInputDTO.getLocation(), planInputDTO.getTheme(), modifiablePlaces);
//
//
//        // 4. Place 및 Route 데이터를 처리하고 변환
//        Set<Place> places = new HashSet<>();
//        Set<Route> routes = new HashSet<>();
//
//        List<PlaceDTO> finalPlaces = new ArrayList<>(userInputPlaces); // 기본적으로 사용자가 입력한 값을 사용
//        // AI가 추천한 값이 있으면, 이를 checker가 false인 값만 교체
//        for (int i = 0; i < recommendedPlaces.size(); i++) {
//            PlaceDTO recommendedPlace = recommendedPlaces.get(i);
//            if (!finalPlaces.get(i).getChecker()) {
//                finalPlaces.set(i, recommendedPlace); // checker가 false인 경우 AI가 추천한 장소로 교체
//            }
//        }
//
//        // 변환된 장소 리스트로 엔티티 생성
//        for (int i = 0; i < finalPlaces.size(); i++) {
//            PlaceDTO placeDTO = finalPlaces.get(i);
//            Place place = modelMapper.map(placeDTO, Place.class);
//            places.add(place);
//
//            // 경로 생성 (첫 번째 장소는 경로 생성 안함)
//            if (i < finalPlaces.size() - 1) {
//                PlaceDTO nextPlaceDTO = finalPlaces.get(i + 1);
//                Integer travelTime = mapAPIService.getTravelTime(placeDTO.getAddr(), nextPlaceDTO.getAddr());
//
//                RouteDTO routeDTO = planInputDTO.getRoutes().stream()
//                        .filter(route -> !route.getChecker()) // AI가 수정할 수 있는 경로만 필터링
//                        .findFirst().orElse(null);
//
//                if (routeDTO != null) {
//                    Route route = modelMapper.map(routeDTO, Route.class);
//                    route.setStartPlace(place);
//                    route.setEndPlace(modelMapper.map(nextPlaceDTO, Place.class));
//                    route.setTravelTime(travelTime);
//                    routes.add(route);
//                }
//            }
//    }
}