package org.backrow.solt.service;

import lombok.RequiredArgsConstructor;
import org.backrow.solt.domain.LikeLog;
import org.backrow.solt.dto.like.LikeDTO;
import org.backrow.solt.repository.LikeLogRepository;
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
            LikeLog likeLog = likeDTO.convertToEntity();
            likeLogRepository.save(likeLog);
        }
        return likeLogRepository.countByBoardBoardId(likeDTO.getBoardId());
    }
}
