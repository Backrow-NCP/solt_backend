package org.backrow.solt.domain.personality;

import lombok.*;
import org.backrow.solt.domain.Member;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PersonalityTestLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long logId;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name="result_id")
    private Result result;

    @ManyToOne
    @JoinColumn(name="personality_test_id")
    private PersonalityTest personalityTest;

    @CreatedDate
    @Column(name = "regdate", updatable = false)
    private LocalDateTime regDate;
}
