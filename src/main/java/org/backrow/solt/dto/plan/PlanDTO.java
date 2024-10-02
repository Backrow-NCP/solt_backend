package org.backrow.solt.dto.plan;

import lombok.*;
import org.backrow.solt.dto.member.MemberInfoDTO;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanDTO {

    private int planId;

    private String title;

    private LocalDateTime regDate;
    private LocalDateTime modDate;

    private boolean confirm;

    private MemberInfoDTO member;

    private List<PlaceDTO> place;
    private List<RouteDTO> route;
    private List<Theme> themes;

}
