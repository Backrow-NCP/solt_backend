package org.backrow.solt.service.plan;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.backrow.solt.domain.member.Member;
import org.backrow.solt.domain.plan.*;
import org.backrow.solt.domain.plan.api.ClovaApiResponse;
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
        log.info("Received PlanInputDTO: " + planInputDTO);

        // Clova API 호출하여 추천 장소 정보 가져오기
        List<PlacesResponses> clovaPlaces = clovaApiService.callClovaApi(planInputDTO); // 수정된 부분
        log.info("Clova API response: " + clovaPlaces);

        // 응답에서 추천 장소 추출 및 변환
        List<PlaceDTO> recommendedPlaces = clovaPlaces.stream()
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

        log.info("Recommended Places: " + recommendedPlaces);

        // 입력된 places 데이터를 가져와서 리스트로 변환
        List<PlaceDTO> placeList = new ArrayList<>(planInputDTO.getPlaces());

        // 중복된 장소를 제거한 상태로 병합
        Set<PlaceDTO> mergedPlaces = new HashSet<>(placeList);
        mergedPlaces.addAll(recommendedPlaces); // 중복된 장소 제거

        // Set<Long>을 Set<ThemeDTO>로 변환하는 로직 추가
        Set<ThemeDTO> themeSet = planInputDTO.getThemes().stream()
                .map(themeId -> ThemeDTO.builder()
                        .themeId(themeId)
                        .build())
                .collect(Collectors.toSet());

        // PlanViewDTO 생성 시 기본 값 설정
        PlanViewDTO planViewDTO = PlanViewDTO.builder()
                .title(planInputDTO.getTitle())
                .member(MemberInfoDTO.builder()
                        .memberId(planInputDTO.getMemberId()) // 실제 Member 정보를 설정
                        .build())
                .places(mergedPlaces) // 병합된 장소 리스트를 Set으로 변환하여 전달
                .themes(themeSet) // 변환된 테마 정보
                .location(planInputDTO.getLocation())
                .startDate(planInputDTO.getStartDate())
                .endDate(planInputDTO.getEndDate())
                .regDate(LocalDateTime.now())
                .modDate(LocalDateTime.now())
                .build();

        log.info("Generated PlanViewDTO: " + planViewDTO);

        // 경로 추천
        // PlanViewDTO의 places를 리스트로 변환 후 startTime 기준으로 정렬
        List<PlaceDTO> sortedPlaces = new ArrayList<>(planViewDTO.getPlaces());
        sortedPlaces.sort(Comparator.comparing(PlaceDTO::getStartTime));

        // 숙소 및 공항 필터링
        PlaceDTO accommodation = sortedPlaces.stream()
                .filter(place -> place.getPlaceName().contains("숙소"))
                .findFirst()
                .orElse(null);
        log.info("Accomdation : " + accommodation);

        PlaceDTO airport = sortedPlaces.stream()
                .filter(place -> place.getPlaceName().contains("공항"))
                .findFirst()
                .orElse(null);
        log.info("Airport : " + airport);

        // 일반 장소는 숙소나 공항이 아닌 것들로 필터링
        List<PlaceDTO> normalPlaces = sortedPlaces.stream()
                .filter(place -> !place.getPlaceName().contains("숙소") && !place.getPlaceName().contains("공항"))
                .collect(Collectors.toList());
        log.info("NormalPlaces : " + normalPlaces);

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
        log.info("calculatedRoutes: " + calculatedRoutes);

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