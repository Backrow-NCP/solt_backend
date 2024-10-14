package org.backrow.solt.domain.board;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Getter
@Setter
@ToString(exclude = "board")
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
