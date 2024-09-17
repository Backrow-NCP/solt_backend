package org.backrow.solt.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.backrow.solt.domain.LikeLog;
import org.backrow.solt.domain.serialize.LikeLogId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;

public interface LikeLogRepository extends JpaRepository<LikeLog, LikeLogId> {
    int countByBoardBoardId(Long boardId);

    @Modifying
    @Transactional
    @Query("DELETE FROM LikeLog l WHERE l.board.boardId = :boardId AND l.member.memberId = :memberId")
    int deleteByBoardIdAndMemberId(@Param("boardId") Long boardId, @Param("memberId") Long memberId);
}
