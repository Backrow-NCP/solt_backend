package org.backrow.solt.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.backrow.solt.dto.plan.PlaceDTO;
import org.backrow.solt.dto.plan.RouteDTO;
import org.backrow.solt.dto.plan.Theme;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Plan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long boardPlanId;

    private String title;

    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL)
    private List<Place> places;

    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL)
    private List<Route> routes;

    @CreatedDate
    @Column(name = "regdate", updatable = false)
    private LocalDateTime regDate;

    @LastModifiedDate
    @Column(name = "moddate")
    private LocalDateTime modDate;

    public void modify(int planId, List<PlaceDTO> place, List<RouteDTO> route, List<Theme> themes) {
    }

}
