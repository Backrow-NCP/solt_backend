package org.backrow.solt.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "routes")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int placeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "start_place_id", nullable = false)
    private Place startPlace;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "end_place_id", nullable = false)
    private Place endPlace;

    @Column(nullable = false)
    private Integer price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transportation_id", nullable = false)
    private TransportationType transport;

    @ManyToOne
    @JoinColumn(name = "plan_id")
    private Plan plan;
}
