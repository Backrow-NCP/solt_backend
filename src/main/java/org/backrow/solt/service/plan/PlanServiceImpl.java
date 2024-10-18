package org.backrow.solt.service.plan;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.backrow.solt.domain.member.Member;
import org.backrow.solt.domain.plan.*;
import org.backrow.solt.domain.plan.api.DirectionsResponses;
import org.backrow.solt.domain.plan.api.PlacesResponses;
import org.backrow.solt.dto.member.MemberInfoDTO;
import org.backrow.solt.dto.plan.*;
import org.backrow.solt.repository.plan.PlanRepository;
import org.backrow.solt.repository.plan.ThemeLogRepository;
import org.backrow.solt.service.ai.ClovaApiService;
import org.backrow.solt.dto.page.PageRequestDTO;
import org.backrow.solt.dto.page.PageResponseDTO;
import org.backrow.solt.service.ai.GoogleMapsApiService;
import org.modelmapper.ModelMapper;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.webjars.NotFoundException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class PlanServiceImpl implements PlanService {
    private final PlanRepository planRepository;
    private final ThemeLogRepository themeLogRepository;
    private final ModelMapper modelMapper;
    private final ClovaApiService clovaApiService;
    private final GoogleMapsApiService googleMapsApiService;

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
        log.info(planInputDTO);

        // Clova API 호출하여 추천 장소 정보 가져오기
        List<PlacesResponses> clovaResponse = clovaApiService.callClovaApi(planInputDTO);
        log.info("Clova API response: " + clovaResponse);

        // Clova API 응답에서 추천 장소를 추출
        List<PlaceDTO> recommendedPlaces = new ArrayList<>();
        if (clovaResponse != null) {
            recommendedPlaces = clovaResponse.stream()
                    .map(response -> PlaceDTO.builder()
                            .placeId(response.getPlaceId())
                            .placeName(response.getPlaceName())
                            .addr(response.getAddr())
                            .price(response.getPrice())
                            .startTime(response.getStartTime())
                            .endTime(response.getEndTime())
                            .description(response.getDescription())
                            .checker(response.isChecker())
                            .build())
                    .collect(Collectors.toList());
        }
        log.info("Recommended Places: " + recommendedPlaces);

        // 입력된 places 데이터를 먼저 가져옴
        Set<PlaceDTO> places = planInputDTO.getPlaces();
        List<PlaceDTO> placeList = new ArrayList<>(places);

        // Clova API로 받은 추천 장소를 기존 장소 리스트에 병합
        placeList.addAll(recommendedPlaces);

        // Set<Long>을 Set<ThemeDTO>로 변환하는 로직 추가
        Set<ThemeDTO> themeSet = planInputDTO.getThemes().stream()
                .map(themeId -> ThemeDTO.builder()
                        .themeId(themeId)
                        .build())
                .collect(Collectors.toSet());

        // PlanViewDTO 생성 시 기본 값 설정
        PlanViewDTO planViewDTO = PlanViewDTO.builder()
                .title(planInputDTO.getTitle())
                .member(MemberInfoDTO.builder().build())
                .places(new HashSet<>(places))
                .themes(themeSet)
                .location(planInputDTO.getLocation())
                .startDate(planInputDTO.getStartDate())
                .endDate(planInputDTO.getEndDate())
                .regDate(LocalDateTime.now())
                .modDate(LocalDateTime.now())
                .build();

        // 숙소와 공항 구분하기
        List<PlaceDTO> accommodations = new ArrayList<>();
        List<PlaceDTO> normalPlaces = new ArrayList<>();
        PlaceDTO airport = null;

        for (PlaceDTO place : placeList) {
            if (place.getPlaceName().contains("공항")) { // 공항 여부를 placeName으로 구분
                airport = place;  // 공항은 따로 저장
            } else if (place.getPlaceName().contains("숙소")) { // 숙소 여부를 placeName으로 구분
                accommodations.add(place);  // 숙소 저장
            } else {
                normalPlaces.add(place);  // 일반 장소는 따로 저장
            }
        }

        // 각 Place의 startTime을 기준으로 정렬
        normalPlaces.sort(Comparator.comparing(PlaceDTO::getStartTime));

        // 날짜별로 정렬된 장소 목록 만들기
        Map<LocalDateTime, List<PlaceDTO>> placesByDate = normalPlaces.stream()
                .collect(Collectors.groupingBy(PlaceDTO::getStartTime));

        List<RouteDTO> calculatedRoutes = new ArrayList<>();

        // 날짜별로 장소 순회
        for (LocalDateTime date : placesByDate.keySet()) {
            List<PlaceDTO> dailyPlaces = placesByDate.get(date);

            // 숙소는 해당 날짜의 마지막 장소로 추가
            if (!accommodations.isEmpty()) {
                PlaceDTO accommodation = accommodations.get(0);  // 해당 날짜의 숙소
                dailyPlaces.add(accommodation);  // 리스트에 숙소를 마지막에 추가
            }

            // 장소 간 경로 계산
            for (int i = 0; i < dailyPlaces.size() - 1; i++) {
                PlaceDTO startPlace = dailyPlaces.get(i);
                PlaceDTO endPlace = dailyPlaces.get(i + 1);

                // 출발지와 도착지의 placeId를 Google Maps API에 전달
                DirectionsResponses directions = googleMapsApiService.getDirections(
                        startPlace.getPlaceName(),
                        endPlace.getPlaceName()
                );

                // API 응답에서 필요한 경로 정보 추출 후 RouteDTO 생성
                RouteDTO route = RouteDTO.builder()
                        .startTime(startPlace.getStartTime())
                        .endTime(endPlace.getEndTime())
                        .distance(directions.getRoutes().get(0).getLegs().get(0).getDistance().getValue())  // 거리 정보
                        .travelTime(directions.getRoutes().get(0).getLegs().get(0).getDuration().getValue()) // 이동 시간
                        .price(0)  // 가격은 0으로 초기화
                        .build();

                calculatedRoutes.add(route);  // 계산된 경로 추가
            }
        }

        // 모든 일정이 끝나고 공항으로 가는 경로 추가
        if (airport != null && !normalPlaces.isEmpty()) {
            PlaceDTO lastPlace = normalPlaces.get(normalPlaces.size() - 1);  // 마지막 일반 장소

            // 마지막 장소에서 공항까지 경로 계산
            DirectionsResponses directions = googleMapsApiService.getDirections(
                    lastPlace.getPlaceName(),
                    airport.getPlaceName()
            );

            RouteDTO airportRoute = RouteDTO.builder()
                    .startTime(lastPlace.getEndTime())  // 마지막 장소의 종료 시간 사용
                    .endTime(airport.getStartTime())     // 공항의 시작 시간 사용
                    .distance(directions.getRoutes().get(0).getLegs().get(0).getDistance().getValue())  // 거리 정보
                    .travelTime(directions.getRoutes().get(0).getLegs().get(0).getDuration().getValue())  // 이동 시간
                    .price(0)  // 가격은 0으로 초기화
                    .build();

            calculatedRoutes.add(airportRoute);  // 공항 경로 추가
        }

        // 계산된 경로를 LinkedHashSet으로 변환해 PlanViewDTO에 설정
        planViewDTO.setRoutes(new LinkedHashSet<>(calculatedRoutes));

        // 정렬된 Place 정보는 이미 Set 형태로 들어가 있으므로 따로 추가 필요 없음
        return planViewDTO;
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