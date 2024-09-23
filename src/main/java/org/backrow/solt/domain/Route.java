package org.backrow.solt.domain;

import lombok.Getter;
import org.springframework.stereotype.Service;

import javax.persistence.*;

@Entity
@Getter
@Service
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "start_place_id")
    private Place startPlace;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "end_place_id")
    private Place endPlace;

    private int price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transportation_id")
    private TransportationType transport;
}
