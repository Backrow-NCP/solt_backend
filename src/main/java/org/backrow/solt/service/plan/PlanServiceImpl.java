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

import java.time.LocalDate;
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
    private final ThemeStore themeStore;

    @Override
    public PageResponseDTO<PlanViewDTO> getPlanList(long id, PageRequestDTO pageRequestDTO) { // List 조회 시에는 Plan의 세부 내용은 필요 없지 않을까..?
        String[] types = pageRequestDTO.getTypes();
        String keyword = pageRequestDTO.getKeyword();
        Pageable pageable = pageRequestDTO.getPageable();

        Page<PlanViewDTO> planPage = planRepository.searchPlanViewWithMemberId(types, keyword, pageable, id);

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

        // 장소 추천
        // Clova API 호출하여 추천 장소 정보 가져오기
        List<PlacesResponses> clovaPlaces = clovaApiService.callClovaApi(planInputDTO); // 수정된 부분
        log.info("Clova API response: " + clovaPlaces);

        // 응답에서 추천 장소 추출 및 변환
        List<PlaceDTO> recommendedPlaces = clovaPlaces.stream()
                .map(response -> PlaceDTO.builder()
                        .placeName(response.getPlaceName())
                        .addr(response.getAddr())
                        .price(response.getPrice())
                        .startTime(response.getStartTime())
                        .endTime(response.getEndTime())
                        .description(response.getDescription())
                        .category(response.getCategory())
                        .checker(response.isChecker())
                        .build())
                .collect(Collectors.toList());

        log.info("Recommended Places: " + recommendedPlaces);

        // 입력된 places 데이터를 가져와서 리스트로 변환
        List<PlaceDTO> placeList = new ArrayList<>(planInputDTO.getPlaces());

        // 중복된 장소를 제거한 상태로 병합
        Set<PlaceDTO> mergedPlaces = new HashSet<>(placeList);
        mergedPlaces.addAll(recommendedPlaces); // 중복된 장소 제거

        // 테마 이름을 가져오기 위한 로직
        Set<ThemeDTO> themeSet = planInputDTO.getThemes().stream()
                .map(themeId -> {
                    ThemeDTO themeDTO = themeStore.getThemeById(themeId);
                    return themeDTO != null ? themeDTO : ThemeDTO.builder().themeId(themeId).name("알 수 없는 테마").build(); // 예외 처리
                })
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
        sortedPlaces.removeIf(place -> place.getStartTime() == null); // startTime이 null인 객체 제거
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

        // 날짜별로 장소 그룹화
        Map<LocalDate, List<PlaceDTO>> placesByDate = normalPlaces.stream()
                .collect(Collectors.groupingBy(place -> place.getStartTime().toLocalDate()));

        List<RouteDTO> calculatedRoutes = new ArrayList<>();

        // 날짜별로 장소 순회
        for (Map.Entry<LocalDate, List<PlaceDTO>> entry : placesByDate.entrySet()) {
            List<PlaceDTO> dailyPlaces = entry.getValue();

            // 숙소가 있는 경우 해당 날짜의 마지막에 숙소 추가
            if (accommodation != null) {
                dailyPlaces.add(accommodation);  // 숙소를 마지막에 추가
            }

            // 장소 간 경로 계산
            for (int i = 0; i < dailyPlaces.size() - 1; i++) {
                PlaceDTO startPlace = dailyPlaces.get(i);
                PlaceDTO endPlace = dailyPlaces.get(i + 1);

                // 출발지와 도착지 정보 로그
                log.info("Calculating route between: " + startPlace.getPlaceName() + " (" + startPlace.getAddr() + ") -> "
                        + endPlace.getPlaceName() + " (" + endPlace.getAddr() + ")");

                // Google Maps API를 호출하여 경로 계산
                DirectionsResponses directions = googleMapsApiService.getDirections(
                        startPlace.getAddr(), // 출발지 주소 사용
                        endPlace.getAddr()    // 도착지 주소 사용
                );

                // Google Maps API 응답 로그
                log.info("Google Maps API response for route from " + startPlace.getPlaceName() + " to " + endPlace.getPlaceName() + ": "
                        + directions);

                // 경로 정보로 RouteDTO 생성
                DirectionsResponses.Route.Leg leg = directions.getRoutes().get(0).getLegs().get(0); // 첫 번째 경로의 첫 번째 다리 선택

                // 이동 수단 정보 추출
                TransportationDTO transportation = TransportationDTO.builder()
                        .id(TransportationUtil.getTransportationId(directions.getRoutes().get(0).getLegs())) // 이동 수단 ID 가져오기
                        .type(TransportationUtil.getTransportationType(directions.getRoutes().get(0).getLegs())) // 이동 수단 타입 가져오기
                        .build();

                // 가격 설정: 대중교통이면 3000원, 도보이면 0원
                int price = "대중교통".equals(transportation.getType()) ? 3000 : 0;

                // 경로 정보로 RouteDTO 생성
                RouteDTO route = RouteDTO.builder()
                        .startTime(startPlace.getEndTime()) // 시작 장소의 종료 시간 사용
                        .endTime(endPlace.getStartTime())   // 도착 장소의 시작 시간 사용
                        .distance(leg.getDistance().getValue())
                        .travelTime(leg.getDuration().getValue())
                        .price(price)  // 이동 수단에 따른 가격 설정
                        .transportation(transportation)
                        .checker(true)  // AI가 수정할 수 없는 정보로 설정
                        .build();

                calculatedRoutes.add(route);  // 계산된 경로 추가

                // 경로 계산 결과 로그
                log.info("Calculated route: " + route);
            }
        }

        // 공항이 있는 경우, 마지막 장소에서 공항으로 가는 경로 추가
        if (airport != null && !normalPlaces.isEmpty()) {
            PlaceDTO lastPlace = normalPlaces.get(normalPlaces.size() - 1); // 마지막 일반 장소

            log.info("Calculating route from last place to airport: " + lastPlace.getPlaceName() + " -> " + airport.getPlaceName());

            DirectionsResponses directions = googleMapsApiService.getDirections(
                    lastPlace.getAddr(),
                    airport.getAddr()
            );

            // Google Maps API 응답 로그
            log.info("Google Maps API response for route from " + lastPlace.getPlaceName() + " to airport (" + airport.getPlaceName() + "): "
                    + directions);

            // 경로 정보로 RouteDTO 생성
            DirectionsResponses.Route.Leg leg = directions.getRoutes().get(0).getLegs().get(0); // 첫 번째 경로의 첫 번째 다리 선택

            // 이동 수단 정보 추출
            TransportationDTO airportTransportation = TransportationDTO.builder()
                    .id(TransportationUtil.getTransportationId(directions.getRoutes().get(0).getLegs())) // 이동 수단 ID 가져오기
                    .type(TransportationUtil.getTransportationType(directions.getRoutes().get(0).getLegs())) // 이동 수단 타입 가져오기
                    .build();

            // 가격 설정: 대중교통이면 3000원, 도보이면 0원
            int airportPrice = "대중교통".equals(airportTransportation.getType()) ? 3000 : 0;

            // 공항 경로를 위한 RouteDTO 생성
            RouteDTO airportRoute = RouteDTO.builder()
                    .routeId(0L) // 경로 ID는 0으로 초기 설정
                    .startTime(lastPlace.getEndTime())  // 마지막 장소의 종료 시간 사용
                    .endTime(airport.getStartTime())    // 공항의 시작 시간 사용
                    .distance(leg.getDistance().getValue())  // 거리 정보
                    .travelTime(leg.getDuration().getValue())  // 이동 시간
                    .price(airportPrice)  // 이동 수단에 따른 가격 설정
                    .transportation(airportTransportation)
                    .checker(true)  // AI가 수정할 수 없는 정보로 설정
                    .build();

            calculatedRoutes.add(airportRoute);  // 공항 경로 추가

            // 공항 경로 계산 결과 로그
            log.info("Calculated airport route: " + airportRoute);
        }

        // 계산된 경로를 PlanViewDTO에 설정
        planViewDTO.setRoutes(new LinkedHashSet<>(calculatedRoutes));

        log.info("Final calculated routes: " + calculatedRoutes);

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