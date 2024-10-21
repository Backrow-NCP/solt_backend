package org.backrow.solt.service.plan;

import org.backrow.solt.domain.plan.api.DirectionsResponses;

import java.util.List;

public class TransportationUtil {

    // 이동 수단 ID를 가져오는 메소드
    public static Integer getTransportationId(List<DirectionsResponses.Route.Step> steps) {
        if (!steps.isEmpty()) {
            String mode = steps.get(0).getTravelMode();
            if ("TRANSIT".equalsIgnoreCase(mode)) {
                return 2; // 대중교통 ID
            } else if ("WALKING".equalsIgnoreCase(mode)) {
                return 1; // 도보 ID
            }
        }
        return 0; // 알 수 없는 ID
    }

    // 이동 수단 타입을 가져오는 메소드
    public static String getTransportationType(List<DirectionsResponses.Route.Step> steps) {
        if (!steps.isEmpty()) {
            String mode = steps.get(0).getTravelMode();
            if ("TRANSIT".equalsIgnoreCase(mode)) {
                DirectionsResponses.Route.Step step = steps.get(0);
                if (step.getTransitDetails() != null) {
                    DirectionsResponses.Route.Step.TransitDetails transitDetails = step.getTransitDetails();
                    String lineName = transitDetails.getLine().getName();
                    String vehicleType = transitDetails.getLine().getVehicle().getName();
                    return String.format("대중교통 (%s, %s)", lineName, vehicleType);
                }
                return "대중교통";
            } else if ("WALKING".equalsIgnoreCase(mode)) {
                return "도보";
            }
        }
        return "알 수 없음";
    }
}
