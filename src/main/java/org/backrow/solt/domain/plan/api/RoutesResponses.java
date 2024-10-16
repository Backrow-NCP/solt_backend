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


}
