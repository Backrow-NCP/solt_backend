package org.backrow.solt.repository;

import org.backrow.solt.domain.LikeLog;
import org.backrow.solt.domain.serialize.LikeLogId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeLogRepository extends JpaRepository<LikeLog, LikeLogId> {
    int countByBoardBoardId(Long boardId);
    Optional<LikeLog> findByBoardBoardIdAndMemberMemberId(Long boardId, Long memberId);
}
