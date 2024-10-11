package org.backrow.solt.repository;

import org.backrow.solt.domain.board.Reply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReplyRepository extends JpaRepository<Reply, Long> {
    @EntityGraph(attributePaths = {"member", "member.profileImage"})
    Page<Reply> findByBoardBoardId(Long boardId, Pageable pageable);
}
