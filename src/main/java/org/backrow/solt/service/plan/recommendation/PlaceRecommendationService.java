package org.backrow.solt.service.plan.recommendation;

import org.backrow.solt.domain.plan.api.PlacesResponses;
import org.backrow.solt.dto.plan.PlaceDTO;
import org.backrow.solt.dto.plan.PlanInputDTO;
import org.backrow.solt.service.ai.ClovaApiService;

import java.util.List;
import java.util.stream.Collectors;

public class PlaceRecommendationService {

    private final ClovaApiService clovaApiService;

    public PlaceRecommendationService(ClovaApiService clovaApiService) {
        this.clovaApiService = clovaApiService;
    }

    public List<PlaceDTO> recommendPlaces(PlanInputDTO planInputDTO) {
        List<PlacesResponses> clovaPlaces = clovaApiService.callClovaApi(planInputDTO);

        return clovaPlaces.stream()
                .map(response -> PlaceDTO.builder()
                        .placeName(response.getPlaceName())
                        .addr(response.getAddr())
                        .price(response.getPrice())
                        .startTime(response.getStartTime())
                        .endTime(response.getEndTime())
                        .description(response.getDescription())
                        .category(response.getCategory())
                        .checker(response.isChecker())
                        .build())
                .collect(Collectors.toList());
    }
}
