package org.backrow.solt.dto.reply;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class ReplyInputDTO {
    @NotEmpty(message = "본문은 반드시 존재해야 합니다.")
    @Size(min = 1, max = 500, message = "본문은 1~500자 이내의 문자열이어야 합니다.")
    private String content;

    @NotNull(message = "게시글 ID는 반드시 존재해야 합니다.")
    private Long boardId;

    @NotNull(message = "멤버 ID는 반드시 존재해야 합니다.")
    private Long memberId;

    private Long parentReplyId;
}
