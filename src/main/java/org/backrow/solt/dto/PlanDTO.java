package org.backrow.solt.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PlanDTO {

    private int planId;
    private String title;
    private boolean confirm;
    private MemberDTO member;
    private List<PlaceDTO> place;
    private List<RouteDTO> route;
    private LocalDateTime regDate;
    private LocalDateTime modDate;
    private List<ThemeDTO> themes;
}
