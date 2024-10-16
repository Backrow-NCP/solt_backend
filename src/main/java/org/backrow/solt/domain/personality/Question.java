package org.backrow.solt.domain.personality;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
import org.backrow.solt.domain.BoardImage;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
