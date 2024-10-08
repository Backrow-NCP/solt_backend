package org.backrow.solt.repository.search;

import com.querydsl.jpa.JPQLQuery;
import org.backrow.solt.domain.*;
import org.backrow.solt.domain.plan.Plan;
import org.backrow.solt.domain.plan.QPlace;
import org.backrow.solt.domain.plan.QPlan;
import org.backrow.solt.domain.plan.QRoute;
import org.backrow.solt.dto.member.MemberInfoDTO;
import org.backrow.solt.dto.plan.PlaceDTO;
import org.backrow.solt.dto.plan.PlanViewDTO;
import org.backrow.solt.dto.plan.RouteDTO;
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
    public Page<PlanViewDTO> searchPlanView(String[] types, String keyword, Pageable pageable) {
        QPlan plan = QPlan.plan;
        QPlace place = QPlace.place;
        QRoute route = QRoute.route;
        QMember member = QMember.member;

        JPQLQuery<Plan> planQuery = from(plan)
                .leftJoin(plan.places, place).fetchJoin()
                .leftJoin(plan.routes, route).fetchJoin()
                .leftJoin(plan.member, member).fetchJoin()
                .groupBy(plan);

        if (keyword != null) {
            planQuery.where(QPlan.plan.title.containsIgnoreCase(keyword));
        }

        Objects.requireNonNull(this.getQuerydsl()).applyPagination(pageable, planQuery);
        List<Plan> planEntities = planQuery.fetch();
        List<PlanViewDTO> planViewDTOS = planEntities.stream()
                .map(this::createPlanViewDTO)
                .collect(Collectors.toList());
        long listCount = planViewDTOS.size();

        return new PageImpl<>(planViewDTOS, pageable, listCount);
    }

    @Override
    @Transactional
    public PlanViewDTO searchPlanView(Long planId) {
        QPlan plan = QPlan.plan;
        QPlace place = QPlace.place;
        QRoute route = QRoute.route;
        QMember member = QMember.member;

        JPQLQuery<Plan> planQuery = from(plan)
                .leftJoin(plan.places, place).fetchJoin()
                .leftJoin(plan.routes, route).fetchJoin()
                .leftJoin(plan.member, member).fetchJoin()
                .where(plan.planId.eq(planId))
                .groupBy(plan);

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
                        .transportationId(routeEntity.getTransportationType().getId())
                        .distance(routeEntity.getDistance())
                        .travelTime(routeEntity.getTravelTime())
                        .build())
                .collect(Collectors.toSet());

        return PlanViewDTO.builder()
                .planId(plan.getPlanId())
                .title(plan.getTitle())
                .member(memberInfoDTO)
                .places(placeDTOS)
                .routes(routeDTOS)
                .regDate(plan.getRegDate())
                .modDate(plan.getModDate())
                .build();
    }
}
