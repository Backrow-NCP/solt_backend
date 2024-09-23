package org.backrow.solt.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Place {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pointId;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id")
    private Plan plan;

    private String placeName;
    private String addr;
    private int price;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

}
