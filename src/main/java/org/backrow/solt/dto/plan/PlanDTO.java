package org.backrow.solt.dto.plan;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
