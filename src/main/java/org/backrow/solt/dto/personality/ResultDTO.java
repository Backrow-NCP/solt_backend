package org.backrow.solt.dto.personality;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResultDTO {
    private int resultId;
//    private int personalityTestId; // 외래키 ID 추가
    private String result;
    private String seasoning;
    private String explainSeasoning;
    private String summary;
    private String description;
    private String recommendation;
    private List<SpotDTO> spots;
//    private int matchSeasoning;
//    private int misMatchSeasoning;
    private String image;

    private PersonalityMatchDTO MatchPersonality;
    private PersonalityMatchDTO misMatchPersonality;



}
