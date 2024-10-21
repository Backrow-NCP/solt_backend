package org.backrow.solt.dto.personality;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResultAllDTO {
    private int resultId;
    private String result;
    private String seasoning;
    private String image;

}
