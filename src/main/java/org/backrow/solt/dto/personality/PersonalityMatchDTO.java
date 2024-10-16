package org.backrow.solt.dto.personality;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PersonalityMatchDTO {
    private int resultId;
    private String seasoning;
    private String image;
}
