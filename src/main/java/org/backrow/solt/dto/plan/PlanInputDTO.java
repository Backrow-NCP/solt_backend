package org.backrow.solt.dto.plan;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class PlanInputDTO {
    @NotEmpty(message = "일정명은 반드시 존재해야 합니다.")
    @Size(min = 1, max = 200, message = "일정명은 1~200자 이내의 문자열이어야 합니다.")
    private String title;

//    private Boolean confirm;

//    @NotNull(message = "멤버 ID는 반드시 존재해야 합니다.")
    private Long memberId;

    private Set<PlaceDTO> places = new HashSet<>();

    private Set<RouteDTO> routes = new HashSet<>();

    @JsonDeserialize(as = HashSet.class)
    private Set<Long> themes = new HashSet<>();

    @NotEmpty(message = "여행 장소는 반드시 존재해야 합니다.")
    private String location;

    @NotNull(message = "시작 날짜는 반드시 존재해야 합니다.")
    private LocalDate startDate;

    @NotNull(message = "종료 날짜는 반드시 존재해야 합니다.")
    private LocalDate endDate;
}
