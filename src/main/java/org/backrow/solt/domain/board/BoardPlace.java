package org.backrow.solt.domain.board;

import lombok.*;
import org.backrow.solt.domain.plan.Plan;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "boardPlan")
public class BoardPlace {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long placeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private BoardPlan boardPlan;

    @Column(nullable = false)
    private String placeName;

    @Column(nullable = false)
    @Builder.Default
    private int price = 0;

    @Column(nullable = false)
    private String addr;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;
}
