package org.backrow.solt.dto.like;

import lombok.Getter;
import org.backrow.solt.domain.Board;
import org.backrow.solt.domain.LikeLog;
import org.backrow.solt.domain.Member;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
public class LikeDTO {
    @NotNull(message = "게시글 ID는 반드시 존재해야 합니다.")
    @Min(value = 1, message = "게시글 ID는 1 이상의 정수여야 합니다.")
    private Long boardId;

    @NotNull(message = "멤버 ID는 반드시 존재해야 합니다.")
    @Min(value = 1, message = "멤버 ID는 1 이상의 정수여야 합니다.")
    private Long memberId;

    public LikeLog convertToEntity() {
        Board board = new Board();
        board.setBoardId(this.boardId);
        Member member = Member.builder().memberId(this.memberId).build();

        return LikeLog.builder()
                .board(board)
                .member(member)
                .build();
    }
}