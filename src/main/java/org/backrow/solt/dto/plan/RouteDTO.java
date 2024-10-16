package org.backrow.solt.dto.plan;

import lombok.*;
import org.backrow.solt.domain.plan.Place;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RouteDTO {
    private Long routeId;
//    private LocalDateTime startTime;
//    private LocalDateTime endTime;
    private Long startPlaceId;
    private Long endPlaceId;
    private LocalDate date;
    private Integer price;
    private Integer transportationId;
    private Integer distance;
    private Integer travelTime;
    private Boolean checker; // AI 판단 여부
}