package org.backrow.solt.service;

import org.backrow.solt.dto.ReplyDTO;
import org.backrow.solt.dto.PageRequestDTO;
import org.backrow.solt.dto.PageResponseDTO;

public interface ReplyService {
    PageResponseDTO<ReplyDTO> getRepliesByBoardId(Long id, PageRequestDTO pageRequestDTO);
    long saveReply(ReplyDTO boardDTO);
    boolean modifyReply(Long id, ReplyDTO boardDTO);
    boolean deleteReply(Long id);
}
