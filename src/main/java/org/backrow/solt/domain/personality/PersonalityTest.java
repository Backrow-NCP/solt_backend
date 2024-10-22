package org.backrow.solt.domain.personality;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PersonalityTest {
    @Id
    private int personalityTestId;

    private String name ;

    @OneToMany(mappedBy = "personalityTest")
    private List<Question> questions;

    @OneToMany(mappedBy = "personalityTest")
    private List<PersonalityTestLog> personalityTestLogs;
}
