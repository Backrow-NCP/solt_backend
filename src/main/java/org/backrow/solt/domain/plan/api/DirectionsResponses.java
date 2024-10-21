package org.backrow.solt.domain.plan.api;

import lombok.Data;
import org.backrow.solt.dto.plan.PlaceDTO;

import java.util.List;

@Data
public class DirectionsResponses {

    private String status;
    private List<GeocodedWaypoint> geocodedWaypoints; // geocoded_waypoints
    private List<Route> routes;
    private List<PlaceDTO> places;

    @Data
    public static class GeocodedWaypoint {
        private String geocoderStatus; // geocoder_status
        private boolean partialMatch; // partial_match
        private String placeId; // place_id
        private List<String> types; // types
    }

    @Data
    public static class Route {
        private Bounds bounds; // 경로 경계
        private String copyrights; // 저작권 정보
        private List<Leg> legs; // legs 리스트
        private OverviewPolyline overviewPolyline; // 전체 경로의 폴리라인
        private String summary; // 요약 정보
        private List<String> warnings; // 경고 메시지
        private List<Integer> waypointOrder; // waypoints의 순서

        @Data
        public static class Bounds {
            private LatLng northeast; // 경로 북동쪽 경계
            private LatLng southwest; // 경로 남서쪽 경계
        }

        @Data
        public static class LatLng { // latitude와 longitude를 저장하는 클래스
            private double lat;
            private double lng;
        }

        @Data
        public static class Leg {
            private Time arrivalTime; // 도착 시간
            private Time departureTime; // 출발 시간
            private Distance distance; // 거리
            private Duration duration; // 소요 시간
            private String endAddress; // 도착지 주소
            private LatLng endLocation; // 도착지 좌표
            private String startAddress; // 출발지 주소
            private LatLng startLocation; // 출발지 좌표
            private List<Step> steps; // 경로 세부 정보
            private List<Object> trafficSpeedEntry; // 교통 속도 정보 (현재 필요 없음)
            private List<Object> viaWaypoint; // 경유지 정보 (현재 필요 없음)
        }

        @Data
        public static class Time {
            private String text; // 시간 텍스트
            private String timeZone; // 시간대
            private long value; // 유닉스 타임스탬프 값
        }

        @Data
        public static class Distance {
            private String text; // 거리 텍스트
            private int value; // 거리 값 (미터 단위)
        }

        @Data
        public static class Duration {
            private String text; // 소요 시간 텍스트
            private int value; // 소요 시간 값 (초 단위)
        }

        @Data
        public static class Step {
            private Distance distance; // 거리
            private Duration duration; // 소요 시간
            private LatLng endLocation; // 세부 경로의 도착지
            private String htmlInstructions; // 안내 텍스트 (HTML 형식)
            private Polyline polyline; // 경로의 폴리라인 정보
            private LatLng startLocation; // 세부 경로의 출발지
            private List<Step> steps; // 하위 세부 경로 (복합 경로일 경우)
            private String travelMode; // 이동 모드 (WALKING, TRANSIT 등)
            private TransitDetails transitDetails; // 대중교통 세부 정보

            @Data
            public static class Polyline {
                private String points; // 폴리라인 포인트
            }

            @Data
            public static class TransitDetails {
                private Stop arrivalStop; // 도착 정류장
                private Time arrivalTime; // 도착 시간
                private Stop departureStop; // 출발 정류장
                private Time departureTime; // 출발 시간
                private String headsign; // 표지판 정보
                private Line line; // 노선 정보
                private int numStops; // 정류장 수

                @Data
                public static class Stop {
                    private LatLng location; // 정류장 좌표
                    private String name; // 정류장 이름
                }

                @Data
                public static class Line {
                    private List<Agency> agencies; // 운영 기관
                    private String color; // 노선 색상
                    private String name; // 노선 이름
                    private String shortName; // 노선 짧은 이름
                    private String textColor; // 텍스트 색상
                    private Vehicle vehicle; // 차량 정보

                    @Data
                    public static class Agency {
                        private String name; // 기관 이름
                        private String url; // 기관 URL
                    }

                    @Data
                    public static class Vehicle {
                        private String icon; // 차량 아이콘
                        private String name; // 차량 이름 (예: Subway)
                        private String type; // 차량 타입 (예: SUBWAY)
                    }
                }
            }
        }

        @Data
        public static class OverviewPolyline {
            private String points; // 전체 경로의 폴리라인 포인트
        }
    }
}
