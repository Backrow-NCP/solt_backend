package org.backrow.solt.service.plan.recommendation;

import org.backrow.solt.dto.plan.PlaceDTO;

import java.util.*;

public class PlaceMerge {

    public Set<PlaceDTO> mergePlaces(List<PlaceDTO> inputPlaces, List<PlaceDTO> recommendedPlaces) {
        Map<String, PlaceDTO> uniquePlacesMap = new HashMap<>();

        inputPlaces.forEach(place -> uniquePlacesMap.put(place.getPlaceName(), place));
        recommendedPlaces.forEach(place -> {
            uniquePlacesMap.merge(place.getPlaceName(), place, this::mergePlaceDetails);
        });

        return new HashSet<>(uniquePlacesMap.values());
    }

    private PlaceDTO mergePlaceDetails(PlaceDTO existing, PlaceDTO recommended) {
        existing.setPrice(existing.getPrice() != null ? existing.getPrice() : recommended.getPrice());
        existing.setDescription(existing.getDescription() != null ? existing.getDescription() : recommended.getDescription());
        existing.setCategory(existing.getCategory() != null ? existing.getCategory() : recommended.getCategory());
        existing.setStartTime(existing.getStartTime() != null ? existing.getStartTime() : recommended.getStartTime());
        existing.setEndTime(existing.getEndTime() != null ? existing.getEndTime() : recommended.getEndTime());
        existing.setChecker(true);
        return existing;
    }
}
