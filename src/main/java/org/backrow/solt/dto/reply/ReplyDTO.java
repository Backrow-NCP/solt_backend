package org.backrow.solt.dto.reply;

import com.fasterxml.jackson.annotation.JsonFormat;
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

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime regDate;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime modDate;
}
