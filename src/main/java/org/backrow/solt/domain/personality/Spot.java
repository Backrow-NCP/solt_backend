package org.backrow.solt.domain.personality;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Spot {
    @Id
    private int spotId;

    @ManyToOne
    @JoinColumn(name="result_id")
    private Result result;

    private String country;

    private String spotName;

    @Column(length = 2048)
    private String image;
}
