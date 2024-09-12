package org.backrow.solt.service;

import lombok.RequiredArgsConstructor;
import org.backrow.solt.domain.Board;
import org.backrow.solt.domain.LikeLog;
import org.backrow.solt.dto.like.LikeDTO;
import org.backrow.solt.repository.LikeLogRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Optional;

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
        Optional<LikeLog> likeLogOptional = likeLogRepository.findByBoardBoardIdAndMemberMemberId(likeDTO.getBoardId(), likeDTO.getMemberId());

        if (likeLogOptional.isPresent()) {
            likeLogRepository.delete(likeLogOptional.get());
        } else {
            LikeLog likeLog = likeDTO.convertToEntity();
            likeLogRepository.save(likeLog);
        }

        return likeLogRepository.countByBoardBoardId(likeDTO.getBoardId());
    }
}
