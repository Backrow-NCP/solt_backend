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

        // 추가적인 수작업이 필요한 매핑 처리
        if (plan.getRoutes() != null) {
            planDTO.setRoute(
                    plan.getRoutes().stream()
                            .map(route -> new RouteDTO(
                                    route.getStartPlace().getPlaceId(),
                                    route.getEndPlace().getPlaceId(),
                                    route.getPrice(),
                                    new TransportationTypeDTO(route.getTransport().getId(), route.getTransport().getType()),
                                    false,  // 예시로 false 지정
                                    0                 // 예시로 0 지정
                            ))
                            .collect(Collectors.toList())
            );
        }

        return planDTO;
    }

    public Plan convertToEntity(PlanDTO planDTO) {
        // ModelMapper를 사용하여 PlanDTO를 Plan 엔티티로 자동 변환
        Plan plan = modelMapper.map(planDTO, Plan.class);

        // 추가적인 수작업이 필요한 매핑 처리
        if (planDTO.getRoute() != null) {
            List<Route> routes = planDTO.getRoute().stream()
                    .map(routeDTO -> {
                        Route route = new Route();
                        Place startPlace = new Place();
                        startPlace.setPlaceId(routeDTO.getStartPlaceId());
                        route.setStartPlace(startPlace);

                        Place endPlace = new Place();
                        endPlace.setPlaceId(routeDTO.getEndPlaceId());
                        route.setEndPlace(endPlace);

                        route.setPrice(routeDTO.getPrice());

                        TransportationType transportationType = new TransportationType();
                        transportationType.setId(routeDTO.getTransport().getId());
                        transportationType.setType(routeDTO.getTransport().getType());
                        route.setTransport(transportationType);

                        route.setPlan(plan); // Route가 Plan을 참조
                        return route;
                    }).collect(Collectors.toList());
            plan.setRoutes(routes);
        }

        return plan;
    }
}

