package org.backrow.solt.service.board;

import lombok.RequiredArgsConstructor;
import org.backrow.solt.domain.board.Board;
import org.backrow.solt.domain.board.LikeLog;
import org.backrow.solt.domain.member.Member;
import org.backrow.solt.dto.like.LikeDTO;
import org.backrow.solt.repository.board.LikeLogRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {
    private final LikeLogRepository likeLogRepository;

    @Override
    public int getLikesByBoardId(Long id) {
        return likeLogRepository.countByBoardBoardId(id);
    }

    @Override
    public int toggleLike(LikeDTO likeDTO) {
        int deletedCount = likeLogRepository.deleteByBoardIdAndMemberId(likeDTO.getBoardId(), likeDTO.getMemberId());
        if (deletedCount == 0) {
            LikeLog likeLog = convertToEntity(likeDTO);
            likeLogRepository.save(likeLog);
        }
        return likeLogRepository.countByBoardBoardId(likeDTO.getBoardId());
    }

    @Override
    public boolean isLiked(LikeDTO likeDTO) {
        return likeLogRepository.existsByBoard_BoardIdAndMember_MemberId(likeDTO.getBoardId(), likeDTO.getMemberId());
    }

    public LikeLog convertToEntity(LikeDTO likeDTO) {
        Board board = new Board();
        board.setBoardId(likeDTO.getBoardId());
        Member member = Member.builder().memberId(likeDTO.getMemberId()).build();

        return LikeLog.builder()
                .board(board)
                .member(member)
                .build();
    }
}
