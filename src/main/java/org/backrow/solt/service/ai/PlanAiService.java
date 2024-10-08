package org.backrow.solt.service.ai;

import org.backrow.solt.domain.plan.Place;
import org.backrow.solt.dto.plan.PlaceDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import java.util.List;

@Service
public class PlanAiService {

    private final RestTemplate restTemplate;

    public PlanAiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<PlaceDTO> getRecommendedPlaces(String location, String theme) {
        String clovaApiUrl = "input Clova Api Address";
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(clovaApiUrl)
                .queryParam("location", location)
                .queryParam("theme", theme); // 수정해야함

        ResponseEntity<PlaceDTO[]> response = restTemplate.getForEntity(builder.toUriString(), PlaceDTO[].class);

        if(response.getStatusCode() == HttpStatus.OK){
            return Arrays.asList(response.getBody());
        }
        throw new RuntimeException("Clova API로 부터 위치 정보를 받아오는데 실패하였습니다");

    }
}
