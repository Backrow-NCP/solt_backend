package org.backrow.solt.repository.search;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQuery;
import org.backrow.solt.domain.Board;
import org.backrow.solt.domain.QBoard;
import org.backrow.solt.domain.QLikeLog;
import org.backrow.solt.dto.board.BoardViewDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

public class BoardSearchImpl extends QuerydslRepositorySupport implements BoardSearch {
    public BoardSearchImpl() {
        super(Board.class);
    }

    @Override
    public Page<BoardViewDTO> searchBoardView(String[] types, String keyword, Pageable pageable) {
        QBoard board = QBoard.board;
        QLikeLog likeLog = QLikeLog.likeLog;

        JPQLQuery<Board> boardQuery = from(board)
                .leftJoin(likeLog).on(likeLog.board.eq(board))
                .groupBy(board);

        for (String type : types) {
            BooleanBuilder builder = new BooleanBuilder();
            switch (type) {
                case "t":
                    builder.or(titleContatin(keyword));
                    break;
                case "c":
                    builder.or(contentContain(keyword));
                    break;
                case "w":
                    builder.or(writerContain(keyword));
                    break;
            }
            boardQuery.where(builder);
        }

        JPQLQuery<BoardViewDTO> boardViewQuery = boardQuery.select(createBoardViewDTOProjection(board, likeLog));

        this.getQuerydsl().applyPagination(pageable, boardViewQuery);
        List<BoardViewDTO> list = boardViewQuery.fetch();
        long listCount = list.size();

        return new PageImpl<>(list, pageable, listCount);
    }

    @Override
    public BoardViewDTO searchBoardView(Long boardId) {
        QBoard board = QBoard.board;
        QLikeLog likeLog = QLikeLog.likeLog;

        JPQLQuery<BoardViewDTO> boardViewQuery = from(board)
                .leftJoin(likeLog).on(likeLog.board.eq(board))
                .where(board.boardId.eq(boardId))
                .groupBy(board)
                .select(createBoardViewDTOProjection(board, likeLog));

        return boardViewQuery.fetchOne();
    }

    private BooleanExpression titleContatin(String keyword) {
        return keyword != null ? QBoard.board.title.containsIgnoreCase(keyword) : null;
    }

    private BooleanExpression contentContain(String keyword) {
        return keyword != null ? QBoard.board.content.containsIgnoreCase(keyword) : null;
    }

    private BooleanExpression writerContain(String keyword) {
        return keyword != null ? QBoard.board.member.name.containsIgnoreCase(keyword) : null;
    }

    private ConstructorExpression<BoardViewDTO> createBoardViewDTOProjection(QBoard board, QLikeLog likeLog) {
        return Projections.constructor(BoardViewDTO.class,
                board.boardId,
                board.title,
                board.content,
                likeLog.count().intValue().coalesce(0).as("likeCount"),
                board.regDate,
                board.modDate);
    }
}
