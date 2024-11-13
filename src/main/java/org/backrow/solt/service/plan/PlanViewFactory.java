package org.backrow.solt.service.plan;

import org.backrow.solt.dto.plan.*;

import java.time.LocalDateTime;
import java.util.Set;

public class PlanViewFactory {

    public PlanViewDTO createPlanView(PlanInputDTO input, Set<PlaceDTO> places, Set<ThemeDTO> themes, Set<RouteDTO> routes) {
        return PlanViewDTO.builder()
                .title(input.getTitle())
                .places(places)
                .themes(themes)
                .location(input.getLocation())
                .startDate(input.getStartDate())
                .endDate(input.getEndDate())
                .regDate(LocalDateTime.now())
                .modDate(LocalDateTime.now())
                .routes(routes)
                .build();
    }
}
