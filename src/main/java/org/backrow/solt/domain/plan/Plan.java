package org.backrow.solt.domain.plan;

import lombok.*;
import org.backrow.solt.domain.member.Member;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EntityListeners(value = {AuditingEntityListener.class})
public class Plan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long planId;

    @Column(nullable = false)
    private String title;

    @OneToMany(mappedBy = "plan",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    @Builder.Default
    private Set<Place> places = new HashSet<>();

    @OneToMany(mappedBy = "plan",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    @Builder.Default
    private Set<Route> routes = new HashSet<>();

    @OneToMany(mappedBy = "plan",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    @Builder.Default
    private Set<ThemeLog> themes = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @CreatedDate
    @Column(name = "regdate", updatable = false)
    private LocalDateTime regDate;

    @LastModifiedDate
    @Column(name = "moddate")
    private LocalDateTime modDate;

    public void modify(String title, Set<Place> places, Set<Route> routes, LocalDate startDate, LocalDate endDate) {
        if (title != null) this.title = title;

        if (places != null && !places.isEmpty()) {
            this.places.removeIf(existingPlace -> !places.contains(existingPlace));

            for (Place newPlace : places) {
                if (!this.places.contains(newPlace)) {
                    newPlace.setPlan(this);
                    this.places.add(newPlace);
                }
            }
        }

        if (routes != null && !routes.isEmpty()) {
            this.routes.removeIf(existingRoute -> !routes.contains(existingRoute));

            for (Route newRoute : routes) {
                if (!this.routes.contains(newRoute)) {
                    newRoute.setPlan(this);
                    this.routes.add(newRoute);
                }
            }
        }

        if (startDate != null) this.startDate = startDate;

        if (endDate != null) this.endDate = endDate;
    }
}
