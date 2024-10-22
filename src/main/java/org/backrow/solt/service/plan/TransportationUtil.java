package org.backrow.solt.service.plan;

import org.backrow.solt.domain.plan.api.DirectionsResponses;

import java.util.List;

public class TransportationUtil {

    // 이동 수단 ID를 대중교통을 한 번이라도 탔는지에 따라 가져오는 메소드
    public static Integer getTransportationId(List<DirectionsResponses.Route.Leg> legs) {
        if (legs.isEmpty()) {
            return 1; // 기본값: 도보 ID
        }

        // 대중교통을 탄 적이 있는지 확인
        boolean hasTransit = false;

        for (DirectionsResponses.Route.Leg leg : legs) {
            for (DirectionsResponses.Route.Step step : leg.getSteps()) {
                String mode = step.getTravelMode();
                if ("TRANSIT".equalsIgnoreCase(mode)) {
                    hasTransit = true;
                    break; // 대중교통을 탔다면 더 이상 확인할 필요 없음
                }
            }
            if (hasTransit) {
                break;
            }
        }

        // 대중교통을 한 번이라도 탔다면 대중교통 ID, 아니면 도보 ID 반환
        return hasTransit ? 2 : 1;
    }

    // 이동 수단 타입을 대중교통을 한 번이라도 탔는지에 따라 가져오는 메소드
    public static String getTransportationType(List<DirectionsResponses.Route.Leg> legs) {
        if (legs.isEmpty()) {
            return "도보"; // 기본값: 도보
        }

        // 대중교통을 탄 적이 있는지 확인
        boolean hasTransit = false;

        for (DirectionsResponses.Route.Leg leg : legs) {
            for (DirectionsResponses.Route.Step step : leg.getSteps()) {
                String mode = step.getTravelMode();
                if ("TRANSIT".equalsIgnoreCase(mode)) {
                    hasTransit = true;
                    break; // 대중교통을 탔다면 더 이상 확인할 필요 없음
                }
            }
            if (hasTransit) {
                break;
            }
        }

        // 대중교통을 한 번이라도 탔다면 대중교통, 아니면 도보 반환
        if (hasTransit) {
            // 가장 처음 만나는 대중교통의 세부 정보를 반환
            for (DirectionsResponses.Route.Leg leg : legs) {
                for (DirectionsResponses.Route.Step step : leg.getSteps()) {
                    if ("TRANSIT".equalsIgnoreCase(step.getTravelMode())) {
                        DirectionsResponses.Route.Step.TransitDetails transitDetails = step.getTransitDetails();
                        if (transitDetails != null) {
                            String lineName = transitDetails.getLine().getName();
                            String vehicleType = transitDetails.getLine().getVehicle().getName();
                            return String.format("대중교통 (%s, %s)", lineName, vehicleType);
                        }
                        return "대중교통"; // 대중교통이지만 추가 정보가 없는 경우
                    }
                }
            }
        }

        // 대중교통을 타지 않았다면 도보 반환
        return "도보";
    }
}
