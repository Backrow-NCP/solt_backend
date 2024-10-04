package org.backrow.solt.domain.Personality;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SurveyLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int logId;

//    @ManyToOne
//    @JoinColumn
//    private Member member;

    @ManyToOne
    @JoinColumn
    private Result result;

    @CreatedDate
    @Column(name = "regdate", updatable = false)
    private LocalDateTime regDate;
}
