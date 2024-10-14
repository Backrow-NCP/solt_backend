package org.backrow.solt.service;

import org.backrow.solt.dto.reply.ReplyDTO;
import org.backrow.solt.dto.page.PageRequestDTO;
import org.backrow.solt.dto.page.PageResponseDTO;
import org.backrow.solt.dto.reply.ReplyInputDTO;

public interface ReplyService {
    PageResponseDTO<ReplyDTO> getRepliesByBoardId(Long id, PageRequestDTO pageRequestDTO);
    long saveReply(ReplyInputDTO replyInputDTO);
    boolean modifyReply(Long boardId, ReplyInputDTO replyInputDTO, Long memberId);
    boolean deleteReply(Long boardId, Long memberId);
}
