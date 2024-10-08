package org.backrow.solt.dto.plan;

import lombok.*;
import org.backrow.solt.dto.member.MemberInfoDTO;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlanViewDTO {
    private Long planId;
    private String title;
//    private Boolean confirm;
    private MemberInfoDTO member;
    private Set<PlaceDTO> places;
    private Set<RouteDTO> routes;
    private Set<ThemeDTO> themes;
    private LocalDateTime regDate;
    private LocalDateTime modDate;
}