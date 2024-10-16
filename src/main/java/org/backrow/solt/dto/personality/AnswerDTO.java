package org.backrow.solt.dto.personality;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AnswerDTO {
    private int answerId;
    private String content;
    private int score;
    private AnswerTypeDTO answerTypes;

}
