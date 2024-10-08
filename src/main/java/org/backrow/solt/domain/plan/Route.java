package org.backrow.solt.domain.plan;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "plan")
public class Route {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long routeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private Plan plan;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Column(nullable = false)
    @Builder.Default
    private int price = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transportation_id", nullable = false)
    private TransportationType transportationType;

    @Column(nullable = false)
    private int distance;

    @Column(nullable = false)
    private int travelTime;
}
