package org.backrow.solt.dto.reply;

import lombok.Data;
import org.backrow.solt.dto.member.MemberInfoDTO;
import java.time.LocalDateTime;

@Data
public class ReplyDTO {
    private Long replyId;
    private String content;
    private Long boardId;
    private MemberInfoDTO member;
    private Long parentReplyId;
    private LocalDateTime regDate;
    private LocalDateTime modDate;
}
