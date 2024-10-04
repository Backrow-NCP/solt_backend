package org.backrow.solt.domain.Personality;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Survey {

    @Id
    private int surveyId;

    private String name ;
}
