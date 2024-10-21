package org.backrow.solt.service.plan;

import org.backrow.solt.domain.plan.api.DirectionsResponses;

import java.util.List;

public class TransportationUtil {

    // 이동 수단 ID를 가져오는 메소드
    public static Integer getTransportationId(List<DirectionsResponses.Route.Step> steps) {
        if (!steps.isEmpty()) {
            String mode = steps.get(0).getTravelMode().getMode();
            if ("TRANSIT".equalsIgnoreCase(mode)) {
                return 2;
            } else if ("WALKING".equalsIgnoreCase(mode)) {
                return 1;
            }
        }
        return 0;
    }

    // 이동 수단 타입을 가져오는 메소드
    public static String getTransportationType(List<DirectionsResponses.Route.Step> steps) {
        if (!steps.isEmpty()) {
            String mode = steps.get(0).getTravelMode().getMode();
            if ("TRANSIT".equalsIgnoreCase(mode)) {
                DirectionsResponses.Route.Step step = steps.get(0);
                // transitDetails에서 추가 정보를 가져올 수 있습니다.
                if (step.getTransitDetails() != null) {
                    return String.format("대중교통 (%s, %s)",
                            step.getTransitDetails().getLine(),
                            step.getTransitDetails().getVehicleType());
                }
                return "대중교통";
            } else if ("WALKING".equalsIgnoreCase(mode)) {
                return "도보";
            }
        }
        return "알 수 없음";
    }
}
