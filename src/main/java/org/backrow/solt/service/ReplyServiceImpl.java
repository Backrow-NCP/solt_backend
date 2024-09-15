package org.backrow.solt.service;

import lombok.RequiredArgsConstructor;
import org.backrow.solt.domain.Reply;
import org.backrow.solt.dto.PageRequestDTO;
import org.backrow.solt.dto.PageResponseDTO;
import org.backrow.solt.dto.reply.ReplyDTO;
import org.backrow.solt.repository.ReplyRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReplyServiceImpl implements ReplyService {
    private final ReplyRepository replyRepository;
    private final ModelMapper modelMapper;

    @Override
    public PageResponseDTO<ReplyDTO> getRepliesByBoardId(Long id, PageRequestDTO pageRequestDTO) {
        Pageable pageable = pageRequestDTO.getPageable();
        Page<Reply> replyPage = replyRepository.findByBoardBoardId(id, pageable);

        List<ReplyDTO> dtoList = replyPage.stream()
                .map(reply -> modelMapper.map(reply, ReplyDTO.class))
                .collect(Collectors.toList());

        return new PageResponseDTO<>(pageRequestDTO, dtoList, (int) replyPage.getTotalElements());
    }

    @Override
    public long saveReply(ReplyDTO replyDTO) {
        Reply reply = modelMapper.map(replyDTO, Reply.class);
        replyRepository.save(reply);
        return reply.getReplyId();
    }

    @Override
    public boolean modifyReply(Long id, ReplyDTO replyDTO) {
        Optional<Reply> findReply = replyRepository.findById(id);
        Reply reply = findReply.orElseThrow(() -> new NotFoundException("Reply not found: " + id));

        reply.modify(replyDTO.getContent());

        replyRepository.save(reply);
        return true;
    }

    @Override
    public boolean deleteReply(Long id) {
        if (replyRepository.existsById(id)) {
            replyRepository.deleteById(id);
            return true;
        } else {
            throw new NotFoundException("Board not found: " + id);
        }
    }
}
