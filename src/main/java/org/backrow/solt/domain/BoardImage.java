package org.backrow.solt.domain;

import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
public class BoardImage {
    @Id
    private String uuid;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private int ord;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;
}
