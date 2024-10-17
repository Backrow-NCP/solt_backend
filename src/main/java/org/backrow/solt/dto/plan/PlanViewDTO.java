package org.backrow.solt.dto.plan;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.backrow.solt.dto.member.MemberInfoDTO;

import java.time.LocalDate;
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

    private String location;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime regDate;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime modDate;
}