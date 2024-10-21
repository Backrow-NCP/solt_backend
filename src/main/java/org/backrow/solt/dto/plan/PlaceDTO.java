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

    private String description;

    private String category;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    @Builder.Default
    private boolean checker = false; // AI 판단 여부
}