package org.backrow.solt.service;

import org.backrow.solt.dto.reply.ReplyDTO;
import org.backrow.solt.dto.page.PageRequestDTO;
import org.backrow.solt.dto.page.PageResponseDTO;

public interface ReplyService {
    PageResponseDTO<ReplyDTO> getRepliesByBoardId(Long id, PageRequestDTO pageRequestDTO);
    long saveReply(ReplyDTO boardDTO);
    boolean modifyReply(Long id, ReplyDTO boardDTO);
    boolean deleteReply(Long id);
}
