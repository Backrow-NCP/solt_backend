package org.backrow.solt.domain.personality;

import lombok.*;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

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

//    @OneToMany(mappedBy="answer")
//    private List<AnswerType> answerTypes;

    @ManyToOne
    @JoinColumn(name="type_id")
    private AnswerType answerType;

    private String content;

    private int score;
}
