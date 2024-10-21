package org.backrow.solt.dto.plan;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RouteDTO {
    private Long routeId;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(type = "string", pattern = "yyyy-MM-dd'T'HH:mm:ss", example = "2024-10-21T05:42:37")
    private LocalDateTime startTime;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(type = "string", pattern = "yyyy-MM-dd'T'HH:mm:ss", example = "2024-10-21T05:42:37")
    private LocalDateTime endTime;

    private Integer price;

    private TransportationDTO transportation;

    private Integer distance;

    private Integer travelTime;

    private Boolean checker; // AI 판단 여부
}