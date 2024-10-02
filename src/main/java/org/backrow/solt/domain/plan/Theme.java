package org.backrow.solt.domain.plan;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "theme")
public class Theme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
}