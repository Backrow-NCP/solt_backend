package org.backrow.solt.repository.board.search;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQuery;
import org.backrow.solt.domain.member.*;
import org.backrow.solt.domain.board.*;
import org.backrow.solt.domain.plan.*;
import org.backrow.solt.dto.board.BoardImageDTO;
import org.backrow.solt.dto.board.BoardViewDTO;
import org.backrow.solt.dto.member.MemberInfoDTO;
import org.backrow.solt.dto.plan.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

public class BoardSearchImpl extends QuerydslRepositorySupport implements BoardSearch {
    public BoardSearchImpl() {
        super(Board.class);
    }

    private final QBoard board = QBoard.board;
    private final QLikeLog likeLog = QLikeLog.likeLog;
    private final QMember member = QMember.member;
    private final QBoardImage boardImage = QBoardImage.boardImage;
    private final QBoardPlan boardPlan = QBoardPlan.boardPlan;
    private final QBoardPlace boardPlace = QBoardPlace.boardPlace;
    private final QBoardRoute boardRoute = QBoardRoute.boardRoute;
    private final QPlan plan = QPlan.plan;
    private final QThemeLog themeLog = QThemeLog.themeLog;
    private final QTheme theme = QTheme.theme;

    @Override
    @Transactional
    public Page<BoardViewDTO> searchBoardView(String[] types, String keyword, Pageable pageable) {
        JPQLQuery<Board> boardQuery = from(board)
                .leftJoin(board.member, member).fetchJoin()
                .leftJoin(board.likeLog, likeLog).fetchJoin()
                .leftJoin(board.boardImages, boardImage).fetchJoin()
                .where(boardImage.isNull().or(boardImage.ord.eq(0)))
                .groupBy(board);

        if (types != null) {
            BooleanBuilder builder = new BooleanBuilder();
            for (String type : types) {
                builder.or(containsKeyword(type, keyword));
            }
            boardQuery.where(builder);
        }

        long totalCount = boardQuery.fetchCount();
        Objects.requireNonNull(this.getQuerydsl()).applyPagination(pageable, boardQuery);

        List<Board> boardEntities = boardQuery.fetch();
        List<BoardViewDTO> boardViewDTOS = boardEntities.stream()
                .map(this::createBoardViewDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(boardViewDTOS, pageable, totalCount);
    }

    @Override
    @Transactional
    public BoardViewDTO searchBoardView(Long boardId) {
        JPQLQuery<Board> boardQuery = from(board)
                .leftJoin(board.member, member).fetchJoin()
                .leftJoin(board.boardImages, boardImage).fetchJoin()
                .leftJoin(board.likeLog, likeLog).fetchJoin()
                .where(board.boardId.eq(boardId))
                .groupBy(board);

        Board boardEntity = boardQuery.fetchOne();

        return createBoardViewDTO(boardEntity);
    }

    @Override
    @Transactional
    public Page<BoardViewDTO> searchBoardViewWithBoardPlan(String[] types, String keyword, String order, Pageable pageable) {
        JPQLQuery<Tuple> boardQuery = from(board)
                .leftJoin(board.member, member).fetchJoin()
                .leftJoin(board.likeLog, likeLog).fetchJoin()
                .leftJoin(board.boardImages, boardImage).fetchJoin()
                .leftJoin(board.boardPlan, boardPlan).fetchJoin()
                .leftJoin(boardPlan.originPlan, plan).fetchJoin()
                .leftJoin(plan.themes, themeLog).fetchJoin()
                .leftJoin(themeLog.theme, theme).fetchJoin()
                .where(boardImage.isNull().or(boardImage.ord.eq(0)))
                .groupBy(board.boardId)
                .select(board, member, likeLog, boardImage, boardPlan, plan, themeLog, theme);

        if (order == null) {
            boardQuery.orderBy(board.regDate.desc());
        } else if (order.equals("l")) {
            boardQuery.orderBy(board.likeLog.size().desc(), board.regDate.desc());
        }

        if (types != null) {
            BooleanBuilder builder = new BooleanBuilder();
            for (String type : types) {
                builder.or(containsKeyword(type, keyword));
            }
            boardQuery.where(builder);
        }

        long totalCount = boardQuery.fetchCount();
        Objects.requireNonNull(this.getQuerydsl()).applyPagination(pageable, boardQuery);

        List<Tuple> results = boardQuery.fetch();

        List<BoardViewDTO> boardViewDTOS = results.stream()
                .map(tuple -> {
                    Board boardEntity = tuple.get(board);
                    BoardPlan boardPlan = boardEntity.getBoardPlan();

                    Set<ThemeDTO> themeDTOS = createThemeDTOS(boardEntity);
                    BoardViewDTO boardViewDTO = createBoardViewDTO(boardEntity);
                    PlanViewDTO planViewDTO = null;
                    if (boardPlan != null) {
                        planViewDTO = PlanViewDTO.builder()
                                .planId(boardPlan.getPlanId())
                                .title(boardPlan.getTitle())
                                .themes(themeDTOS)
                                .location(boardPlan.getLocation())
                                .startDate(boardPlan.getStartDate())
                                .endDate(boardPlan.getEndDate())
                                .build();
                    }
                    boardViewDTO.setPlan(planViewDTO);
                    return boardViewDTO;
                })
                .collect(Collectors.toList());

        return new PageImpl<>(boardViewDTOS, pageable, totalCount);
    }

    @Override
    @Transactional
    public Page<BoardViewDTO> searchBoardViewByMemberIdWithBoardPlan(Long memberId, String[] types, String keyword, String order, Pageable pageable) {
        JPQLQuery<Tuple> boardQuery = from(board)
                .leftJoin(board.member, member).fetchJoin()
                .leftJoin(board.likeLog, likeLog).fetchJoin()
                .leftJoin(board.boardImages, boardImage).fetchJoin()
                .leftJoin(board.boardPlan, boardPlan).fetchJoin()
                .leftJoin(boardPlan.originPlan, plan).fetchJoin()
                .leftJoin(plan.themes, themeLog).fetchJoin()
                .leftJoin(themeLog.theme, theme).fetchJoin()
                .where((boardImage.isNull()
                        .or(boardImage.ord.eq(0)))
                        .and(member.memberId.eq(memberId)))
                .groupBy(board.boardId)
                .select(board, member, likeLog, boardImage, boardPlan, plan, themeLog, theme);

        if (order == null) {
            boardQuery.orderBy(board.regDate.desc());
        } else if (order.equals("l")) {
            boardQuery.orderBy(board.likeLog.size().desc(), board.regDate.desc());
        }

        if (types != null) {
            BooleanBuilder builder = new BooleanBuilder();
            for (String type : types) {
                builder.or(containsKeyword(type, keyword));
            }
            boardQuery.where(builder);
        }

        long totalCount = boardQuery.fetchCount();
        Objects.requireNonNull(this.getQuerydsl()).applyPagination(pageable, boardQuery);

        List<Tuple> results = boardQuery.fetch();

        List<BoardViewDTO> boardViewDTOS = results.stream()
                .map(tuple -> {
                    Board boardEntity = tuple.get(board);
                    BoardPlan boardPlan = boardEntity.getBoardPlan();

                    Set<ThemeDTO> themeDTOS = createThemeDTOS(boardEntity);
                    BoardViewDTO boardViewDTO = createBoardViewDTO(boardEntity);
                    PlanViewDTO planViewDTO = null;
                    if (boardPlan != null) {
                        planViewDTO = PlanViewDTO.builder()
                                .planId(boardPlan.getPlanId())
                                .title(boardPlan.getTitle())
                                .themes(themeDTOS)
                                .location(boardPlan.getLocation())
                                .startDate(boardPlan.getStartDate())
                                .endDate(boardPlan.getEndDate())
                                .build();
                    }
                    boardViewDTO.setPlan(planViewDTO);
                    return boardViewDTO;
                })
                .collect(Collectors.toList());

        return new PageImpl<>(boardViewDTOS, pageable, totalCount);
    }

    @Transactional
    @Override
    public BoardViewDTO searchBoardViewWithBoardPlan(Long boardId) {
        JPQLQuery<Tuple> boardQuery = from(board)
                .leftJoin(board.member, member).fetchJoin()
                .leftJoin(board.boardImages, boardImage).fetchJoin()
                .leftJoin(board.likeLog, likeLog).fetchJoin()
                .leftJoin(board.boardPlan, boardPlan).fetchJoin()
                .leftJoin(boardPlan.places, boardPlace).fetchJoin()
                .leftJoin(boardPlan.routes, boardRoute).fetchJoin()
                .leftJoin(boardPlan.originPlan, plan).fetchJoin()
                .leftJoin(plan.themes, themeLog).fetchJoin()
                .leftJoin(themeLog.theme, theme).fetchJoin()
                .where(board.boardId.eq(boardId))
                .distinct()
                .select(board, member, boardImage, likeLog, boardPlan, boardPlace, boardRoute, plan, themeLog, theme);

        List<Tuple> results = boardQuery.fetch();

        if (results.isEmpty()) return null;

        Tuple result = results.get(0);
        Board boardEntity = result.get(board);

        Set<ThemeDTO> themeDTOS = createThemeDTOS(boardEntity);

        BoardViewDTO boardViewDTO = createBoardViewDTO(boardEntity);
        PlanViewDTO planViewDTO = createPlanViewDTO(boardEntity.getBoardPlan(), themeDTOS);
        boardViewDTO.setPlan(planViewDTO);

        return boardViewDTO;
    }


    private BooleanExpression containsKeyword(String type, String keyword) {
        if (keyword == null) return null;
        switch (type) {
            case "t": return board.title.containsIgnoreCase(keyword);
            case "c": return board.content.containsIgnoreCase(keyword);
            case "w": return board.member.name.containsIgnoreCase(keyword);
            default: return null;
        }
    }

    private Set<ThemeDTO> createThemeDTOS(Board boardEntity) {
        return Optional.ofNullable(boardEntity.getBoardPlan())
                .map(BoardPlan::getOriginPlan)
                .map(Plan::getThemes)
                .orElse(Collections.emptySet())
                .stream()
                .map(ThemeLog::getTheme)
                .filter(Objects::nonNull)
                .map(themeEntity -> ThemeDTO.builder()
                        .themeId(themeEntity.getThemeId())
                        .name(themeEntity.getName())
                        .build())
                .collect(Collectors.toSet());
    }

    private BoardViewDTO createBoardViewDTO(Board board) {
        if (board == null) return null;

        MemberInfoDTO memberInfoDTO = MemberInfoDTO.builder()
                .memberId(board.getMember().getMemberId())
                .name(board.getMember().getName())
                .build();
        Set<BoardImageDTO> boardImageDTOS = board.getBoardImages().stream()
                .map(boardImageEntity -> BoardImageDTO.builder()
                        .fileName(boardImageEntity.getFileName())
                        .ord(boardImageEntity.getOrd())
                        .build())
                .collect(Collectors.toSet());
        int likeCount = board.getLikeLog() != null ? board.getLikeLog().size() : 0;

        return BoardViewDTO.builder()
                .boardId(board.getBoardId())
                .title(board.getTitle())
                .content(board.getContent())
                .member(memberInfoDTO)
                .images(boardImageDTOS)
                .likeCount(likeCount)
                .regDate(board.getRegDate())
                .modDate(board.getModDate())
                .build();
    }

    private PlanViewDTO createPlanViewDTO(BoardPlan plan, Set<ThemeDTO> themeDTOS) {
        if (plan == null) return null;

        Set<PlaceDTO> placeDTOS = plan.getPlaces().stream()
                .map(placeEntity -> PlaceDTO.builder()
                        .placeId(placeEntity.getPlaceId())
                        .placeName(placeEntity.getPlaceName())
                        .addr(placeEntity.getAddr())
                        .price(placeEntity.getPrice())
                        .description(placeEntity.getDescription())
                        .category(placeEntity.getCategory())
                        .startTime(placeEntity.getStartTime())
                        .endTime(placeEntity.getEndTime())
                        .build())
                .collect(Collectors.toSet());
        Set<RouteDTO> routeDTOS = plan.getRoutes().stream()
                .map(routeEntity -> RouteDTO.builder()
                        .routeId(routeEntity.getRouteId())
                        .startTime(routeEntity.getStartTime())
                        .endTime(routeEntity.getEndTime())
                        .price(routeEntity.getPrice())
                        .transportation(TransportationDTO.builder()
                                .id(routeEntity.getTransportationType().getId())
                                .type(routeEntity.getTransportationType().getType())
                                .build())
                        .distance(routeEntity.getDistance())
                        .travelTime(routeEntity.getTravelTime())
                        .build())
                .collect(Collectors.toSet());

        return PlanViewDTO.builder()
                .planId(plan.getPlanId())
                .title(plan.getTitle())
                .places(placeDTOS)
                .routes(routeDTOS)
                .themes(themeDTOS)
                .location(plan.getLocation())
                .startDate(plan.getStartDate())
                .endDate(plan.getEndDate())
                .build();
    }
}
