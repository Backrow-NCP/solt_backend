package org.backrow.solt.repository;

import org.backrow.solt.domain.Reply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReplyRepository extends JpaRepository<Reply, Long> {
    Page<Reply> findByBoardBoardId(Long boardId, Pageable pageable);
}
