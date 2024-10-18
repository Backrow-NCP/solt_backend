package org.backrow.solt.domain.board;

import lombok.*;
import org.backrow.solt.domain.plan.Plan;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BoardPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long planId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "origin_plan_id", nullable = false)
    private Plan originPlan;

    @Column(nullable = false)
    private String title;

    @OneToMany(mappedBy = "boardPlan",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    @Builder.Default
    private Set<BoardPlace> places = new HashSet<>();

    @OneToMany(mappedBy = "boardPlan",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    @Builder.Default
    private Set<BoardRoute> routes = new HashSet<>();

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;
}
