package org.backrow.solt.service;

import org.backrow.solt.dto.reply.ReplyDTO;
import org.backrow.solt.dto.page.PageRequestDTO;
import org.backrow.solt.dto.page.PageResponseDTO;
import org.backrow.solt.dto.reply.ReplyInputDTO;

public interface ReplyService {
    PageResponseDTO<ReplyDTO> getRepliesByBoardId(Long id, PageRequestDTO pageRequestDTO);
    long saveReply(ReplyInputDTO replyInputDTO);
    boolean modifyReply(Long id, ReplyInputDTO replyInputDTO);
    boolean deleteReply(Long id);
}
