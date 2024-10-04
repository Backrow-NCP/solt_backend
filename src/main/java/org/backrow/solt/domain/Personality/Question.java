package org.backrow.solt.domain.Personality;

import lombok.*;

import javax.persistence.*;

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
    @JoinColumn
    private Survey Survey;

    private String content;

    @Column(length = 2048)
    private String image;
}
