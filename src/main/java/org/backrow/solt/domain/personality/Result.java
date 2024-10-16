package org.backrow.solt.domain.personality;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Result {
    @Id
    private int resultId;

    @ManyToOne
    @JoinColumn(name="personality_test_id")
    private PersonalityTest personalityTest;

    private String result;
    private String seasoning;
    private String explainSeasoning;
    private String summary;

    @Column(length = 2048)
    private String description;

    @Column(length = 2048)
    private String recommendation;

    private String recomSpot1;
    private String recomSpot2;

    private int matchSeasoning;
    private int misMatchSeasoning;

    @Column(length = 2048)
    private String image;
}
