package org.backrow.solt.dto.personality;

import lombok.*;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PersonalityTestDTO {
    private int personalityTestId;
    private String name;
    private List<QuestionDTO> questions;
}