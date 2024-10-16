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
public class Question {
    @Id
    private int questionId;

    @ManyToOne
    @JoinColumn(name="personality_test_id")
    private PersonalityTest personalityTest;

    private String content;

    @Column(length = 2048)
    private String image;

    @OneToMany(mappedBy = "question")
    private List<Answer> answers;

}
