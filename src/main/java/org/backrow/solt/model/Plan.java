package org.backrow.solt.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.List;

@Entity
public class Plan {

    @Id
    private Long boardPlanId;
    private String title;

    private List<Place> places;
    private List<Route> routes;
    private LocalDateTime regDate;
    private LocalDateTime modDate;
}
