package org.backrow.solt.domain;

import lombok.*;
import org.backrow.solt.domain.serialize.LikeLogId;

import javax.persistence.*;

@Getter
@Builder
@Entity
@IdClass(LikeLogId.class)
@NoArgsConstructor
@AllArgsConstructor
public class LikeLog {
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;
}
