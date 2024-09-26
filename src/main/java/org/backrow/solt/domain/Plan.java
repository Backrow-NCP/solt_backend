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
@Table(name = "plans")
public class Plan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long boardPlanId;

    @Column(nullable = false)
    private String title;

    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL)
    private List<Place> places;

    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL)
    private List<Route> routes;

    private LocalDateTime regDate;
    private LocalDateTime modDate;

}
