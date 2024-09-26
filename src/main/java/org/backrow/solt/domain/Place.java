package org.backrow.solt.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "place")
public class Place {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pointId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private Plan plan;

    @Column(nullable = false)
    private String placeName;

    @Column(nullable = false)
    private String addr;

    private int price;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @OneToMany(mappedBy = "startPlace", cascade = CascadeType.ALL)
    private List<Route> startRoutes;

    @OneToMany(mappedBy = "endPlace", cascade = CascadeType.ALL)
    private List<Route> endRoutes;

}
