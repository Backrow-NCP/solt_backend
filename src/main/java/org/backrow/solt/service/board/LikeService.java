package org.backrow.solt.service.board;

import org.backrow.solt.dto.like.LikeDTO;

public interface LikeService {
    int getLikesByBoardId(Long id);
    int toggleLike(LikeDTO likeDTO);
    boolean isLiked(LikeDTO likeDTO);
}
