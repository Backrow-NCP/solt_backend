package org.backrow.solt.repository.board;

import org.backrow.solt.domain.board.Reply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReplyRepository extends JpaRepository<Reply, Long> {
    @EntityGraph(attributePaths = {"member"})
    Page<Reply> findByBoardBoardId(Long boardId, Pageable pageable);

    void deleteByReplyIdAndMember_MemberId(Long replyId, Long memberId);
}
