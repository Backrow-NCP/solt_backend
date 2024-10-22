package org.backrow.solt.domain.personality;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Answer {
    @Id
    private int answerId;

    @ManyToOne
    @JoinColumn(name="question_id")
    private Question question;

    @ManyToOne
    @JoinColumn(name="type_id")
    private AnswerType answerType;

    private String content;

    private int score;
}
