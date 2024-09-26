package org.backrow.solt.dto.plan;

import lombok.*;

@Getter
@Setter
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