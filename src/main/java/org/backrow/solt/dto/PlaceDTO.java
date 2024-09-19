package org.backrow.solt.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PlaceDTO {
    private int placeId;
    private int planId;
    private String placeName;
    private int price;
    private String addr;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private boolean checker;
}