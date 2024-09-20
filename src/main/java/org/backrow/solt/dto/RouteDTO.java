package org.backrow.solt.dto;

import lombok.Data;

@Data
public class RouteDTO {

    private int startPlaceId;
    private int endPlaceId;
    private int price;
    private int transportId;
    private boolean checker;
    private int travelTime;
}