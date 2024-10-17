package org.backrow.solt.domain.plan.api;

import lombok.Data;
import java.util.List;

@Data
public class RoutesResponses {

    private String status;
    private List<Route> routes;

    @Data
    public static class Route {
        private List<Leg> legs;

        @Data
        public static class Leg {
            private Distance distance;
            private Duration duration;
            private Fare fare; // Add fare information
            private List<Step> steps; // Add steps to include transit details
        }

        @Data
        public static class Step {
            private TravelMode travelMode;
            private TransitDetails transitDetails; // Add transit details
        }

        @Data
        public static class TravelMode {
            private String mode;
        }

        @Data
        public static class TransitDetails {
            private String arrivalStop;
            private String departureStop;
            private String line; // Details like bus/train line
            private String vehicleType; // Type of vehicle like BUS or TRAIN
        }
    }

    @Data
    public static class Distance {
        private int value;
    }

    @Data
    public static class Duration {
        private int value;
    }

    @Data
    public static class Fare {
        private int value; // Fare price
    }
}
