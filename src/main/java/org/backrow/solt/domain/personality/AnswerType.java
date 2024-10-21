package org.backrow.solt.domain.personality;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AnswerType {
    @Id
    private int typeId;

    private String name ;

//    @ManyToOne
//    @JoinColumn(name="answer_id")
//    private Answer answer;

    @OneToMany(mappedBy = "answerType")
    private List<Answer> answers;
}
