package org.backrow.solt.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RouteDTO {

    private int startPlaceId;
    private int endPlaceId;

    private int price;

    private TransportationTypeDTO transportationType;
    private int travelTime;

    private boolean checker; //AI 판단 여부

}