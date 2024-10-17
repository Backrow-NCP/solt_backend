package org.backrow.solt.service.plan;

import lombok.RequiredArgsConstructor;
import org.backrow.solt.domain.Member;
import org.backrow.solt.domain.plan.*;
import org.backrow.solt.service.ai.ClovaApiService;
import org.backrow.solt.dto.page.PageRequestDTO;
import org.backrow.solt.dto.page.PageResponseDTO;
import org.backrow.solt.dto.plan.PlanInputDTO;
import org.backrow.solt.dto.plan.PlanViewDTO;
import org.backrow.solt.repository.ThemeLogRepository;
import org.backrow.solt.repository.PlanRepository;
import org.modelmapper.ModelMapper;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    private final ClovaApiService clovaApiService;

//    private final PlanAiService planAiService; // Clova AI를 활용한 장소 추천 서비스
//    private final MapAPIService mapAPIService; // Google Maps API를 활용한 경로 시간 계산 서비스

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
    public boolean modifyPlan(long id, PlanInputDTO planInputDTO) {
        Optional<Plan> findPlan = planRepository.findById(id);
        Plan plan = findPlan.orElseThrow(() -> new NotFoundException("Plan not found: " + id));

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
//        // 1. Clova API 호출로 장소 추천 받기
//        String clovaRequestBody = ClovaApiService.createClovaRequestBody(planInputDTO);
//        ClovaResponse clovaResponse = clovaApiService.callClovaApi(clovaRequestBody);
//
//        // 2. 각 추천된 장소들에 대해 Google Maps API를 통해 경로 정보 받기
//        List<Place> places = clovaResponse.getPlaces(); // ClovaResponse에서 장소 리스트 가져오기
//        Set<RouteDTO> routes = new HashSet<>();
//
//        for (int i = 0; i < places.size() - 1; i++) {
//            Place startPlace = places.get(i);
//            Place endPlace = places.get(i + 1);
//
//            // Google Maps API를 통해 두 장소 간 경로 정보 조회
//            RoutesResponses googleResponse = googleResponse.getRoutes(startPlace.getAddr(), endPlace.getAddr());
//
//            RouteDTO routeDTO = RouteDTO.builder()
//                    .startPlaceId(startPlace.getPlaceId())
//                    .endPlaceId(endPlace.getPlaceId())
//                    .date(planInputDTO.getStartDate().plusDays(i)) // 날짜는 시작일을 기준으로 하루씩 더함
//                    .distance(googleResponse.getRoutes().get(0).getLegs().get(0).getDistance().getValue() / 1000.0 + " km") // km 단위로 변환
//                    .travelTime(googleResponse.getRoutes().get(0).getLegs().get(0).getDuration().getValue() / 60 + " min") // 분 단위로 변환
//                    .price(0) // 가격 정보가 없는 경우 기본 0으로 설정
//                    .transportationId(0) // 실제 transportationId를 가져오는 방법에 맞게 수정 필요
//                    .build();
//
//            routes.add(routeDTO);
//        }
//
//        // 3. 통합된 PlanOutputDTO를 생성하여 JSON 파일로 반환
//        PlanViewDTO outputDTO = PlanViewDTO.builder()
//                .title(planInputDTO.getTitle())
//                .member(new MemberInfoDTO(planInputDTO.getMemberId(), "이름", Date.valueOf("2024-01-01"), true, "파일이름")) // MemberInfoDTO 생성에 맞게 수정 필요
//                .places(new HashSet<>(places))
//                .routes(routes)
//                .location(planInputDTO.getLocation())
//                .startDate(planInputDTO.getStartDate())
//                .endDate(planInputDTO.getEndDate())
//                .themes(planInputDTO.getThemes())
//                .build();
//
//        return outputDTO;
        return null;
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