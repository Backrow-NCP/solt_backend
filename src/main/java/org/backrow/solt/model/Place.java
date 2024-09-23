package org.backrow.solt.model;

import javax.persistence.Entity;
import java.time.LocalDateTime;
import java.util.List;

@Entity
public class Place {

    private Long poinId;
    private Plan plan;
    private String placeName;
    private int price;
    private String addr;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

}
