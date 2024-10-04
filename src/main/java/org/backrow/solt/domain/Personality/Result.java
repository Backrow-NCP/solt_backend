package org.backrow.solt.domain.Personality;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Result {
    @Id
    private int resultId;

    @ManyToOne
    @JoinColumn
    private Survey survey;

    private String surveyResult;

    private String summary;

    @Column(length = 2048)
    private String description;

    @Column(length = 2048)
    private String image;
}
