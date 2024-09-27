package org.backrow.solt.service;

import org.backrow.solt.dto.like.LikeDTO;

public interface LikeService {
    int getLikesByBoardId(Long id);
    int toggleLike(LikeDTO likeDTO);
}
