package org.backrow.solt.dto.personality;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PersonalityTestLogInputDTO {
    private Long memberId;
    private int resultId;
    private int personalityTestId;
    private LocalDateTime regDate;
}
