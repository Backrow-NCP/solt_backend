package org.backrow.solt.service.plan.recommendation;

import org.backrow.solt.domain.plan.api.DirectionsResponses;
import org.backrow.solt.dto.plan.PlaceDTO;
import org.backrow.solt.dto.plan.PlanInputDTO;
import org.backrow.solt.dto.plan.RouteDTO;
import org.backrow.solt.dto.plan.TransportationDTO;
import org.backrow.solt.service.ai.GoogleMapsApiService;
import org.backrow.solt.service.plan.TransportationUtil;

import java.util.ArrayList;
import java.util.List;

public class RouteCalculation {

    private final GoogleMapsApiService googleMapsApiService;

    public RouteCalculation(GoogleMapsApiService googleMapsApiService) {
        this.googleMapsApiService = googleMapsApiService;
    }

    public List<RouteDTO> calculateRoutes(List<PlaceDTO> places, PlaceDTO accomodation, PlaceDTO airport) {
        List<RouteDTO> calculateRoutes = new ArrayList<>();

        // 장소 간 경로 계산 로직
        for(int i = 0; i < places.size(); i++) {
            PlaceDTO startPlace = places.get(i);
            PlaceDTO endPlace = places.get(i+1);
            RouteDTO route = createRoute(startPlace, endPlace);
            if(route!=null) calculateRoutes.add(route);
        }

        // 공항 경로 추가
        if(airport != null && !places.isEmpty()) {
            PlaceDTO lastPlace = places.get(places.size()-1);
            RouteDTO airportRoute = createRoute(lastPlace, airport);
            if(airportRoute!=null) calculateRoutes.add(airportRoute);
        }

        return calculateRoutes;
    }

    private RouteDTO createRoute(PlaceDTO startPlace, PlaceDTO endPlace) {
        try{
            DirectionsResponses directions = googleMapsApiService.getDirections(startPlace.getAddr(), endPlace.getAddr());
            if(directions.getRoutes() == null || directions.getRoutes().isEmpty()) return null;

            TransportationUtil.TransportationResult transportationResult = TransportationUtil.getTransportationInfo(directions.getRoutes().get(0).getLegs());
            TransportationDTO transportation = TransportationDTO.builder()
                    .id(transportationResult.getTransportationId())
                    .type(transportationResult.getTransportationType())
                    .build();

            int price = "대중교통".equals(transportation.getType()) ? 3000 : 0;

            return RouteDTO.builder()
                    .startTime(startPlace.getEndTime())
                    .endTime(endPlace.getStartTime())
                    .distance(transportationResult.getDistance())
                    .travelTime(transportationResult.getTravelTime())
                    .price(price)
                    .transportation(transportation)
                    .checker(true)
                    .build();
        } catch (Exception e) {
            return null;
        }
    }
}
