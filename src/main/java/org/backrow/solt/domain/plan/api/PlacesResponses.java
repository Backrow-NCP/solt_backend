package org.backrow.solt.domain.plan.api;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class PlacesResponses {

    private Long placeId;
    private String placeName;
    private String addr;
    private Integer price;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String description;
    private boolean checker;

}