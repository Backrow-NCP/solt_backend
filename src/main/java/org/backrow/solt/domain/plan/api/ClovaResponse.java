package org.backrow.solt.domain.plan.api;

import lombok.Data;
import org.backrow.solt.domain.plan.Place;

import java.util.List;

@Data
public class ClovaResponse {
    private List<Place> places;
}
