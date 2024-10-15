package org.backrow.solt.dto.plan;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RouteDTO {
    private Long routeId;
//    private LocalDateTime startTime;
//    private LocalDateTime endTime;
    private Integer price;
    private Integer transportationId;
    private Integer distance;
    private Integer travelTime;
    private Boolean checker; // AI 판단 여부
}