package org.backrow.solt.service.plan;

import org.backrow.solt.domain.plan.api.DirectionsResponses;

import java.util.List;

public class TransportationUtil {

    // 이동 수단 ID를 가장 긴 거리를 기준으로 가져오는 메소드
    public static Integer getTransportationId(List<DirectionsResponses.Route.Step> steps) {
        if (steps.isEmpty()) {
            return 1; // 기본값: 도보 ID
        }

        // 가장 긴 거리를 찾기 위한 변수
        DirectionsResponses.Route.Step longestStep = steps.get(0);

        // 가장 긴 거리를 찾는 로직
        for (DirectionsResponses.Route.Step step : steps) {
            if (step.getDistance().getValue() > longestStep.getDistance().getValue()) {
                longestStep = step;
            }
        }

        // 가장 긴 거리를 간 이동 수단에 따라 ID 반환
        String mode = longestStep.getTravelMode();
        if ("TRANSIT".equalsIgnoreCase(mode)) {
            return 2; // 대중교통 ID
        } else if ("WALKING".equalsIgnoreCase(mode)) {
            return 1; // 도보 ID
        }

        // 기본값: 도보 ID
        return 1;
    }

    // 이동 수단 타입을 가장 긴 거리를 기준으로 가져오는 메소드
    public static String getTransportationType(List<DirectionsResponses.Route.Step> steps) {
        if (steps.isEmpty()) {
            return "도보"; // 기본값: 도보
        }

        // 가장 긴 거리를 찾기 위한 변수
        DirectionsResponses.Route.Step longestStep = steps.get(0);

        // 가장 긴 거리를 찾는 로직
        for (DirectionsResponses.Route.Step step : steps) {
            if (step.getDistance().getValue() > longestStep.getDistance().getValue()) {
                longestStep = step;
            }
        }

        // 가장 긴 거리를 간 이동 수단에 따라 타입 반환
        String mode = longestStep.getTravelMode();
        if ("TRANSIT".equalsIgnoreCase(mode)) {
            DirectionsResponses.Route.Step.TransitDetails transitDetails = longestStep.getTransitDetails();
            if (transitDetails != null) {
                String lineName = transitDetails.getLine().getName();
                String vehicleType = transitDetails.getLine().getVehicle().getName();
                return String.format("대중교통 (%s, %s)", lineName, vehicleType);
            }
            return "대중교통"; // 대중교통이지만 추가 정보가 없는 경우
        } else if ("WALKING".equalsIgnoreCase(mode)) {
            return "도보"; // 도보인 경우
        }

        // 기본값: 도보
        return "도보";
    }
}
