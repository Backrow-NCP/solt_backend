package org.backrow.solt.dto.plan;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanDTO {

    private int planId;

    private String title;

    private LocalDateTime regDate;
    private LocalDateTime modDate;

    private boolean confirm;

    //private MemberDTO member;

    private List<PlaceDTO> place;
    private List<RouteDTO> route;
    private List<Theme> themes;

}
