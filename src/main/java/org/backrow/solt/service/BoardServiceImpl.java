package org.backrow.solt.service;

import lombok.RequiredArgsConstructor;
import org.backrow.solt.domain.board.*;
import org.backrow.solt.domain.Member;
import org.backrow.solt.domain.plan.Plan;
import org.backrow.solt.dto.board.BoardImageDTO;
import org.backrow.solt.dto.board.BoardModifyDTO;
import org.backrow.solt.dto.page.PageRequestDTO;
import org.backrow.solt.dto.page.PageResponseDTO;
import org.backrow.solt.dto.board.BoardInputDTO;
import org.backrow.solt.dto.board.BoardViewDTO;
import org.backrow.solt.repository.BoardRepository;
import org.backrow.solt.repository.PlanRepository;
import org.modelmapper.ModelMapper;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.webjars.NotFoundException;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {
    private final BoardRepository boardRepository;
    private final PlanRepository planRepository;
    private final ModelMapper modelMapper;

    @Override
    public PageResponseDTO<BoardViewDTO> getBoardList(PageRequestDTO pageRequestDTO) {
        String[] types = pageRequestDTO.getTypes();
        String keyword = pageRequestDTO.getKeyword();
        Pageable pageable = pageRequestDTO.getPageable();

        Page<BoardViewDTO> boardPage = boardRepository.searchBoardViewWithBoardPlan(types, keyword, pageable);

        return new PageResponseDTO<>(pageRequestDTO, boardPage.getContent(), (int) boardPage.getTotalElements());
    }

    @Override
    public PageResponseDTO<BoardViewDTO> getBoardListByMemberId(Long id, PageRequestDTO pageRequestDTO) {
        String[] types = pageRequestDTO.getTypes();
        String keyword = pageRequestDTO.getKeyword();
        Pageable pageable = pageRequestDTO.getPageable();

        Page<BoardViewDTO> boardPage = boardRepository.searchBoardViewByMemberIdWithBoardPlan(id, types, keyword, pageable);

        return new PageResponseDTO<>(pageRequestDTO, boardPage.getContent(), (int) boardPage.getTotalElements());
    }

    @Override
    public BoardViewDTO getBoard(Long id) {
        BoardViewDTO result = boardRepository.searchBoardViewWithBoardPlan(id);
        if (result == null) {
            throw new NotFoundException("Board not found: " + id);
        }
        return result;
    }

    @Transactional
    @Override
    public long saveBoard(BoardInputDTO boardInputDTO, Long memberId) {
        boardInputDTO.setMemberId(memberId);

        Board board = convertToEntity(boardInputDTO);
        setBoardImages(board);

        // 게시글 작성한 뒤에 Plan을 지우면 BoardPlan에서 Theme를 불러오지 못하는 문제가 발생할 수 있음
        // 그렇다고 BoardPlan 만들 때 Theme까지 복제하자니 쿼리를 너무 많이 생성하는 것 같은데...;;;; 어쩌지
        Optional<Plan> findPlan = planRepository.findById(boardInputDTO.getPlanId()); // 여기서 Plan, Place, Route를 한 번의 쿼리로 가져오게 하면 더 효율적일 듯 (지금은 쿼리 3번 날림)
        Plan plan = findPlan.orElseThrow(() -> new NotFoundException("Plan not found: " + boardInputDTO.getPlanId()));
        board.setBoardPlan(planToBoardPlan(plan));

        boardRepository.save(board);
        return board.getBoardId();
    }

    @Transactional
    @Override
    public boolean modifyBoard(Long boardId, BoardModifyDTO boardModifyDTO, Long memberId) {
        Optional<Board> findBoard = boardRepository.findById(boardId);
        Board board = findBoard.orElseThrow(() -> new NotFoundException("Board not found: " + boardId));
        if (!Objects.equals(board.getMember().getMemberId(), memberId))
            throw new AccessDeniedException("You do not have permission to modify this post.");

        Set<BoardImageDTO> boardImageDTOS = boardModifyDTO.getBoardImages();
        Set<BoardImage> boardImages = null;
        if (boardImageDTOS != null) {
            boardImages = boardImageDTOS.stream()
                    .map((boardImageDTO -> modelMapper.map(boardImageDTO, BoardImage.class)))
                    .collect(Collectors.toSet());
        }

        board.modify(boardModifyDTO.getTitle(), boardModifyDTO.getContent(), boardImages);
        setBoardImages(board);

        boardRepository.save(board);
        return true;
    }

    @Override
    public boolean deleteBoard(Long boardId, Long memberId) {
        try {
            boardRepository.deleteByBoardIdAndMember_MemberId(boardId, memberId);
            return true;
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Board not found or you do not have permission to delete it.");
        }
    }

    /** Board 내 Image 엔티티에 참조할 Board를 지정합니다. */
    private void setBoardImages(Board board) {
        Set<BoardImage> boardImages = board.getBoardImages();
        if (boardImages != null) {
            boardImages.forEach(boardImage -> boardImage.setBoard(board));
        }
    }

    /** BoardInputDTO를 Board Entity로 매핑합니다. **/
    private Board convertToEntity(BoardInputDTO boardInputDTO) {
        Board board = modelMapper.map(boardInputDTO, Board.class);
        board.setBoardId(null);
        Member member = Member.builder()
                .memberId(boardInputDTO.getMemberId())
                .build();
        board.setMember(member);
        return board;
    }

    /** Plan Entity를 BoardPlan Entity로 매핑합니다. **/
    private BoardPlan planToBoardPlan(Plan plan) {
        if (plan == null) return null;

        BoardPlan boardPlan = BoardPlan.builder()
                .originPlan(plan)
                .title(plan.getTitle())
                .location(plan.getLocation())
                .startDate(plan.getStartDate())
                .endDate(plan.getEndDate())
                .build();

        Set<BoardPlace> places = plan.getPlaces().stream()
                .map(place -> BoardPlace.builder()
                        .boardPlan(boardPlan)
                        .placeName(place.getPlaceName())
                        .price(place.getPrice())
                        .addr(place.getAddr())
                        .startTime(place.getStartTime())
                        .endTime(place.getEndTime())
                        .build())
                .collect(Collectors.toSet());
        boardPlan.setPlaces(places);

        Set<BoardRoute> routes = plan.getRoutes().stream()
                .map(route -> BoardRoute.builder()
                        .boardPlan(boardPlan)
                        .startTime(route.getStartTime())
                        .endTime(route.getEndTime())
                        .price(route.getPrice())
                        .transportationType(route.getTransportationType())
                        .distance(route.getDistance())
                        .travelTime(route.getTravelTime())
                        .build())
                .collect(Collectors.toSet());
        boardPlan.setRoutes(routes);

        return boardPlan;
    }
}
