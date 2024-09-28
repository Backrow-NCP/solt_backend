package org.backrow.solt.dto.plan;

import lombok.RequiredArgsConstructor;
import org.backrow.solt.domain.Place;
import org.backrow.solt.domain.Plan;
import org.backrow.solt.domain.Route;
import org.backrow.solt.domain.TransportationType;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PlanConvertion {

    private final ModelMapper modelMapper;

    public PlanDTO convertToDTO(Plan plan) {
        // ModelMapper를 사용하여 Plan을 PlanDTO로 자동 변환
        PlanDTO planDTO = modelMapper.map(plan, PlanDTO.class);

        // Route 리스트 변환
        if (plan.getRoutes() != null) {
            List<RouteDTO> routeDTOList = plan.getRoutes().stream()
                    .map(route -> RouteDTO.builder()
                            .startPlaceId(route.getStartPlace().getPlaceId().intValue())
                            .endPlaceId(route.getEndPlace().getPlaceId().intValue())
                            .price(route.getPrice())
                            .transportationType(new TransportationTypeDTO(
                                    route.getTransport().getId(),
                                    route.getTransport().getType()
                            ))
                            .travelTime(0) // 예시로 0 지정
                            .checker(false) // AI 판단 여부 예시로 false 지정
                            .build())
                    .collect(Collectors.toList());
            planDTO.setRoute(routeDTOList);
        }

        return planDTO;
    }

    public Plan convertToEntity(PlanDTO planDTO) {
        Plan plan = modelMapper.map(planDTO, Plan.class);

        // RouteDTO 리스트를 Route 엔티티 리스트로 변환
        if (planDTO.getRoute() != null) {
            List<Route> routeList = planDTO.getRoute().stream()
                    .map(routeDTO -> {
                        Route route = new Route();

                        // startPlace, endPlace는 DB에서 불러와야 함
                        Place startPlace = new Place();
                        startPlace.setPlaceId((long) routeDTO.getStartPlaceId());
                        route.setStartPlace(startPlace);

                        Place endPlace = new Place();
                        endPlace.setPlaceId((long) routeDTO.getEndPlaceId());
                        route.setEndPlace(endPlace);

                        route.setPrice(routeDTO.getPrice());

                        // TransportationTypeDTO에서 TransportationType 엔티티로 변환
                        TransportationType transportationType = new TransportationType();
                        transportationType.setId(routeDTO.getTransportationType().getId());
                        transportationType.setType(routeDTO.getTransportationType().getType());
                        route.setTransport(transportationType);

                        route.setPlan(plan); // Route가 Plan을 참조

                        return route;
                    }).collect(Collectors.toList());
            plan.setRoutes(routeList);
        }

        return plan;
    }
}

