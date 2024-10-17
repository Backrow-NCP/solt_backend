package org.backrow.solt.dto.plan;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlanOutputDTO {

    @NotNull
    private String title;
    private long memberId;
    private List<PlaceDTO> places;
    private List<Integer> themes;
    private String location;
    private LocalDate startDate;
    private LocalDate endDate;

}
