package org.backrow.solt.domain.board;

import lombok.*;
import org.backrow.solt.domain.member.Member;
import org.backrow.solt.domain.board.serialize.LikeLogId;

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
