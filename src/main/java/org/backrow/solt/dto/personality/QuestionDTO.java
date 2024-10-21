package org.backrow.solt.dto.personality;

import lombok.*;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "answers")
public class QuestionDTO {

    private int questionId;
    private String content;
    private String image;

    private List<AnswerDTO> answers;



}
