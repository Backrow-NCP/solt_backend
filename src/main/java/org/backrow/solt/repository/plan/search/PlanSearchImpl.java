package org.backrow.solt.repository.plan.search;

import com.querydsl.jpa.JPQLQuery;
import org.backrow.solt.domain.member.*;
import org.backrow.solt.domain.plan.*;
import org.backrow.solt.dto.member.MemberInfoDTO;
import org.backrow.solt.dto.plan.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class PlanSearchImpl extends QuerydslRepositorySupport implements PlanSearch {
    public PlanSearchImpl() {
        super(Plan.class);
    }

    @Override
    @Transactional
    public Page<PlanViewDTO> searchPlanViewWithMemberId(String[] types, String keyword, Pageable pageable, Long memberId) {
        QPlan plan = QPlan.plan;
        QPlace place = QPlace.place;
        QRoute route = QRoute.route;
        QMember member = QMember.member;
        QThemeLog themeLog = QThemeLog.themeLog;
        QTheme theme = QTheme.theme;

        JPQLQuery<Plan> planQuery = from(plan)
                .leftJoin(plan.places, place).fetchJoin()
                .leftJoin(plan.routes, route).fetchJoin()
                .leftJoin(plan.member, member).fetchJoin()
                .leftJoin(plan.themes, themeLog).fetchJoin()
                .leftJoin(themeLog.theme, theme).fetchJoin()
                .where(member.memberId.eq(memberId))
                .distinct();

        if (keyword != null) {
            planQuery.where(QPlan.plan.title.containsIgnoreCase(keyword));
        }

        long totalCount = planQuery.fetchCount();
        Objects.requireNonNull(this.getQuerydsl()).applyPagination(pageable, planQuery);

        List<Plan> planEntities = planQuery.fetch();
        List<PlanViewDTO> planViewDTOS = planEntities.stream()
                .map(this::createPlanViewDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(planViewDTOS, pageable, totalCount);
    }

    @Override
    @Transactional
    public PlanViewDTO searchPlanView(Long planId) {
        QPlan plan = QPlan.plan;
        QPlace place = QPlace.place;
        QRoute route = QRoute.route;
        QMember member = QMember.member;
        QThemeLog themeLog = QThemeLog.themeLog;
        QTheme theme = QTheme.theme;

        JPQLQuery<Plan> planQuery = from(plan)
                .leftJoin(plan.places, place).fetchJoin()
                .leftJoin(plan.routes, route).fetchJoin()
                .leftJoin(plan.member, member).fetchJoin()
                .leftJoin(plan.themes, themeLog).fetchJoin()
                .leftJoin(themeLog.theme, theme).fetchJoin()
                .where(plan.planId.eq(planId))
                .distinct();

        Plan planEntity = planQuery.fetchOne();

        return createPlanViewDTO(planEntity);
    }

    private PlanViewDTO createPlanViewDTO(Plan plan) {
        if (plan == null) return null;

        MemberInfoDTO memberInfoDTO = MemberInfoDTO.builder()
                .memberId(plan.getMember().getMemberId())
                .name(plan.getMember().getName())
                .build();
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
        Set<ThemeDTO> themeDTOS = plan.getThemes().stream()
                .map(themeLogEntity -> ThemeDTO.builder()
                        .themeId(themeLogEntity.getTheme().getThemeId())
                        .name(themeLogEntity.getTheme().getName())
                        .build())
                .collect(Collectors.toSet());

        return PlanViewDTO.builder()
                .planId(plan.getPlanId())
                .title(plan.getTitle())
                .member(memberInfoDTO)
                .places(placeDTOS)
                .routes(routeDTOS)
                .themes(themeDTOS)
                .location(plan.getLocation())
                .startDate(plan.getStartDate())
                .endDate(plan.getEndDate())
                .regDate(plan.getRegDate())
                .modDate(plan.getModDate())
                .build();
    }
}