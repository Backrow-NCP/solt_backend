package org.backrow.solt.service;

import lombok.RequiredArgsConstructor;
import org.backrow.solt.domain.board.Board;
import org.backrow.solt.domain.Member;
import org.backrow.solt.domain.board.Reply;
import org.backrow.solt.dto.page.PageRequestDTO;
import org.backrow.solt.dto.page.PageResponseDTO;
import org.backrow.solt.dto.reply.ReplyDTO;
import org.backrow.solt.dto.reply.ReplyInputDTO;
import org.backrow.solt.repository.ReplyRepository;
import org.modelmapper.ModelMapper;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.webjars.NotFoundException;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReplyServiceImpl implements ReplyService {
    private final ReplyRepository replyRepository;
    private final ModelMapper modelMapper;

    @Transactional
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
    public long saveReply(ReplyInputDTO replyInputDTO) {
        Reply reply = convertToEntity(replyInputDTO);
        replyRepository.save(reply);
        return reply.getReplyId();
    }

    @Override
    public boolean modifyReply(Long replyId, ReplyInputDTO replyInputDTO, Long memberId) {
        Optional<Reply> findReply = replyRepository.findById(replyId);
        Reply reply = findReply.orElseThrow(() -> new NotFoundException("Reply not found: " + replyId));
        if (!Objects.equals(reply.getMember().getMemberId(), memberId))
            throw new AccessDeniedException("You do not have permission to modify this reply.");

        reply.modify(replyInputDTO.getContent());

        replyRepository.save(reply);
        return true;
    }

    @Override
    public boolean deleteReply(Long replyId, Long memberId) {
        try {
            replyRepository.deleteByReplyIdAndMember_MemberId(replyId, memberId);
            return true;
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Reply not found: " + replyId);
        }
    }

    /** ReplyInputDTO를 Reply Entity로 매핑합니다. **/
    private Reply convertToEntity(ReplyInputDTO replyInputDTO) {
        Board board = Board.builder().boardId(replyInputDTO.getBoardId()).build();
        Member member = Member.builder().memberId(replyInputDTO.getMemberId()).build();
        Reply parentReply = null;
        if (replyInputDTO.getParentReplyId() != null) {
            parentReply = Reply.builder().replyId(replyInputDTO.getParentReplyId()).build();
        }

        return Reply.builder()
                .content(replyInputDTO.getContent())
                .board(board)
                .member(member)
                .parentReply(parentReply)
                .build();
    }
}
