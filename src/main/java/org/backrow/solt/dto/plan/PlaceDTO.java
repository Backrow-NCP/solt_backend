package org.backrow.solt.dto.plan;

import lombok.*;

import java.time.LocalDate;

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
    private LocalDate date;
    private String description;
    private Boolean checker; // AI 판단 여부
}