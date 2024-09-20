package org.backrow.solt.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class PlanDTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int planId;

    private String title;
    private LocalDateTime regDate;
    private LocalDateTime modDate;

    private boolean confirm;
    private MemberDTO member;

    @OneToMany(cascade = CascadeType.ALL)
    private List<PlaceDTO> place;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "place_id", referencedColumnName = "startPlaceId")
    private RouteDTO route;

    @ManyToMany
    @JoinTable(
            name = "themes",
            joinColumns = @JoinColumn(name = "plan_id"),
            inverseJoinColumns = @JoinColumn(name = "theme_id"))
    private List<ThemeDTO> themes;
}
