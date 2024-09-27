package org.backrow.solt.dto.plan;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaceDTO {

    private int placeId;

    private String placeName;
    private String addr;

    private int price;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    
    private boolean checker; //AI 판단 여부

}