package org.backrow.solt.dto.plan;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransportationDTO {
    private Integer id;
    private String type;
}
