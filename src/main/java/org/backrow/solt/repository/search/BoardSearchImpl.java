package org.backrow.solt.repository.search;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQuery;
import org.backrow.solt.domain.*;
import org.backrow.solt.dto.board.BoardImageDTO;
import org.backrow.solt.dto.board.BoardViewDTO;
import org.backrow.solt.dto.member.MemberInfoDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;
import java.util.stream.Collectors;

public class BoardSearchImpl extends QuerydslRepositorySupport implements BoardSearch {
    public BoardSearchImpl() {
        super(Board.class);
    }

    @Override
    public Page<BoardViewDTO> searchBoardView(String[] types, String keyword, Pageable pageable) {
        QBoard board = QBoard.board;
        QLikeLog likeLog = QLikeLog.likeLog;
        QMember member = QMember.member;

        JPQLQuery<Board> boardQuery = from(board)
                .leftJoin(member).on(board.member.eq(member))
                .leftJoin(likeLog).on(likeLog.board.eq(board))
                .groupBy(board);

        if (types != null) {
            for (String type : types) {
                BooleanBuilder builder = new BooleanBuilder();
                switch (type) {
                    case "t":
                        builder.or(titleContain(keyword));
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
        }

        this.getQuerydsl().applyPagination(pageable, boardQuery);
        List<Board> boardEntities = boardQuery.fetch();
        List<BoardViewDTO> boardViewDTOS = boardEntities.stream()
                .map(boardEntity -> createBoardViewDTO(boardEntity, false))
                .collect(Collectors.toList());
        long listCount = boardViewDTOS.size();

        return new PageImpl<>(boardViewDTOS, pageable, listCount);
    }

    @Override
    public BoardViewDTO searchBoardView(Long boardId) {
        QBoard board = QBoard.board;
        QLikeLog likeLog = QLikeLog.likeLog;
        QMember member = QMember.member;
        QBoardImage boardImage = QBoardImage.boardImage;

        JPQLQuery<Board> boardQuery = from(board)
                .leftJoin(member).on(board.member.eq(member))
                .leftJoin(boardImage).on(boardImage.board.eq(board))
                .leftJoin(likeLog).on(likeLog.board.eq(board))
                .where(board.boardId.eq(boardId))
                .groupBy(board);

        Board boardEntity = boardQuery.fetchOne();

        return createBoardViewDTO(boardEntity, true);
    }

    private BooleanExpression titleContain(String keyword) {
        return keyword != null ? QBoard.board.title.containsIgnoreCase(keyword) : null;
    }

    private BooleanExpression contentContain(String keyword) {
        return keyword != null ? QBoard.board.content.containsIgnoreCase(keyword) : null;
    }

    private BooleanExpression writerContain(String keyword) {
        return keyword != null ? QBoard.board.member.name.containsIgnoreCase(keyword) : null;
    }

    private BoardViewDTO createBoardViewDTO(Board board, boolean includeImage) {
        BoardViewDTO.BoardViewDTOBuilder builder = BoardViewDTO.builder();

        MemberInfoDTO memberInfoDTO = MemberInfoDTO.builder()
                .memberId(board.getMember().getMemberId())
                .name(board.getMember().getName())
                .build();

        if (includeImage) {
            List<BoardImageDTO> boardImageDTOS = board.getBoardImages().stream()
                    .map(boardImageEntity -> BoardImageDTO.builder()
                            .uuid(boardImageEntity.getUuid())
                            .fileName(boardImageEntity.getFileName())
                            .ord(boardImageEntity.getOrd())
                            .build())
                    .collect(Collectors.toList());
            builder.images(boardImageDTOS);
        }

        int likeCount = board.getLikeLog() != null ? board.getLikeLog().size() : 0;

        return builder
                .boardId(board.getBoardId())
                .title(board.getTitle())
                .content(board.getContent())
                .member(memberInfoDTO)
                .likeCount(likeCount)
                .regDate(board.getRegDate())
                .modDate(board.getModDate())
                .build();
    }
}
