package org.backrow.solt.dto.plan;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaceDTO {
    private Long placeId;
    private String placeName;
    private String addr;
    private Integer price;
//    private LocalDateTime startTime;
//    private LocalDateTime endTime;
    private Boolean checker; // AI 판단 여부
}