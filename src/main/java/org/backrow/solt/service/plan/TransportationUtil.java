package org.backrow.solt.service.plan;

import org.backrow.solt.domain.plan.api.DirectionsResponses;

import java.util.List;

public class TransportationUtil {

    // 이동 수단 ID와 타입을 반환하는 메소드
    public static TransportationResult getTransportationInfo(List<DirectionsResponses.Route.Leg> legs) {
        if (legs.isEmpty()) {
            return new TransportationResult(1, "도보", 0, 0); // 기본값: 도보, 거리와 시간은 0
        }

        // 대중교통을 탄 적이 있는지 확인
        boolean hasTransit = false;
        int totalDistance = 0; // 거리 합계 (미터 단위)
        int totalDuration = 0; // 시간 합계 (초 단위)

        for (DirectionsResponses.Route.Leg leg : legs) {
            totalDistance += leg.getDistance().getValue(); // 각 구간의 거리를 합산
            totalDuration += leg.getDuration().getValue(); // 각 구간의 시간을 합산

            for (DirectionsResponses.Route.Step step : leg.getSteps()) {
                String mode = step.getTravelMode();
                if ("TRANSIT".equalsIgnoreCase(mode)) {
                    hasTransit = true;
                    break; // 대중교통을 탔다면 더 이상 확인할 필요 없음
                }
            }
        }

        // 거리와 시간을 킬로미터와 분으로 변환
        int distanceInKm = (int) Math.round(totalDistance / 1000.0);
        int travelTimeInMin = totalDuration / 60;

        // 이동 수단 ID와 타입 설정
        int transportationId;
        String transportationType;

        if (hasTransit || distanceInKm >= 2) {
            transportationId = 2; // 대중교통 ID
            transportationType = "대중교통"; // 대중교통 타입
        } else {
            transportationId = 1; // 도보 ID
            transportationType = "도보"; // 도보 타입
        }

        return new TransportationResult(transportationId, transportationType, distanceInKm, travelTimeInMin);
    }

    // 결과를 담을 클래스
    public static class TransportationResult {
        private final Integer transportationId;
        private final String transportationType;
        private final Integer distance; // km 단위
        private final Integer travelTime; // 분 단위

        public TransportationResult(Integer transportationId, String transportationType, Integer distance, Integer travelTime) {
            this.transportationId = transportationId;
            this.transportationType = transportationType;
            this.distance = distance;
            this.travelTime = travelTime;
        }

        public Integer getTransportationId() {
            return transportationId;
        }

        public String getTransportationType() {
            return transportationType;
        }

        public Integer getDistance() {
            return distance;
        }

        public Integer getTravelTime() {
            return travelTime;
        }
    }
}
