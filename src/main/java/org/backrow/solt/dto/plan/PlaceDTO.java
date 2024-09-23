package org.backrow.solt.dto.plan;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaceDTO {

    private int placeId;

    private String placeName;
    private String addr;

    private int planId;
    private int price;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    
    private boolean checker; //AI 판단 여부

}